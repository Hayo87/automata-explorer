package io.github.Hayo87.domain.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.jgrapht.alg.util.UnionFind;

import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.github.tno.gltsdiff.operators.combiners.Combiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffAutomatonStatePropertyCombiner;
import com.github.tno.gltsdiff.operators.combiners.lts.automaton.diff.DiffPropertyCombiner;
import com.github.tno.gltsdiff.scorers.SimilarityScorer;
import com.github.tno.gltsdiff.scorers.WalkinshawLocalScorer;

import io.github.Hayo87.dto.AnalysisDTO;
import io.github.Hayo87.dto.BuildDTO;

/**
 * Analyzes DiffAutomaton to identify interesting patterns like divergence. The class uses scoring to find 
 * related (twin) states and uses a matcher to determine which transitions cause the divergence between the pair or group. 
 *
 */
public class Analyzer<T> {

    private final SimilarityScorer scorer;
    private final Combiner<DiffProperty<T>> propertyCombiner;

    public Analyzer(Combiner<T> combiner){
        this.scorer = new WalkinshawLocalScorer(new DiffAutomatonStatePropertyCombiner(), new DiffPropertyCombiner<>(combiner));
        this.propertyCombiner = new DiffPropertyCombiner<>(combiner);
    }

    /**
     * Analyzes the difference machine to find relevant differences for (a group of) pairs by identifying root causes for divergence.
     *   
     * @param diffAutomaton
     * @return {@link AnalysisDTO}
     */
    public AnalysisDTO analyze(DiffAutomaton<T> diffAutomaton) {
        var twins = findTwins(diffAutomaton);

        // For all twins find cause
        List<AnalysisDTO.TwinAnalysis> result = new ArrayList<>(); 
        for (Pair<Integer, Integer> twin : twins) {
            Set<BuildDTO.Edge> causes = findCauses(diffAutomaton, twin.getLeft(), twin.getRight());
            result.add(new AnalysisDTO.TwinAnalysis(twin.getLeft(), twin.getRight(), causes));
        }

        // Return result 
        return new AnalysisDTO(result, groupTwins(result));    
    }
 
    /**
     * Find twins for added and removed nodes based on the WalkinshawLocalScorer.
     * 
     * @param diffAutomaton the input for the scoring and twin detection. 
     * @return a set of twin pairs with the State Id. 
     */
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
            .map(State::getId)
            .collect(Collectors.toSet());

        // Filter the matrix scores
        for(Integer nodeId: nodes) {
            RealVector rowVector = matrix.getRowVector(nodeId);
            rowVector.setEntry(nodeId, 0);
            int bestIndex = rowVector.getMaxIndex();
            twins.add(Pair.of(Math.min(nodeId, bestIndex), Math.max(nodeId, nodeId)));
        }
        return twins; 
    }

    /**
     * Find possible causes of divergence between the twins states by filtering relevant transitions
     * @param diffAutomaton
     * @param left  Id left twin
     * @param right id right twin
     * @return as set of transitions explaining the divergence between twin states 
     */
    private Set<BuildDTO.Edge> findCauses(DiffAutomaton<T> diffAutomaton, Integer left, Integer right){
        Set<BuildDTO.Edge> result = new HashSet<>();
        State<DiffAutomatonStateProperty> leftState = diffAutomaton.getStateById(left);
        State<DiffAutomatonStateProperty> rightState = diffAutomaton.getStateById(right);
        DiffKind twinDiff = (leftState.getProperty().getStateDiffKind() != DiffKind.UNCHANGED)? 
                                leftState.getProperty().getStateDiffKind() : rightState.getProperty().getStateDiffKind();

        // Only outgoing transitions are relevant
        Set<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> transitions = diffAutomaton.getTransitions(t ->
            (t.getSource().equals(leftState) || t.getSource().equals(rightState)));

        // Apply filtering based on allow transition matching conditions
        for (var t1 : transitions) { 
                if(t1.getProperty().getDiffKind() == twinDiff) {
                boolean match = transitions.stream().anyMatch(t2 -> isMatch(t1, t2));
                if (!match) result.add(
                    new BuildDTO.Edge(t1.getProperty().getProperty().toString(), t1.getSource().getId(), t1.getTarget().getId(),null)); 
                }   
            }       
        return result;
    }

    /*
     * Helper function that contains the conditions under which transitions are allow to be matched on property. 
     */
    private boolean isMatch(Transition<DiffAutomatonStateProperty, DiffProperty<T>> t1, Transition<DiffAutomatonStateProperty, DiffProperty<T>> t2){
        return (t1 != t2)   && (
               (t1.getTarget().getId() == t1.getSource().getId() && t2.getTarget().getId() == t2.getSource().getId())   // loop rule
            || (t1.getTarget().getId() == t2.getTarget().getId())                                                       // same target rule) 
        ) && propertyCombiner.areCombinable(t1.getProperty(), t2.getProperty());
    }

    /**
     * Helper method to group all twins in groups if they overlap
     * @param twins
     * @return List of {@link GroupedTwinAnalysisDTO}
     */
    private List<AnalysisDTO.GroupedTwinAnalysis> groupTwins(List<AnalysisDTO.TwinAnalysis> twins) {
        Set<Integer> ids = twins.stream().flatMap(
            dto -> Stream.of(dto.left(), dto.right()))
            .collect(Collectors.toSet());
        
        UnionFind<Integer> uf = new UnionFind<>(ids);
        twins.forEach(dto -> uf.union(dto.left(), dto.right()));

        Map<Integer, Set<Integer>> members = new HashMap<>();
        Map<Integer, Set<BuildDTO.Edge>> causes = new HashMap<>();

        for(AnalysisDTO.TwinAnalysis dto: twins){
            int root = uf.find(dto.left());

            members.computeIfAbsent(root, r -> new HashSet<>()).add(dto.left());
            members.get(root).add(dto.right());
            causes.computeIfAbsent(root, r ->  new HashSet<>()).addAll(dto.causes());
        }
        
        return members.entrySet().stream()
            .map(e -> new AnalysisDTO.GroupedTwinAnalysis(e.getValue(), causes.getOrDefault(e.getKey(), Set.of())))
            .toList();
    }
}
