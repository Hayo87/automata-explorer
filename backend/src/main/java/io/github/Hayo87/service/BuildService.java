package io.github.Hayo87.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.BuildRequestDTO;
import io.github.Hayo87.dto.BuildResponseDTO;
import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.dto.ProcessingOptionDTO;
import io.github.Hayo87.handlers.DiffHandler;
import io.github.Hayo87.model.AutomataType;
import io.github.Hayo87.processors.ProcessingModel.Stage;
import io.github.Hayo87.processors.ProcessingRules;

/**
 * Manages all build related actions for Difference Automata's.
 * 
/** */
 @Service
public class BuildService {
    private final SessionService sessionService;
    private final ParserService parserService; 
    private final HandlerService handlerService;
    private final ObjectMapper mapper;

    @Autowired
    public BuildService( SessionService sessionService, ParserService parserService, HandlerService handlerService, ObjectMapper mapper) {
        this.sessionService = sessionService;
        this.parserService = parserService;
        this.handlerService = handlerService;
        this.mapper = mapper;
    }
    /**
     * Get a grouped list of processing options available for the given
     * automata type for the ProcessingRules.
     * @param type
     * @return grouped list with options
     */
    public List<ProcessingOptionDTO> getProcessingOptions(AutomataType type){
        return ProcessingRules.optionsFor(type);
    }

    public void buildInputs(String sessionId) {
        SessionData session = sessionService.getSession(sessionId);

        session.getLock().lock();
        try {
            session.setReference(buildInput(session.getInputReference()));
            session.setSubject(buildInput(session.getInputSubject()));
    
        } catch (Exception e) {
            System.err.println("Rebuilding inputs failed: " + e.getMessage());
        } finally {
            session.getLock().unlock();
        }
    }

    /**
     * Build an input automaton
     * 
     * @param input the input string
     * @param isReference wheter the input is the reference automata
     */
    private Automaton<String> buildInput(String input){
        Automaton<String> result; 

        try{
            result =  parserService.convertDotStringToAutomaton(input); 

        } catch (IOException e) {
            System.err.println("Parsing failed: " + e.getMessage());
            throw new IllegalArgumentException("Invalid DOT file format.");
        }
        return result; 
    }

    public BuildResponseDTO buildDiff(String sessionId, BuildRequestDTO request) {
        SessionData session = sessionService.getSession(sessionId);
        List<ProcessingActionDTO> filters = request.getFilters() == null
            ? List.of()
            : mapper.convertValue(request.getFilters(), new TypeReference<>() {});
    
        DiffHandler<?> handler = handlerService.getHandler(session.getType());
    
        session.getLock().lock();
        try { 
            return handleDiffBuild(session, handler, filters);
        } catch (Exception e) {
            throw new RuntimeException("Build failed", e); 
        } finally {
            session.getLock().unlock(); 
        }
    }

    private <T> BuildResponseDTO handleDiffBuild( SessionData session, DiffHandler<T> handler, List<ProcessingActionDTO> actions) {

         // Convert to type specific
        DiffAutomaton<T> reference = handler.convert(session.getReference(), true);
        DiffAutomaton<T> subject = handler.convert(session.getSubject(), false);

        // Pre processing 
        List<ProcessingActionDTO> preProcessingActions = filterByStage(actions, Stage.PRE);
        reference = handler.preProcessing(reference, preProcessingActions);
        subject = handler.preProcessing(subject, preProcessingActions);

        // Build
        DiffAutomaton<T> result = handler.build(reference, subject);

        // Post processing 
        List<ProcessingActionDTO> postProcessingActions = filterByStage(actions, Stage.POST);
        result = handler.postProcessing(result, postProcessingActions);

        return new BuildResponseDTO(session.getType(), "Build succesfull", handler.serialize(result), actions);
    }

    private List<ProcessingActionDTO> filterByStage(List<ProcessingActionDTO> actions, Stage stage) {
        return actions.stream()
            .filter(action -> action.stage() == stage)
            .toList();
    }   
}