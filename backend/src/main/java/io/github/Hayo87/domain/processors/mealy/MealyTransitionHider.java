package io.github.Hayo87.domain.processors.mealy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.domain.handlers.AbstractDiffHandler.ActionKey;
import io.github.Hayo87.domain.model.Mealy;
import io.github.Hayo87.domain.model.MergedMealy;
import io.github.Hayo87.domain.processors.DiffAutomatonProcessor;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;
import io.github.Hayo87.dto.ProcessingActionDTO;

/**
 * Hider that hides transitions based on their input or output. 
 */
@Component
public class MealyTransitionHider implements DiffAutomatonProcessor<Mealy> {

    @Override
    public Set<ActionKey> keys() {
        return Set.of(
            new ActionKey(Type.HIDER, SubType.INPUT),
            new ActionKey(Type.HIDER, SubType.OUTPUT)
        );
    }    

    @Override
    public DiffAutomaton<Mealy> apply(DiffAutomaton<Mealy> diffAutomaton, ProcessingActionDTO action) {
        List<String> values = action.values();
        SubType subtype = action.subtype();
        Function<Mealy, List<String>> extract;

        switch (subtype) {
            case INPUT -> {
                extract = mealy -> List.of(mealy.input());
            }
            case OUTPUT -> {
                extract = mealy -> (mealy instanceof MergedMealy merged)
                    ? List.of(merged.output(), merged.addedOutput())     
                    : List.of(mealy.output());
            }
            default -> throw new IllegalStateException("Unexpected subtype: " + subtype);
        }

        List<Transition<DiffAutomatonStateProperty, DiffProperty<Mealy>>> toRemove = new ArrayList<>();
        // Check all transitions
        for (var t : diffAutomaton.getTransitions()) {
            Mealy oldLabel = t.getProperty().getProperty();
            List<String> currentValues = extract.apply(oldLabel);
        
            if (currentValues.stream().anyMatch(values::contains)) {
                toRemove.add(t);
            }
        }
        // Remove old transitions
        toRemove.forEach(diffAutomaton::removeTransition);

        return diffAutomaton;
    }     
}

