package io.github.Hayo87.service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.MatchResultDTO;
import io.github.Hayo87.model.SimpleEdge;

@Service
public class MatchService {


    public MatchResultDTO match(DiffAutomaton<String> automaton) {

        Set<Transition<DiffAutomatonStateProperty,DiffProperty<String>>> edges = automaton.getTransitions();
        return groupRelevantTransitions(edges);
    }


    public MatchResultDTO groupRelevantTransitions(
        Set<Transition<DiffAutomatonStateProperty, DiffProperty<String>>> edges) {

    // Collect all `SimpleEdge` objects
    List<SimpleEdge> simpleEdges = edges.stream()
            .filter(t -> t.getProperty().getDiffKind() == DiffKind.ADDED || t.getProperty().getDiffKind() == DiffKind.REMOVED)
            .map(t -> new SimpleEdge(t.getSource().getId(), t.getTarget().getId(), t.getProperty().getProperty(), t.getProperty().getDiffKind()))
            .toList();

    // Group `SimpleEdge` objects by (startId, endId)
    List<List<SimpleEdge>> groupedEdges = simpleEdges.stream()
            .collect(Collectors.groupingBy(e -> new AbstractMap.SimpleEntry<>(e.getStartId(), e.getEndId())))
            .values().stream()
            .toList();

    return new MatchResultDTO(groupedEdges.size(), groupedEdges);
}

}

        

