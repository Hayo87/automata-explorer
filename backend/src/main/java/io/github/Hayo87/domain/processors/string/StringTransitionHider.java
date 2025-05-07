package io.github.Hayo87.domain.processors.string;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.domain.handlers.AbstractDiffHandler.ActionKey;
import io.github.Hayo87.domain.processors.DiffAutomatonProcessor;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;
import io.github.Hayo87.dto.ProcessingActionDTO;

/**
 * Hider that hides transitions based on their label text. 
 */
@Component
public class StringTransitionHider implements DiffAutomatonProcessor<String> {

    @Override
    public Set<ActionKey> keys() {
        return Set.of(
            new ActionKey(Type.HIDER, SubType.LABEL)
        );
    }

    @Override
    public DiffAutomaton<String> apply(DiffAutomaton<String> diffAutomaton, ProcessingActionDTO action) {
        System.out.println("Apply string hider");
        List<Transition<DiffAutomatonStateProperty, DiffProperty<String>>> toRemove = new ArrayList<>();
        // Check all transitions
        for (var t : diffAutomaton.getTransitions()) {
            String label = t.getProperty().getProperty();
            List<String> hiderValues = action.values();
        
            if (hiderValues.contains(label)) {
                toRemove.add(t);
            }
        }
        // Remove old transitions
        toRemove.forEach(diffAutomaton::removeTransition);

        return diffAutomaton;
    }       
}
    
    


