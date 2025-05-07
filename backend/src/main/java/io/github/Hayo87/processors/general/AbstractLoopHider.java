package io.github.Hayo87.processors.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.handlers.AbstractDiffHandler.ActionKey;
import io.github.Hayo87.processors.DiffAutomatonProcessor;
import io.github.Hayo87.processors.ProcessingModel.SubType;
import io.github.Hayo87.processors.ProcessingModel.Type;

/**
 * Universal hider for transitions with the same source and target (loops). 
 */
@Component
public abstract class AbstractLoopHider<T> implements DiffAutomatonProcessor<T> {

    @Override
    public Set<ActionKey> keys() {
        return Set.of(
            new ActionKey(Type.HIDER, SubType.LOOP)
        );
    }     
    
    @Override
    public DiffAutomaton<T> apply(DiffAutomaton<T> diffAutomaton, ProcessingActionDTO action) {
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

