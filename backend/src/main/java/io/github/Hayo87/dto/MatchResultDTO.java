package io.github.Hayo87.dto;

import java.io.Serializable;
import java.util.List;

import io.github.Hayo87.model.EditOperation;


public class MatchResultDTO implements Serializable {
    private final int score;  
    private final List<EditOperation> matches;

    public MatchResultDTO(int score, List<EditOperation> matches) {
        this.score = score;
        this.matches = matches;
    }
}
