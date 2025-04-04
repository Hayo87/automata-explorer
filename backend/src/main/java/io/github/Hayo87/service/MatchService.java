package io.github.Hayo87.service;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.MatchResultDTO;


@Service
public class MatchService {

    public MatchResultDTO match(DiffAutomaton<String> automaton) {
        return new MatchResultDTO(1, "Dummy");
    }
}

        

