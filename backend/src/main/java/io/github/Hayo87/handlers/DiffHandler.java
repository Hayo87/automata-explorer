package io.github.Hayo87.handlers;

import java.util.List;


import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.BuildDTO;
import io.github.Hayo87.dto.ProcessingActionDTO;

public interface DiffHandler<T> {
    DiffAutomaton<T> convert(Automaton<String> input, boolean isReference);
    DiffAutomaton<T> build(DiffAutomaton<T> reference, DiffAutomaton<T> subject);
    DiffAutomaton<T> preProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filterActions);
    DiffAutomaton<T> postProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filterActions);
    BuildDTO serialize(DiffAutomaton<T> automaton);
}
