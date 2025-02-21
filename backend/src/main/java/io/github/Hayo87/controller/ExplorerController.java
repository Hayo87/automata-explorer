package io.github.Hayo87.controller;

import io.github.Hayo87.service.BuildService;
import io.github.Hayo87.service.SessionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for session management and building diff automata.
 */
@RestController
@RequestMapping("/api")
public class ExplorerController {
    private final SessionService sessionService;
    private final BuildService buildService;

    /**
     * Constructor - Injects required services.
     * 
     * @param sessionService Manages session storage.
     * @param buildService Builds the diff automaton.
     */
    public ExplorerController(SessionService sessionService, BuildService buildService) {
        this.sessionService = sessionService;
        this.buildService = buildService;
    }

    /**
     * Creates a new session and build the input
     *
     * @param input A map containing "reference" and "subject" DOT file content.
     * @return A unique session ID.
     */
    @PostMapping("/session")
    public ResponseEntity<Map<String, String>> createSession(@RequestBody Map<String, String> input) {
        String sessionId = sessionService.createSession();
        buildService.buildInput(sessionId, input.get("reference"), input.get("subject"));

        // Create JSON response
        Map<String, String> response = Map.of("sessionId", sessionId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Initializes the build and returns the DiffMachine in JSON format
     *
     * @param sessionId The session ID.
     * @return The latest diff automaton in JSON format.
     */
    @GetMapping("/session/{sessionId}/build")
    public ResponseEntity<Map<String, Object>> getDiffAsJson(@PathVariable String sessionId) {
        try {
            return ResponseEntity.ok(buildService.buildDefault(sessionId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
}

