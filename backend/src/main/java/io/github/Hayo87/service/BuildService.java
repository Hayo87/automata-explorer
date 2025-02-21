package io.github.Hayo87.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.builders.lts.automaton.diff.DiffAutomatonStructureComparatorBuilder;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;
import com.github.tno.gltsdiff.operators.hiders.SubstitutionHider;

@Service
public class BuildService {
    private final SessionService sessionService;
    private final ParserService parserService;

    public BuildService(SessionService sessionService, ParserService parserService) {
        this.sessionService = sessionService;
        this.parserService = parserService;
    }

    
    /**
     * Generates the input differenceAutomaton from the DOT files.
     * 
     * @param sessionId
     */
    public void buildInput(String sessionId, String reference, String subject){
        try{
            // Attempt to parse DOT files
            DiffAutomaton<String> refAutomaton = parserService.parseToDiffAutomaton(reference, true);
            DiffAutomaton<String> subAutomaton = parserService.parseToDiffAutomaton(subject, false);

            // Add to session history
            sessionService.store(sessionId, refAutomaton);   
            sessionService.store(sessionId, subAutomaton);      

        } catch (Exception e) {
            System.err.println("Parsing failed: " + e.getMessage());
            throw new IllegalArgumentException("Invalid DOT file format.");
        }
    }

  
    /**
     * Builds the default differenceAutomaton based on the sessions intput
     * @param sessionId
     * @return the JSON reprententation or the empty string in case of an error
     */

    public Map<String,Object> buildDefault(String sessionId) {
        DiffAutomaton<String> reference = sessionService.getReferenceAutomata(sessionId);
        DiffAutomaton<String> subject = sessionService.getSubjectAutomata(sessionId);

        // Configure comparison, merging, and writing.
        DiffAutomatonStructureComparatorBuilder<String> builder = new DiffAutomatonStructureComparatorBuilder<>();
        builder.setDiffAutomatonTransitionPropertyHider(new SubstitutionHider<>("[skip]"));
        var comparator = builder.createComparator();

        // Apply structural comparison to the two input automata.
        DiffAutomaton<String> result = comparator.compare(reference, subject);

        // Put in session history
        sessionService.store(sessionId,result);

        return parserService.convertToJson(result);
    }
}