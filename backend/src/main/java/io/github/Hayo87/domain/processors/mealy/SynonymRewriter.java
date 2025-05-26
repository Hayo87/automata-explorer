package io.github.Hayo87.domain.processors.mealy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.domain.handlers.AbstractDiffHandler.ActionKey;
import io.github.Hayo87.domain.model.Mealy;
import io.github.Hayo87.domain.processors.DiffAutomatonProcessor;
import io.github.Hayo87.domain.rules.LabelUtils;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;
import io.github.Hayo87.dto.ProcessingActionDTO;

/**
 * Pre-processor to overwrite input or output to implement to concept of synonyms. 
 */
@Component
public class SynonymRewriter implements DiffAutomatonProcessor<Mealy> {

    @Override
    public Set<ActionKey> keys() {
        return Set.of(
            new ActionKey(Type.SYNONYM, SubType.INPUT),
            new ActionKey(Type.SYNONYM, SubType.OUTPUT)
        );
    }     

    @Override
    public DiffAutomaton<Mealy> apply(DiffAutomaton<Mealy> diffAutomaton, ProcessingActionDTO action) {
        String name = action.name();
        List<String> synonyms = action.values();
        SubType subtype = action.subtype();


        // Check all transitions, replace if needed
        for (var t : diffAutomaton.getTransitions()) {
            Mealy m = t.getProperty().getProperty();

            // Input case
            if(subtype == SubType.INPUT && synonyms.contains(m.getInput().getProperty())) {
                m.setInput(new DiffProperty<>(name, m.getInput().getDiffKind()));
            }

            // Output case
            if(subtype== SubType.OUTPUT) {
                boolean hasMatch = m.getOutput().stream().anyMatch(o -> synonyms.contains(o.getProperty()));

                if(hasMatch){
                    Set<DiffProperty<String>> newOutputs = m.getOutput().stream()
                        .map(o -> synonyms.contains(o.getProperty())
                            ? new DiffProperty<>(LabelUtils.writeSynonymLabel(name), o.getDiffKind())
                            : o)
                        .collect(Collectors.toSet());  

                    m.setOutput(newOutputs);
                }
            }
        }
        return diffAutomaton;
    }
}
