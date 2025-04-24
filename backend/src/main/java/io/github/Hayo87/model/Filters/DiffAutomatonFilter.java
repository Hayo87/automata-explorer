package io.github.Hayo87.model.Filters;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

public interface DiffAutomatonFilter<T> {

    FilterType getType();
    boolean supports(FilterSubtype subType);
    DiffAutomaton<T> apply(DiffAutomaton<T> diffAutomaton, FilterActionDTO action);
}