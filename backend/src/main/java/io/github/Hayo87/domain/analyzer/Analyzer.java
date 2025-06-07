package io.github.Hayo87.domain.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.github.tno.gltsdiff.operators.combiners.Combiner;
import com.github.tno.gltsdiff.operators.combiners.TransitionCombiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffAutomatonStatePropertyCombiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffPropertyCombiner;
import com.github.tno.gltsdiff.scorers.SimilarityScorer;
import com.github.tno.gltsdiff.scorers.WalkinshawLocalScorer;

import io.github.Hayo87.dto.AnalysisDTO;
import io.github.Hayo87.dto.CauseDTO;
import io.github.Hayo87.dto.TwinAnalysisDTO;

public class Analyzer<T> {

    private final SimilarityScorer scorer;
    private final Combiner<Transition<DiffAutomatonStateProperty,DiffProperty<T>>> combiner;
    private final Combiner<DiffProperty<T>> propertyCombiner;

    public Analyzer(Combiner<T> combiner){
        this.scorer = new WalkinshawLocalScorer(new DiffAutomatonStatePropertyCombiner(), new DiffPropertyCombiner<>(combiner));
        this.combiner = new TransitionCombiner<>(new DiffPropertyCombiner<>(combiner));
        this.propertyCombiner = new DiffPropertyCombiner<>(combiner);
    }

    public AnalysisDTO analyze(DiffAutomaton<T> diffAutomaton) {
        List<TwinAnalysisDTO> result = new ArrayList<>(); 

        // Find twins
        var twins = findTwins(diffAutomaton);

        // For all twins find cause
        for (Pair<Integer, Integer> twin : twins) {
            Set<CauseDTO> causes = findCauses(diffAutomaton, twin.getLeft(), twin.getRight());
            result.add(new TwinAnalysisDTO(twin.getLeft(), twin.getRight(), causes));
        }
        // Return result 
        return new AnalysisDTO(result);    
    }
 
    private  Set<Pair<Integer, Integer>> findTwins(DiffAutomaton<?> diffAutomaton){
        Set<Pair<Integer, Integer>> twins = new HashSet<>();
        
        // Calculate scores for all node pairs
        RealMatrix matrix = scorer.compute(diffAutomaton, diffAutomaton);
        
        // Get list of added or removed nodes
        Set<Integer> nodes = diffAutomaton.getStates().stream()
            .filter(state -> {
                DiffKind kind = state.getProperty().getStateDiffKind();
                return kind != DiffKind.UNCHANGED;
            })
            .map(state -> state.getId())
            .collect(Collectors.toSet());

        // Filter the matrix scores
        for(Integer nodeId: nodes) {
            RealVector rowVector = matrix.getRowVector(nodeId);
            rowVector.setEntry(nodeId, 0);
            int bestIndex = rowVector.getMaxIndex();
            twins.add(Pair.of(nodeId, bestIndex));
        }
        return twins; 
    }

    private Set<CauseDTO> findCauses(DiffAutomaton<T> diffAutomaton, Integer left, Integer right){
        State<DiffAutomatonStateProperty> twin1 = diffAutomaton.getStateById(left);
        State<DiffAutomatonStateProperty> twin2 = diffAutomaton.getStateById(right);

        Set<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> transitions = new HashSet();
        transitions.addAll(diffAutomaton.getIncomingTransitions(twin1));
        transitions.addAll(diffAutomaton.getOutgoingTransitions(twin1));
        transitions.addAll(diffAutomaton.getIncomingTransitions(twin2));
        transitions.addAll(diffAutomaton.getOutgoingTransitions(twin2));

        Set<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> candidates = findCauseCandidates(transitions);   
        return filterCandidates(left, right, candidates);
    }

    private Set<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> findCauseCandidates(
        Set<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> transitions) {

        Set<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> candidates = new HashSet<>();

        for (var t1 : transitions) {
                boolean match = transitions.stream().anyMatch(t2 -> t1 != t2 && combiner.areCombinable(t1, t2));
                if (!match) candidates.add(t1);
            }
        return candidates;
    }

    private Set<CauseDTO> filterCandidates(int left, int right, Set<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> candidates){
        Set<CauseDTO> result = new HashSet<>();

        // Transitions between twins are never filtered
        for (var t : candidates){
            int source = t.getSource().getId();
            int target = t.getTarget().getId();
            if ( (source == left && target == right) || (source == right && target == left)){
                result.add(new CauseDTO(source, target, t.getProperty().getProperty().toString()));
            }
        }

        // Apply filtering based on proposition
        for (var t1 : candidates) {
            boolean match = candidates.stream().anyMatch(t2 -> 
                t1 != t2 
                && ((t1.getSource().getId() == t1.getTarget().getId() && t2.getSource().getId() == t2.getTarget().getId())  
                    || (t1.getTarget().getId() == t2.getTarget().getId())                                                      
                    || (t1.getSource().getId() == t2.getSource().getId())) 
                && propertyCombiner.areCombinable(t1.getProperty(), t2.getProperty()));
            if (!match && t1.getProperty().getDiffKind() != DiffKind.UNCHANGED) result.add(new CauseDTO(t1.getSource().getId(), t1.getTarget().getId(), t1.getProperty().getProperty().toString()));
            }      
        return result;
    }
}
