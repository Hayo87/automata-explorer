package io.github.Hayo87.processors.mealy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.handlers.AbstractDiffHandler.ActionKey;
import io.github.Hayo87.model.Mealy;
import io.github.Hayo87.model.MergedMealy;
import io.github.Hayo87.processors.DiffAutomatonProcessor;
import io.github.Hayo87.processors.ProcessingModel.SubType;
import io.github.Hayo87.processors.ProcessingModel.Type;

/**
 * Post-processor to merge parallel Mealy transtitions with the same input into a 
 * merged transtion property {@link MergedMealy}.  
 */
@Component
public class MealyMerger implements DiffAutomatonProcessor<Mealy>{

    @Override
    public Set<ActionKey> keys() {
        return Set.of(
            new ActionKey(Type.MERGER, SubType.INPUT)
        );
    }

    @Override
    public DiffAutomaton<Mealy> apply(DiffAutomaton<Mealy> diffAutomaton, ProcessingActionDTO action) {
        List<Transition<DiffAutomatonStateProperty, DiffProperty<Mealy>>> toRemove = new ArrayList<>();

        // Get a list without unchanged transition
        var allTranstitions = diffAutomaton.getTransitions();
        var  relevantTransitions = 
            allTranstitions.stream()
                .filter(t -> t.getProperty().getDiffKind() != DiffKind.UNCHANGED)
                .toList();

        // Group remaining transitions by source, target, and input
        record TransitionKey(int source, int target, String input){};

        var grouped = relevantTransitions.stream()
            .collect(Collectors.groupingBy(t -> 
            new TransitionKey(
                t.getSource().getId(), t.getTarget().getId(), t.getProperty().getProperty().input())));

        // Identify merge possibilities
        for(var entry: grouped.entrySet()) {
            var transitions = entry.getValue();

            if(transitions.size() ==2 ) {
                var t1 = transitions.get(0);
                var t2 = transitions.get(1);

                // Create and add a new merged transtition
                boolean t1Removed = t1.getProperty().getDiffKind() == DiffKind.REMOVED;

                String input = t1.getProperty().getProperty().input();
                String removedOutput = t1Removed? t1.getProperty().getProperty().output(): t2.getProperty().getProperty().output();
                String adddedOutput = t1Removed? t2.getProperty().getProperty().output(): t1.getProperty().getProperty().output();

                diffAutomaton.addTransition(
                new Transition<>(
                    t1.getSource(),
                    new DiffProperty<>(new MergedMealy(input, removedOutput, adddedOutput), DiffKind.UNCHANGED),
                    t1.getTarget()
                )
            ); 

                // Mark old transitions for removal
                toRemove.add(t1);
                toRemove.add(t2);
            }
        }        

        // Remove old transitions
        toRemove.forEach(diffAutomaton::removeTransition);

        return diffAutomaton;
    }
}
