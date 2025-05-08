package io.github.Hayo87.domain.handlers;

import java.util.List;

import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.BuildDTO;
import io.github.Hayo87.dto.ProcessingActionDTO;

public interface DiffHandler<T> {

    /**
     * Converts a plain automaton into a difference automaton with uniform error handeling. 
     * @param input input automaton
     * @param isReference if input is the reference
     * @return the converted automaton
     */
    default DiffAutomaton<T> convert(Automaton<String> input, boolean isReference) {
        try {
            return convertInternal(input, isReference);
        } catch (Exception e) {
            String automata = isReference? "reference automata": "subject automata";
            throw new RuntimeException("error converting " + automata , e);
        }
    }

    /**
     * Actual conversion logic to be implemented by concrete handlers is wrapped in {@link #convert}.
     */
    DiffAutomaton<T> convertInternal(Automaton<String> input, boolean isReference);

    /**
     * Build the diff automaton using gLTSDiff by comparing reference and subject
     * @param reference referce automaton
     * @param subject subject automaton
     * @return difference automaton
     */
    default DiffAutomaton<T> build(DiffAutomaton<T> reference, DiffAutomaton<T> subject){
        try {
            return buildInternal(reference, subject);
        } catch (Exception e) {
            throw new RuntimeException("error building the difference automata", e);
        }
    }

    /**
     * Actual build to be implemented by concrete handlers is wrapped in {@link #build}.
     */
    DiffAutomaton<T> buildInternal(DiffAutomaton<T> reference, DiffAutomaton<T> subject);

    /**
     * Applies pre-processing actions before building the difference automaton
     * @param automaton the automaton to modify
     * @param processingActions a list of actions to be applied
     * @return the modified automaton
     */
    default DiffAutomaton<T> preProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> processingActions){
        try {
            return preProcessingInternal(automaton, processingActions);
        } catch (Exception e) {
            throw new RuntimeException("error during pre-processing", e);
        }
    }

    /**
     * Actual pre-processing logic to be implemented by concrete handlers is wrapped in {@link #preProcessing}.
     */
    DiffAutomaton<T> preProcessingInternal(DiffAutomaton<T> automaton, List<ProcessingActionDTO> processingActions);

    /**
     * Applies post-processing actions after building the difference automaton
     * @param automaton the difference automaton to modify
     * @param processingActions a list of actions to be applied
     * @return the modified automaton
     */
    default DiffAutomaton<T> postProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> processingActions){
        try {
            return postProcessingInternal(automaton, processingActions);
        } catch (Exception e) {
            throw new RuntimeException("error during post-processing", e);
        }
    }

    /**
     * Actual post-processing logic to be implemented by concrete handlers is wrapped in {@link #postProcessing}.
     */
    DiffAutomaton<T> postProcessingInternal(DiffAutomaton<T> automaton, List<ProcessingActionDTO> processingActions);

    /**
     * Serializes the difference automaton into a DTO. 
     * @param automaton the automaton to serialize
     * @return the serialized automaton
     */
    default BuildDTO serialize(DiffAutomaton<T> automaton){
        try {
            return serializeInternal(automaton);
        } catch (Exception e) {
            throw new RuntimeException("error during serializing", e);
        }
    }
    
    /**
     * Actual serialization logic to be implemented by concrete handlers is wrapped in {@link #serialize}.
     */
    BuildDTO serializeInternal(DiffAutomaton<T> automaton);
}
