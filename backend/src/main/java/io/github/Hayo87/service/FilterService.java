package io.github.Hayo87.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.Transition;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomatonStateProperty;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.model.LabelUtils;
import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

@Service
public class FilterService {
    private final SessionService sessionService;

    public FilterService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void preProcessing(String sessionId, List<FilterActionDTO> filterActions) {
        List<FilterActionDTO> preActions = filterActions.stream()
            .filter(action -> action.getType() == FilterType.SYNONYM)
            .collect(Collectors.toList());
        process(sessionId, preActions);
    }

    public void postProcessing(String sessionId,List<FilterActionDTO> filterActions) {
        List<FilterActionDTO> postActions = filterActions.stream()
            .filter(action -> action.getType() == FilterType.HIDER)
            .collect(Collectors.toList());
        process(sessionId, postActions);
    }

    private void process(String sessionId, List<FilterActionDTO> filterActions){
        filterActions.sort(Comparator.comparingInt(FilterActionDTO::getOrder));
        
        for (FilterActionDTO action : filterActions) {
            System.out.println(action.getType().toString());
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

        synonymsAction.setDecoratedName(LabelUtils.writeSynonymLabel("", name, subtype));      
    }    
        
    private void applySynonyms(FilterSubtype subtype, DiffAutomaton<String> automaton, String name, List<String> synonyms) {
        List <Transition<DiffAutomatonStateProperty, DiffProperty<String>>> toRemove = new ArrayList<>();

        for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> t: automaton.getTransitions()) {
            String label = t.getProperty().getProperty();

            String partToCompare = switch (subtype) {
                case INPUT -> LabelUtils.extractInput(label);
                case OUTPUT -> LabelUtils.extractOutput(label);
                default -> throw new IllegalStateException("Unexpected subtype: " + subtype);
            };

            if (synonyms.contains(partToCompare)) {
                String newLabel = LabelUtils.writeSynonymLabel(label, name, subtype);
                
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

    private void processHider(FilterActionDTO filterAction,String sessionId){
        DiffAutomaton<String> diffAutomaton = sessionService.getLatestDiffAutomaton(sessionId);
        List<String> filters = filterAction.getValues();
        FilterSubtype subtype = filterAction.getSubtype();

        applyHiders(subtype, diffAutomaton,filters);
    } 

    private void applyHiders(FilterSubtype subtype, DiffAutomaton<String> diffAutomaton, List<String> filters) {
        List<Transition<DiffAutomatonStateProperty, DiffProperty<String>>> toRemove = new ArrayList<>();
    
        if (subtype == FilterSubtype.LOOP) {
            toRemove.addAll(diffAutomaton.getTransitions(t -> t.getSource().equals(t.getTarget())));

        } else {
            System.out.println("*** Processing INPUT / OUTPUT filters");
            for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> t : diffAutomaton.getTransitions()) {
                String label = t.getProperty().getProperty();
    
                String partToCompare = switch (subtype) {
                    case INPUT -> LabelUtils.extractInput(label);
                    case OUTPUT -> LabelUtils.extractOutput(label);
                    default -> throw new IllegalStateException("Unexpected subtype: " + subtype);
                };
    
                if (filters.contains(partToCompare)) {
                    System.out.println("*** Filters: " + filters.toString());
                    System.out.println("*** Part to Compare" + filters.toString());
                    toRemove.add(t);
                }
            }
        }
    
        for (Transition<DiffAutomatonStateProperty, DiffProperty<String>> t : toRemove) {
            diffAutomaton.removeTransition(t);
        }
    }
}
    
