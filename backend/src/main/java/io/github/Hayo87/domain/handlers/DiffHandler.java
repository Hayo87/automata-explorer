package io.github.Hayo87.domain.handlers;

import java.util.List;

import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.BuildDTO;
import io.github.Hayo87.dto.ProcessingActionDTO;

public interface DiffHandler<T> {

    /**
     * Converts a plain automaton into a difference automaton
     * @param input input automaton
     * @param isReference if input is the reference
     * @return the converted automaton
     */
    DiffAutomaton<T> convert(Automaton<String> input, boolean isReference);

    /**
     * Build the diff automaton using gLTSDiff by comparing reference and subject
     * @param reference referce automaton
     * @param subject subject automaton
     * @return difference automaton
     */
    DiffAutomaton<T> build(DiffAutomaton<T> reference, DiffAutomaton<T> subject);

    /**
     * Applies pre-processing actions before building the difference automaton
     * @param automaton the automaton to modify
     * @param processingActions a list of actions to be applied
     * @return the modified automaton
     */
    DiffAutomaton<T> preProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> processingActions);

    /**
     * Applies post-processing actions after building the difference automaton
     * @param automaton the difference automaton to modify
     * @param processingActions a list of actions to be applied
     * @return the modified automaton
     */
    DiffAutomaton<T> postProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> processingActions);

    /**
     * Serializes the difference automaton into a DTO. 
     * @param automaton the automaton to serialize
     * @return the serialized automaton
     */
    BuildDTO serialize(DiffAutomaton<T> automaton);
}
