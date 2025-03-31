package io.github.Hayo87.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.model.LabelUtils;

@Service
public class FilterService {
    private final SessionService sessionService;

    public FilterService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void processSynonyms(List<FilterActionDTO> filters,String sessionId) {
        DiffAutomaton<String> reference = sessionService.getReferenceAutomata(sessionId);
        DiffAutomaton<String> subject = sessionService.getSubjectAutomata(sessionId);

        for (FilterActionDTO s: filters) {
            String name = s.getName();
            List<String> synonyms = s.getValues();
            applySynonyms(reference, name, synonyms);
            applySynonyms(subject, name, synonyms); 
        }
    }    
        
    private void applySynonyms( DiffAutomaton<String> automaton, String name, List<String> synonyms ) {
        Set<Transition<DiffAutomatonStateProperty, DiffProperty<String>>> transitions = automaton.getTransitions();
        List <Transition<DiffAutomatonStateProperty, DiffProperty<String>>> toRemove = new ArrayList<>();

            for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> t: transitions) {
                String label = t.getProperty().getProperty();
                String input = LabelUtils.extractInput(label);
                String output = LabelUtils.extractOutput(label);

                if (synonyms.contains(output)) {
                String newLabel = input + "/" + name;    
                // add new transtition        
                automaton.addTransition(t.getSource(), new DiffProperty<>(newLabel, t.getProperty().getDiffKind()), t.getTarget());
                // mark old transition for removal
                toRemove.add(t);    
                }
            }
            // Remove replaced transitions
            for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> t : toRemove) {
                automaton.removeTransition(t);
            }
        }
}
