package io.github.Hayo87.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.lts.automaton.Automaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomata;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.operators.hiders.SubstitutionHider;

import io.github.Hayo87.dto.BuildRequestDTO;
import io.github.Hayo87.dto.BuildResponseDTO;
import io.github.Hayo87.dto.FilterActionDTO;
import io.github.Hayo87.model.SessionData;
import io.github.Hayo87.type.BuildType;

/**
 * Manages all build related actions for Difference Automata's.
 * 
/** */
 @Service
public class BuildService {
    private final SessionService sessionService;
    private final ParserService parserService; 
    private final FilterService filterService;
    private final ObjectMapper mapper;

    @Autowired
    public BuildService( SessionService sessionService, ParserService parserService, FilterService filterService, ObjectMapper mapper) {
        this.sessionService = sessionService;
        this.parserService = parserService;
        this.filterService = filterService;
        this.mapper = mapper;
    }

    /**
     * General method to process build actions.
     * 
     * @param sessionId the session ID for the build action.
     * @param request the BuildRequestDTO with the build action and build data.
     * @return result of the action wrapped in a responseDTO.
     */
    public BuildResponseDTO processBuildAction(String sessionId, BuildRequestDTO request) {
        BuildType type = request.getAction();
        SessionData session = sessionService.getSession(sessionId);

        switch (type) {

            case INPUTS -> {
                session.getLock().lock();
    
                try {
                    session.setReference(buildInput(session.getInputReference()));
                    session.setSubject(buildInput(session.getInputSubject()));
            
                } catch (Exception e) {
                    System.err.println("Rebuilding inputs failed: " + e.getMessage());
                    session.setReady(false);
                    return new BuildResponseDTO("inputs", "error", "Input parsing failed: " + e.getMessage());
                } finally {
                    session.getLock().unlock();
                }
                return new BuildResponseDTO("inputs", "success", "Inputs processed successfully");
            }

            case BUILD  -> {
                DiffAutomaton<String> result = null;
                List<FilterActionDTO> filters = request.getFilters() == null
                    ? List.of()
                    : mapper.convertValue(request.getFilters(), new TypeReference<>() {});

                session.getLock().lock();

                try{ 
                    result = buildDefault(session, filters);
                }catch (Exception e) {
                    sessionService.terminateSession(sessionId);
                } finally {
                    session.getLock().unlock(); 
                }
                return new BuildResponseDTO("build", "success", "Build succesfull", result, filters);
            }
            default -> throw new IllegalStateException("Unhandled BuildType: " + type);
        }
    }

    /**
     * Build an input (difference) automaton based on the input and 
     * stores the result in the session history.
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

    /**
     * Builds the default differenceAutomaton based on the sessions intput
     * @param session the sessionData object for the build 
     * @return the JSON reprententation or the empty string in case of an error
     */

    public DiffAutomaton<String> buildDefault(SessionData session, List<FilterActionDTO> filters) {
        DiffAutomaton<String> reference = DiffAutomata.fromAutomaton(session.getReference(), DiffKind.REMOVED);
        DiffAutomaton<String> subject = DiffAutomata.fromAutomaton(session.getSubject(), DiffKind.ADDED);
        
        // Perform pre processing filters
        //filterService.preProcessing(session, ??);

        // Configure comparison, merging, and writing.
        DiffAutomatonStructureComparatorBuilder<String> builder = new DiffAutomatonStructureComparatorBuilder<>();
        builder.setDiffAutomatonTransitionPropertyHider(new SubstitutionHider<>(""));
        var comparator = builder.createComparator();

        // Apply structural comparison to the two input automata and store
        DiffAutomaton<String> result = comparator.compare(reference, subject);
        session.setDiffAutomaton(result);

        // Delegate processing to parserService
        return result;
    }
      
}