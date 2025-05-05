package io.github.Hayo87.model.Processors;

import java.util.Set;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.model.Handlers.AbstractDiffHandler.ActionKey;

/**
 * The processor applies a transformation to a {@link DiffAutomaton} based on the
 * specific processing action. 
 */
public interface DiffAutomatonProcessor<T> {

    /**
     * Returns the set of (Type, Subtype) pairst that this processor supports.
     */
    Set<ActionKey> keys();

    /**
     * Applies this processors logic to the given automaton based on the action.
     */
    DiffAutomaton<T> apply(DiffAutomaton<T> diffAutomaton, ProcessingActionDTO action);
}