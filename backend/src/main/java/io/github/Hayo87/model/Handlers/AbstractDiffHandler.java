package io.github.Hayo87.model.Handlers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.model.Processors.DiffAutomatonProcessor;
import io.github.Hayo87.model.Processors.ProcessingModel.SubType;
import io.github.Hayo87.model.Processors.ProcessingModel.Type;

public abstract class AbstractDiffHandler<T> implements DiffHandler<T> {
    
    public static record ActionKey(Type type, SubType subtype) {}
    protected final Map<ActionKey, DiffAutomatonProcessor<T>> actionRegistry;
    
    /** 
     * Builds the action registry by associating each supported pair
     * with the corresponing processor. 
     */
    public AbstractDiffHandler(List<DiffAutomatonProcessor<T>> processors) {
        this.actionRegistry = processors.stream()
            .flatMap(p -> p.keys().stream().map(key -> Map.entry(key, p)))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
        }

    protected DiffAutomaton<T> applyFilters(DiffAutomaton<T> automaton, List<ProcessingActionDTO> actions) {
        for (ProcessingActionDTO action : actions) {
            ActionKey key = new ActionKey(action.type(), action.subType());
            DiffAutomatonProcessor<T> processor = actionRegistry.get(key);
     
            if (processor != null) {
                automaton = processor.apply(automaton, action);
            }
        }
        return automaton;
    }
        
    @Override
    public DiffAutomaton<T> preProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filters) {
        return applyFilters(automaton, filters);
    }

    @Override
    public DiffAutomaton<T> postProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filters) {
        return applyFilters(automaton, filters);
    }
}
