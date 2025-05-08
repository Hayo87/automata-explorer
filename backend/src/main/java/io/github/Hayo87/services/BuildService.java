package io.github.Hayo87.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.controller.BadRequestException;
import io.github.Hayo87.domain.handlers.DiffHandler;
import io.github.Hayo87.domain.rules.AutomataType;
import io.github.Hayo87.domain.rules.ProcessingModel.Stage;
import io.github.Hayo87.domain.rules.ProcessingRules;
import io.github.Hayo87.dto.BuildRequestDTO;
import io.github.Hayo87.dto.BuildResponseDTO;
import io.github.Hayo87.dto.ProcessingActionDTO;
import io.github.Hayo87.dto.ProcessingOptionDTO;

/**
 * Manages the generic, type-independent build process for difference automatons including 
 * processing actions and responses.
 */

 @Service
public class BuildService {
    private final SessionService sessionService;
    private final ParserService parserService; 
    private final HandlerService handlerService;

    @Autowired
    public BuildService( SessionService sessionService, ParserService parserService, HandlerService handlerService ) {
        this.sessionService = sessionService;
        this.parserService = parserService;
        this.handlerService = handlerService;
    }

    /**
     * Get a grouped list of processing options available for the given
     * automata type for the ProcessingRules.
     * @param type the automata type
     * @return grouped list with options
     */
    public List<ProcessingOptionDTO> getProcessingOptions(AutomataType type){
        return ProcessingRules.optionsFor(type);
    }

    /**
     * Builds the input automatons for a given session.
     * 
     * @param sessionId 
     */
    public void buildInputs(String sessionId) {
        SessionData session = sessionService.getSession(sessionId);

        try {
           session.setReference(parserService.convertDotStringToAutomaton(session.getRawReference())); 
        } catch (BadRequestException e) {
            throw new BadRequestException("Invalid reference DOT format", e); 
        }
        try {
           session.setSubject(parserService.convertDotStringToAutomaton(session.getRawSubject())); 
        } catch (BadRequestException e) {
            throw new BadRequestException("Invalid subject DOT format", e); 
        }  
    }

    /**
     * Builds the difference machine for a given session. 
     * @param sessionId
     * @param request {@link BuildRequestDTO}
     * @return {@link BuildResponseDTO}
     */
    public BuildResponseDTO buildDiff(String sessionId, BuildRequestDTO request) {
        SessionData session = sessionService.getSession(sessionId);
        List<ProcessingActionDTO> actions = 
            request.actions() == null? List.of() : request.actions();
    
        DiffHandler<?> handler = handlerService.getHandler(session.getType());
    
        try { 
            return handleDiffBuild(session, handler, actions);
        } catch (Exception e) {
            // TODO
            throw new RuntimeException("Build failed", e); 
        } finally {
        }
    }

    /**
     * Handles the full diff build for a given session using the specified handler.
     * 
     * @param <T> the transition property type
     * @param session the current sessionData {@link SessionData}
     * @param handler the handler for the automaton type 
     * @param actions list of user defined processing actions to apply 
     * @return {@link BuildResponseDTO}
     */
    private <T> BuildResponseDTO handleDiffBuild( SessionData session, DiffHandler<T> handler, List<ProcessingActionDTO> actions) {
        
        try {
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

            return new BuildResponseDTO(session.getType(), handler.serialize(result), actions);
   
        } catch (Exception e) {
            throw new RuntimeException("Build failed: " + e.getMessage(),e);
        }
    }

    /**
     * Helper method to filter the processing actions by stage
     * 
     * @param actions the list of actions to be filtered
     * @param stage the requested stage
     * @return the filtered action list
     */
    private List<ProcessingActionDTO> filterByStage(List<ProcessingActionDTO> actions, Stage stage) {
        return actions.stream()
            .filter(action -> action.stage() == stage)
            .toList();
    }   
}