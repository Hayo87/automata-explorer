package io.github.Hayo87.model.Filters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

@Component
public class LoopHider<T> implements DiffAutomatonFilter<T> {

    @Override
    public FilterType getType() {
        return FilterType.HIDER; 
    }

    @Override
    public boolean supports(FilterSubtype subType) {
        return subType == FilterSubtype.LOOP; 
    }

    @Override
    public DiffAutomaton<T> apply(DiffAutomaton<T> diffAutomaton, FilterActionDTO action) {
        List<Transition<DiffAutomatonStateProperty, DiffProperty<T>>> toRemove = new ArrayList<>();

        // Add all transitions with same source, target
        toRemove.addAll(diffAutomaton.getTransitions(t -> t.getSource().equals(t.getTarget())));

        // Remove all from the diffAutomaton
        for (Transition<DiffAutomatonStateProperty, DiffProperty<T>> t : toRemove) {
            diffAutomaton.removeTransition(t);
        }
        
        return diffAutomaton;
    }
}

