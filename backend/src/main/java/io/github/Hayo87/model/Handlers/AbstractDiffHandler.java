package io.github.Hayo87.model.Handlers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.model.Filters.DiffAutomatonFilter;
import io.github.Hayo87.type.FilterType;

public abstract class AbstractDiffHandler<T> implements DiffHandler<T> {

    protected final Map<FilterType, DiffAutomatonFilter<T>> filterRegistry;

    public AbstractDiffHandler(List<DiffAutomatonFilter<T>> filters) {
        this.filterRegistry = filters.stream()
            .collect(Collectors.toMap(DiffAutomatonFilter::getType, f -> f));
    }

    protected DiffAutomaton<T> applyFilters(DiffAutomaton<T> automaton, List<FilterActionDTO> actions) {
        for (FilterActionDTO action : actions) {
            DiffAutomatonFilter<T> filter = filterRegistry.get(action.getType());
    
            if (filter != null && filter.supports(action.getSubtype())) {
                automaton = filter.apply(automaton, action);
            }
        }
        return automaton;
    }
    
    @Override
    public DiffAutomaton<T> preFilter(DiffAutomaton<T> automaton, List<FilterActionDTO> filters) {
        return applyFilters(automaton, filters);
    }

    @Override
    public DiffAutomaton<T> postFilter(DiffAutomaton<T> automaton, List<FilterActionDTO> filters) {
        return applyFilters(automaton, filters);
    }
}
