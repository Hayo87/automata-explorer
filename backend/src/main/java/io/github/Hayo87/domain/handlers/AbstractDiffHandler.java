package io.github.Hayo87.domain.handlers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.domain.processors.DiffAutomatonProcessor;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;
import io.github.Hayo87.dto.ProcessingActionDTO;

/**
 * Base implementation for the {@link DiffHandler} that provides the common logic for
 * pre- and post-processing. 
 * 
 * @param <T> the automaton transition property type
 */
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

    /**
     * Helper method to apply the actual actions. 
     * 
     * @param automaton the automaton to be modified
     * @param actions the list of actions to be aplied
     * @return the modified automaton
     */    
    protected DiffAutomaton<T> applyProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> actions) {
        for (ProcessingActionDTO action : actions) {
            ActionKey key = new ActionKey(action.type(), action.subtype());
            DiffAutomatonProcessor<T> processor = actionRegistry.get(key);
     
            if (processor != null) {
                automaton = processor.apply(automaton, action);
            }
        }
        return automaton;
    }
        
    @Override
    public DiffAutomaton<T> preProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filters) {
        return applyProcessing(automaton, filters);
    }

    @Override
    public DiffAutomaton<T> postProcessing(DiffAutomaton<T> automaton, List<ProcessingActionDTO> filters) {
        return applyProcessing(automaton, filters);
    }
}
