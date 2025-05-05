package io.github.Hayo87.model.Processors.Universal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.model.Handlers.AbstractDiffHandler.ActionKey;
import io.github.Hayo87.model.MealyTransition.Mealy;
import io.github.Hayo87.model.Processors.DiffAutomatonProcessor;
import io.github.Hayo87.model.Processors.ProcessingModel.SubType;
import io.github.Hayo87.model.Processors.ProcessingModel.Type;


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
        SubType subtype = action.subType();
        Function<Mealy, String> extract;

        switch (subtype) {
            case INPUT -> {
                extract = mealy -> mealy.input();
            }
            case OUTPUT -> {
                extract = mealy -> mealy.output();
            }
            default -> throw new IllegalStateException("Unexpected subtype: " + subtype);
        }

        List<Transition<DiffAutomatonStateProperty, DiffProperty<Mealy>>> toRemove = new ArrayList<>();
        // Check all transitions
        for (var t : diffAutomaton.getTransitions()) {
            Mealy oldLabel = t.getProperty().getProperty();
            String currentValue = extract.apply(oldLabel);
        
            if (values.contains(currentValue)) {
                toRemove.add(t);
            }
        }
        // Remove old transitions
        toRemove.forEach(diffAutomaton::removeTransition);

        return diffAutomaton;
    }     
}

