package io.github.Hayo87.model.Handlers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.ProcessingActionDTO;

public interface DiffHandler<T> {
    DiffAutomaton<T> convert(Automaton<String> input, boolean isReference);
    DiffAutomaton<T> build(DiffAutomaton<T> reference, DiffAutomaton<T> subject);
    DiffAutomaton<T> preProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filterActions);
    DiffAutomaton<T> postProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filterActions);
    JsonNode serialize(DiffAutomaton<T> automaton);
}
