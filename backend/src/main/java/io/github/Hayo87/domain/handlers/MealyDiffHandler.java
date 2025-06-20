package io.github.Hayo87.domain.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.State;
import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.AutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.domain.analyzer.Analyzer;
import io.github.Hayo87.domain.model.Mealy;
import io.github.Hayo87.domain.model.MealyCombiner;
import io.github.Hayo87.domain.model.MergedMealy;
import io.github.Hayo87.domain.processors.DiffAutomatonProcessor;
import io.github.Hayo87.domain.rules.LabelUtils;
import io.github.Hayo87.domain.rules.ProcessingModel.Stage;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;
import io.github.Hayo87.dto.AnalysisDTO;
import io.github.Hayo87.dto.BuildDTO;
import io.github.Hayo87.dto.BuildDTO.LabelEntry;
import io.github.Hayo87.dto.BuildDTO.LabelType;
import io.github.Hayo87.dto.ProcessingActionDTO;

@Component
public class MealyDiffHandler extends  AbstractDiffHandler<Mealy> {

    public MealyDiffHandler(List<DiffAutomatonProcessor<Mealy>> actions) {
        super(actions);
    }

    @Override
    public DiffAutomaton<Mealy> convertInternal(Automaton<String> input, boolean isReference) {
        DiffAutomaton<Mealy> automaton = new DiffAutomaton<>();
        DiffKind diffKind = isReference ? DiffKind.REMOVED : DiffKind.ADDED;
        
        Map<Integer, State<DiffAutomatonStateProperty>> stateMap = new HashMap<>();
        
        // Process states
        for (State<AutomatonStateProperty> s: input.getStates()) {
            int stateId = s.getId();
            boolean isStart = s.getProperty().isInitial();

            State<DiffAutomatonStateProperty> state = automaton.addState(
                new DiffAutomatonStateProperty(isStart, diffKind, Optional.empty())
            );
            stateMap.put(stateId, state); 
        }

        // Process transitions
        for (Transition<AutomatonStateProperty, String> t: input.getTransitions()) {
            int source = t.getSource().getId();
            int target = t.getTarget().getId();
            String label = t.getProperty();
            
            String inputLabel = LabelUtils.extractInput(label);
            String outputLabel = LabelUtils.extractOutput(label);
    
            automaton.addTransition(
                new Transition<>(
                    stateMap.get(source),
                    new DiffProperty<>(new Mealy(inputLabel, outputLabel), diffKind),
                    stateMap.get(target)
                )
            );
        }
        return automaton; 
    }

    @Override
    public DiffAutomaton<Mealy> buildInternal(DiffAutomaton<Mealy> reference, DiffAutomaton<Mealy> subject) {
        DiffAutomatonStructureComparatorBuilder<Mealy> builder = new DiffAutomatonStructureComparatorBuilder<>();
        builder.setRewriters(Collections.emptyList());
        builder.setDiffAutomatonTransitionPropertyCombiner(new MealyCombiner());
        var comparator = builder.createComparator();

        // First level matching
        var result = comparator.compare(reference, subject);
          
        // Second level matching(merger)
        List<ProcessingActionDTO> action = List.of(new ProcessingActionDTO(Stage.POST, Type.MERGER, SubType.INPUT, 0, null, null));

        return applyProcessing(result, action);
    }
 
    @Override
    public BuildDTO serializeInternal(DiffAutomaton<Mealy> automaton) {

        // Serialize nodes
        List<BuildDTO.Node> nodes = automaton.getStates().stream()
            .map(state -> new BuildDTO.Node(
                state.getId(), 
                new BuildDTO.NodeAttributes(
                    ("S" + state.getId()),
                    state.getProperty().isInitial(), 
                    state.getProperty().getStateDiffKind().toString()
                )
            ))
            .toList();

        // Serialize edges    
        AtomicInteger counter = new AtomicInteger(nodes.size() -1);
        List<BuildDTO.Edge> edges = automaton.getTransitions().stream()
            .map(transition -> {
                int edgeId = counter.incrementAndGet();
                Mealy mealy = transition.getProperty().getProperty();
                
                return new BuildDTO.Edge(
                String.valueOf(edgeId), 
                transition.getSource().getId(), 
                transition.getTarget().getId(), 
                new BuildDTO.EdgeAttributes(
                    (mealy instanceof MergedMealy)? "COMBINED": transition.getProperty().getDiffKind().toString(),
                    mealy.toString(), 
                    toLabelEntries(mealy , transition.getProperty().getDiffKind())
                ));
            })           
            .toList();

        return new BuildDTO(nodes, edges); 
    }
    
    /**
     * Helper method to build the label entries.
     * @param mealy the mealy property
     * @param diffkind the diffkind for the property
     * @return list of label entries
     */
    private List<LabelEntry> toLabelEntries(Mealy mealy, DiffKind diffkind){
        if(mealy instanceof MergedMealy merged) {
            return List.of(
                new BuildDTO.LabelEntry(LabelType.INPUT, merged.input(), DiffKind.UNCHANGED.toString()),
                new BuildDTO.LabelEntry(LabelType.OUTPUT, merged.output(), DiffKind.REMOVED.toString()),
                new BuildDTO.LabelEntry(LabelType.OUTPUT, merged.addedOutput(), DiffKind.ADDED.toString())
            );

        } else {
            return List.of(
                new BuildDTO.LabelEntry(LabelType.INPUT, mealy.input(), diffkind.toString()),
                new BuildDTO.LabelEntry(LabelType.OUTPUT, mealy.output(), diffkind.toString())
            );
        }
    }
    
    @Override
    public AnalysisDTO analyze(DiffAutomaton<Mealy> automaton){
        Analyzer<Mealy> analyzer = new Analyzer<>(new MealyCombiner());
        return analyzer.analyze(automaton);
    }
}
