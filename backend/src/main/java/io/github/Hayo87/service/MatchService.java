package io.github.Hayo87.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import io.github.Hayo87.dto.MatchResultDTO;

@Service
public class MatchService {


    public MatchResultDTO match(DiffAutomaton<String> automaton) {

        Set<Transition<DiffAutomatonStateProperty,DiffProperty<String>>> edges = automaton.getTransitions();
        return groupRelevantTransitions(edges);
    }


    public MatchResultDTO groupRelevantTransitions(
        Set<Transition<DiffAutomatonStateProperty, DiffProperty<String>>> edges) {

    // { startId, endId } → List<Transitions>
    Table<Integer, Integer, List<Transition<DiffAutomatonStateProperty, DiffProperty<String>>>> groups = HashBasedTable.create();

    // Filter and group
    edges.stream()
            .filter(t -> t.getProperty().getDiffKind() == DiffKind.ADDED || t.getProperty().getDiffKind() == DiffKind.REMOVED)
            .forEach(t -> {
                int startId = t.getSource().getId();
                int endId = t.getTarget().getId();

                // Ensure the (startId, endId) entry exists
                if (!groups.contains(startId, endId)) {
                    groups.put(startId, endId, new ArrayList<>());
                }

                // Add the transition to the list
                groups.get(startId, endId).add(t);
            });

    return new MatchResultDTO(groups.size(), groups);
    }
}
        

