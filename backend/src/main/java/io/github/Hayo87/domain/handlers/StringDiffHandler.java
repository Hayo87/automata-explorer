package io.github.Hayo87.domain.handlers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomata;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.operators.combiners.EqualityCombiner;

import io.github.Hayo87.domain.analyzer.Analyzer;
import io.github.Hayo87.domain.processors.DiffAutomatonProcessor;
import io.github.Hayo87.dto.AnalysisDTO;
import io.github.Hayo87.dto.BuildDTO;

@Component
public class StringDiffHandler extends AbstractDiffHandler<String> {

    public StringDiffHandler(List<DiffAutomatonProcessor<String>> filters) {
        super(filters);
    }

    @Override
    public DiffAutomaton<String> convertInternal( Automaton<String> input, boolean isReference) {
        return DiffAutomata.fromAutomaton(input, (isReference ? DiffKind.REMOVED : DiffKind.ADDED));
    }

    @Override
    public DiffAutomaton<String> buildInternal(DiffAutomaton<String> reference, DiffAutomaton<String> subject) {
        DiffAutomatonStructureComparatorBuilder<String> builder = new DiffAutomatonStructureComparatorBuilder<>();
        builder.setRewriters(Collections.emptyList());
        var comparator = builder.createComparator();

        return comparator.compare(reference, subject);
    }

    @Override
    public BuildDTO serializeInternal(DiffAutomaton<String> automaton) {
        
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
                
                return new BuildDTO.Edge(
                String.valueOf(edgeId), 
                transition.getSource().getId(), 
                transition.getTarget().getId(), 
                new BuildDTO.EdgeAttributes(
                    transition.getProperty().getDiffKind().toString(),
                    transition.getProperty().getProperty(),
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

    @Override
    public AnalysisDTO analyze(DiffAutomaton<String> automaton){
        Analyzer<String> analyzer = new Analyzer<>(new EqualityCombiner<>());
        return analyzer.analyze(automaton);
    }
}
