package io.github.Hayo87.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.Hayo87.dto.BuildRequestDTO;
import io.github.Hayo87.dto.BuildResponseDTO;
import io.github.Hayo87.dto.SessionRequestDTO;
import io.github.Hayo87.dto.SessionResponseDTO;
import io.github.Hayo87.service.BuildService;
import io.github.Hayo87.service.SessionService;

/**
 * REST Controller for session management, and the building and exploring of 
 * Difference Automata(Diff Machine) based on the gLTSDiff library.
 * 
 * @author Marijn Verheul
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ExplorerController {
    private final SessionService sessionService;
    private final BuildService buildService;

    /**
     * Constructor - Injects required services.
     * 
     * @param sessionService Manages session storage.
     * @param buildService Builds diff automaton.
     */
    public ExplorerController(SessionService sessionService, BuildService buildService) {
        this.sessionService = sessionService;
        this.buildService = buildService;
    }

   /**
     * Creates a new session and builds the input.
     *
     * @param input A JSON object containing "reference" and "subject" DOT file content.
     * @return A unique session ID wrapped in a DTO.
     */
    @PostMapping("/session")
    public ResponseEntity<SessionResponseDTO> createSession(@RequestBody SessionRequestDTO input) {
        String sessionId = sessionService.createSession(input.getType(), input.getReference(), input.getSubject());

        // Parse inputs in background
        CompletableFuture.runAsync(() -> {
            buildService.buildInputs(sessionId);
        });

        return ResponseEntity.ok(new SessionResponseDTO(sessionId, "Created"));
    }

    /**
     * Deletes a session and clears its history.
     *
     * @param sessionId The ID of the session to be deleted.
     * @return ResponseEntity (idempotent, always succes).
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<SessionResponseDTO> deleteSession(@PathVariable String sessionId) {        
        return ResponseEntity.ok(sessionService.terminateSession(sessionId)); 
    }

    /**
     * Handles build-related actions for a given session.
     *
     * @param sessionId The unique identifier of the session.
     * @param request The {@link BuildRequestDTO} containing the action to perform.
     * @return A {@link ResponseEntity} indicating the success or failure of the action.
     *         - HTTP 200 OK → Action executed successfully, results returned in DTO.
     *         - HTTP 400 Bad Request → Invalid action provided.
     */

    @PostMapping("/session/{sessionId}/build")
    public ResponseEntity<BuildResponseDTO> handleBuildRequest(
            @PathVariable String sessionId, 
            @RequestBody BuildRequestDTO request) {

        try {
            BuildResponseDTO response = buildService.buildDiff(sessionId, request);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            sessionService.terminateSession(sessionId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BuildResponseDTO( "Error processing: " +  e.getMessage()));
        }
    }
}

