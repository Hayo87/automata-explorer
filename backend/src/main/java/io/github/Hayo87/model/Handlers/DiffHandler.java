package io.github.Hayo87.model.Handlers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.FilterActionDTO;

public interface DiffHandler<T> {
    DiffAutomaton<T> convert(Automaton<String> input, boolean isReference);
    DiffAutomaton<T> build(DiffAutomaton<T> reference, DiffAutomaton<T> subject);
    DiffAutomaton<T> preFilter(DiffAutomaton<T> automaton, List<FilterActionDTO> filterActions);
    DiffAutomaton<T> postFilter(DiffAutomaton<T> automaton, List<FilterActionDTO> filterActions);
    JsonNode serialize(DiffAutomaton<T> automaton);
}
