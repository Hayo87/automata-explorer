package io.github.Hayo87.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.model.LabelUtils;
import io.github.Hayo87.type.FilterSubtype;

@Service
public class FilterService {
    private final SessionService sessionService;

    public FilterService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void processFilters(String sessionId, List<FilterActionDTO> filterActions){
        // Sort the actions by the order field
        filterActions.sort(Comparator.comparingInt(FilterActionDTO::getOrder));
        
        //Process filters actions in order
        for (FilterActionDTO action : filterActions) {
            switch (action.getType()) {
                case SYNONYM -> processSynonym(action, sessionId);
                case HIDER -> processHider(action, sessionId);
                default -> {
                }
            }
        }
    }
        
    private void processSynonym(FilterActionDTO synonymsAction,String sessionId) {
        DiffAutomaton<String> reference = sessionService.getReferenceAutomata(sessionId);
        DiffAutomaton<String> subject = sessionService.getSubjectAutomata(sessionId);

            String name = synonymsAction.getName();
            List<String> synonyms = synonymsAction.getValues();
            FilterSubtype subtype = synonymsAction.getSubtype();
            applySynonyms(subtype, reference, name, synonyms);
            applySynonyms(subtype, subject, name, synonyms); 
    }    
        
    private void applySynonyms(FilterSubtype subtype, DiffAutomaton<String> automaton, String name, List<String> synonyms) {
        List <Transition<DiffAutomatonStateProperty, DiffProperty<String>>> toRemove = new ArrayList<>();

        for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> t: automaton.getTransitions()) {
            String label = t.getProperty().getProperty();

            String partToCompare = switch (subtype) {
                case INPUT -> LabelUtils.extractInput(label);
                case OUTPUT -> LabelUtils.extractOutput(label);
            };

            if (synonyms.contains(partToCompare)) {
                String newLabel = switch (subtype) {
                    case INPUT -> LabelUtils.replaceInput(label, name);
                    case OUTPUT -> LabelUtils.replaceOutput(label, name);
                    };
                // add new transtition
                automaton.addTransition(t.getSource(), new DiffProperty<>(newLabel, t.getProperty().getDiffKind()), t.getTarget());
                // mark old transition for removal
                toRemove.add(t);    
                }
        }
        // Remove old transitions
        for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> t : toRemove) {
            automaton.removeTransition(t);
        }
    }

    private void processHider(FilterActionDTO hiderAction,String sessionId){
        // dummy 
    } 

}
