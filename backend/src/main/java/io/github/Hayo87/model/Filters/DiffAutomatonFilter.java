package io.github.Hayo87.model.Filters;

import java.util.Set;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

public interface DiffAutomatonFilter<T> {

    FilterType getType();
    Set<FilterSubtype> getSupportedSubtypes();
    DiffAutomaton<T> apply(DiffAutomaton<T> diffAutomaton, FilterActionDTO action);
}