package io.github.Hayo87.model.Handlers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.model.Filters.DiffAutomatonFilter;
import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

public abstract class AbstractDiffHandler<T> implements DiffHandler<T> {

    protected static record FilterKey(FilterType type, FilterSubtype subtype) {}
    protected final Map<FilterKey, DiffAutomatonFilter<T>> filterRegistry;
    
    public AbstractDiffHandler(List<DiffAutomatonFilter<T>> filters) {
        this.filterRegistry = filters.stream()
            .flatMap(f -> f.getSupportedSubtypes().stream()
                .map(sub -> Map.entry(new FilterKey(f.getType(), sub), f))
            )
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
        }

    protected DiffAutomaton<T> applyFilters(DiffAutomaton<T> automaton, List<FilterActionDTO> actions) {
        for (FilterActionDTO action : actions) {
            FilterKey key = new FilterKey(action.getType(), action.getSubtype());
            DiffAutomatonFilter<T> filter = filterRegistry.get(key);
     
            if (filter != null) {
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
