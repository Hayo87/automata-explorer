package io.github.Hayo87.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.operators.hiders.SubstitutionHider;

import io.github.Hayo87.dto.BuildRequestDTO;
import io.github.Hayo87.dto.BuildResponseDTO;
import io.github.Hayo87.dto.MatchResultDTO;

/**
 * Manages all build related actions and information request 
 * for Difference Automata's.
 * 
/** */
 @Service
public class BuildService {
    private final SessionService sessionService;
    private final ParserService parserService;
    private final MatchService matchService; 

    public BuildService(SessionService sessionService, ParserService parserService, MatchService matchService) {
        this.sessionService = sessionService;
        this.parserService = parserService;
        this.matchService = matchService;
    }

    /**
     * General method to process build actions.
     * 
     * @param sessionId the session ID for the build action.
     * @param request the BuildRequestDTO with the build action and build data.
     * @return result of the action wrapped in a responseDTO.
     */
    public BuildResponseDTO processBuildAction(String sessionId, BuildRequestDTO request) {
        String action = request.getAction().toLowerCase();
        Object data = request.getData();

        switch (action) {

            case "reference" -> {
                buildInput(sessionId, (String) data, true);
                return new BuildResponseDTO("reference", "success", "Reference processed successfully");
            }

            case "subject" -> {
                buildInput(sessionId, (String) data, false);
                return new BuildResponseDTO("subject", "success", "Subject processed successfully");
            }

            case "build" -> {
                Object buildData = buildDefault(sessionId); 
                return new BuildResponseDTO("build", "success", "Build succesfull", buildData);
            }

            case "match" -> {
                Object matchData = match(sessionId);
                return new BuildResponseDTO("match", "success", "DiffMachine differences matched", matchData);
            }

            case "filter" -> {
                // apply filters
                Object buildData = buildDefault(sessionId);;
                return new BuildResponseDTO("build", "success", "Build succesfull", buildData);
            }

            default -> {
                return new BuildResponseDTO(action, "Error processing", "Invalid action");
            }
        }
    }

    /**
     * Build an input (difference) automaton based on the input and 
     * stores the result in the session history.
     * 
     * @param sessionId the sessionID for the build
     * @param input the input string
     * @param isReference wheter the input is the reference automata
     */
    private void buildInput(String sessionId, String input, Boolean isReference){
        try{
            // Attemp parse and add to session history
            if (isReference){
                sessionService.storeReference(sessionId, parserService.convertDotStringToDiffAutomaton(input, isReference));   
            } else {
                sessionService.storeSubject(sessionId, parserService.convertDotStringToDiffAutomaton(input, isReference));
            }

        } catch (IOException e) {
            System.err.println("Parsing failed: " + e.getMessage());
            throw new IllegalArgumentException("Invalid DOT file format.");
        }
    }

    /**
     * Builds the default differenceAutomaton based on the sessions intput
     * @param sessionId
     * @return the JSON reprententation or the empty string in case of an error
     */

    public JsonNode buildDefault(String sessionId) {
        DiffAutomaton<String> reference = sessionService.getReferenceAutomata(sessionId);
        DiffAutomaton<String> subject = sessionService.getSubjectAutomata(sessionId);

        // Configure comparison, merging, and writing.
        DiffAutomatonStructureComparatorBuilder<String> builder = new DiffAutomatonStructureComparatorBuilder<>();
        builder.setDiffAutomatonTransitionPropertyHider(new SubstitutionHider<>(""));
        var comparator = builder.createComparator();
        var writer = builder.createWriter();

        // Apply structural comparison to the two input automata and store
        DiffAutomaton<String> result = comparator.compare(reference, subject);
        sessionService.storeDifference(sessionId,result);

        // Delegate processing to parserService
        return parserService.convertJsonToDiffAutomaton(result, writer); 
    }

    /** 
    * Reviews the changes in the DIFF machines and checks with differences are 
    * possibly related using matchers and scores. 
    * @param sessionId
    * @return the JSON reprententation or the empty string in case of an error
    */
    public MatchResultDTO match(String sessionId) {
        DiffAutomaton<String> diffMachine = sessionService.getLatestDiffAutomaton(sessionId);
        return matchService.match(diffMachine); 
    }        
}