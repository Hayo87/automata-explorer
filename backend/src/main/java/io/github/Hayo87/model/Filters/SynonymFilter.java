package io.github.Hayo87.model.Filters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.model.MealyTransition.Mealy;
import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

@Component
public class SynonymFilter implements DiffAutomatonFilter<Mealy> {

    @Override
    public FilterType getType() {
        return FilterType.SYNONYM;
    }

    @Override
    public boolean supports(FilterSubtype subType) {
        return subType == FilterSubtype.INPUT || subType == FilterSubtype.OUTPUT;
    }

    @Override
    public DiffAutomaton<Mealy> apply(DiffAutomaton<Mealy> diffAutomaton, FilterActionDTO action) {

        String name = action.getName();
        List<String> synonyms = action.getValues();
        FilterSubtype subtype = action.getSubtype();

        Function<Mealy, String> extract;
        BiFunction<Mealy, String, Mealy> replace;

        switch (subtype) {
            case INPUT -> {
                extract = mealy -> mealy.getInput().getProperty();
                replace = (old, newVal) -> new Mealy(newVal, old.getOutput().getProperty(), old.getInput().getDiffKind());
            }
            case OUTPUT -> {
                extract = mealy -> mealy.getOutput().getProperty();
                replace = (old, newVal) -> new Mealy(old.getInput().getProperty(), newVal, old.getOutput().getDiffKind());
            }
            default -> throw new IllegalStateException("Unexpected subtype: " + subtype);
        }

        List<Transition<DiffAutomatonStateProperty, DiffProperty<Mealy>>> toRemove = new ArrayList<>();
        
        // Check all transitions, replace if needed
        for (var t : diffAutomaton.getTransitions()) {
            Mealy oldLabel = t.getProperty().getProperty();
            String currentValue = extract.apply(oldLabel);
        
            if (synonyms.contains(currentValue)) {
                Mealy newLabel = replace.apply(oldLabel, name);

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

