package io.github.Hayo87.controller;

import java.util.List;

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
import io.github.Hayo87.dto.ProcessingOptionDTO;
import io.github.Hayo87.dto.SessionRequestDTO;
import io.github.Hayo87.dto.SessionResponseDTO;
import io.github.Hayo87.services.BuildService;
import io.github.Hayo87.services.SessionService;
import jakarta.validation.Valid;

/**
 * REST Controller for session management, and the building and exploring of 
 * the difference automata(Diff Machine) based on the gLTSDiff library.
 * 
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
     */
    public ExplorerController(SessionService sessionService, BuildService buildService) {
        this.sessionService = sessionService;
        this.buildService = buildService;
    }

    /**
     * Creates a new session and builds the input.
     *
     * @param input {@link SessionRequestDTO} 
     * @return {@link SessionResponseDTO} 
     */
    @PostMapping("/session")
    public ResponseEntity<SessionResponseDTO> createSession( @RequestBody @Valid SessionRequestDTO input) {

        String sessionId = sessionService.createSession(input.type(), input.reference(), input.subject());

        try {
            buildService.buildInputs(sessionId);
        } catch (Exception e) {
            sessionService.terminateSession(sessionId);
            throw e;
        }
        
        List<ProcessingOptionDTO> options = buildService.getProcessingOptions(input.type());

        return ResponseEntity.ok(new SessionResponseDTO(sessionId, options));
    }

    /**
     * Deletes a session and clears its data.
     *
     * @param sessionId The ID of the session to be deleted.
     * @return {@link SessionResponseDTO}
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<SessionResponseDTO> deleteSession(@PathVariable String sessionId) {        
        return ResponseEntity.ok(sessionService.terminateSession(sessionId)); 
    }

    /**
     * Handles the build for a given session.
     *
     * @param sessionId The unique identifier of the session.
     * @param request {@link BuildRequestDTO}
     * @return {@link BuildResponseDTO}
     */
    @PostMapping("/session/{sessionId}/build")
    public ResponseEntity<BuildResponseDTO> handleBuildRequest(
            @PathVariable String sessionId, 
            @RequestBody @Valid BuildRequestDTO request
            ) {

        try {
            BuildResponseDTO response = buildService.buildDiff(sessionId, request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            sessionService.terminateSession(sessionId);
            throw e;
        }
    }
}

