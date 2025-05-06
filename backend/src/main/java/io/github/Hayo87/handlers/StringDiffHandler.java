package io.github.Hayo87.handlers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomata;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;

import io.github.Hayo87.dto.BuildDTO;
import io.github.Hayo87.processors.DiffAutomatonProcessor;

@Component
public class StringDiffHandler extends AbstractDiffHandler<String> {

    public StringDiffHandler(List<DiffAutomatonProcessor<String>> filters) {
        super(filters);
    }

    @Override
    public DiffAutomaton<String> convert( Automaton<String> input, boolean isReference) {
        return DiffAutomata.fromAutomaton(input, (isReference ? DiffKind.REMOVED : DiffKind.ADDED));
    }

    @Override
    public DiffAutomaton<String> build(DiffAutomaton<String> reference, DiffAutomaton<String> subject) {
        DiffAutomatonStructureComparatorBuilder<String> builder = new DiffAutomatonStructureComparatorBuilder<>();
        //builder.setDiffAutomatonTransitionPropertyHider(new SubstitutionHider<>(""));
        builder.setRewriters(Collections.emptyList());
        var comparator = builder.createComparator();

        return comparator.compare(reference, subject);
    }

    @Override
    public BuildDTO serialize(DiffAutomaton<String> automaton) {
        
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
        AtomicInteger counter = new AtomicInteger(0); 
        List<BuildDTO.Edge> edges = automaton.getTransitions().stream()
            .map(transition -> {
                int edgeId = counter.incrementAndGet();
                
                return new BuildDTO.Edge(
                String.valueOf(edgeId), 
                transition.getSource().getId(), 
                transition.getTarget().getId(), 
                new BuildDTO.EdgeAttributes(
                    List.of(new BuildDTO.LabelEntry(
                        BuildDTO.LabelType.LABEL, 
                        transition.getProperty().getProperty(), 
                        transition.getProperty().getDiffKind().toString())
                    )
                ));
            })           
            .toList();

        return new BuildDTO(nodes, edges); 
    }
}
