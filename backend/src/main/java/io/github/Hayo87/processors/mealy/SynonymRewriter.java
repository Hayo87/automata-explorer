package io.github.Hayo87.processors.mealy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.handlers.AbstractDiffHandler.ActionKey;
import io.github.Hayo87.model.LabelUtils;
import io.github.Hayo87.model.Mealy;
import io.github.Hayo87.processors.DiffAutomatonProcessor;
import io.github.Hayo87.processors.ProcessingModel.SubType;
import io.github.Hayo87.processors.ProcessingModel.Type;

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

        Function<Mealy, String> extract;
        BiFunction<Mealy, String, Mealy> replace;

        switch (subtype) {
            case INPUT -> {
                extract = mealy -> mealy.input();
                replace = (old, newVal) -> new Mealy(newVal, old.output());
            }
            case OUTPUT -> {
                extract = mealy -> mealy.output();
                replace = (old, newVal) -> new Mealy(old.input(), newVal);
            }
            default -> throw new IllegalStateException("Unexpected subtype: " + subtype);
        }

        List<Transition<DiffAutomatonStateProperty, DiffProperty<Mealy>>> toRemove = new ArrayList<>();
        
        // Check all transitions, replace if needed
        for (var t : diffAutomaton.getTransitions()) {
            Mealy oldLabel = t.getProperty().getProperty();
            String currentValue = extract.apply(oldLabel);
        
            if (synonyms.contains(currentValue)) {
                Mealy newLabel = replace.apply(oldLabel, LabelUtils.writeSynonymLabel(name));

                DiffProperty<Mealy> newProp = new DiffProperty<>(newLabel, t.getProperty().getDiffKind());
        
                diffAutomaton.addTransition(t.getSource(), newProp, t.getTarget());
                toRemove.add(t);
            }
        }
        // Remove old transitions
        toRemove.forEach(diffAutomaton::removeTransition);

        return diffAutomaton;
    }
}

