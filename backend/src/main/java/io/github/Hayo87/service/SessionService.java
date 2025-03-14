package io.github.Hayo87.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.DeleteSessionResponseDTO;


/**
 * Manages session storage and history tracking.
 * Each session maintains history for undo/redo functionality.
 */
@Service
public class SessionService {

    private final Map<String, List<DiffAutomaton<String>>> sessionHistory = new ConcurrentHashMap<>();

    public SessionService() {
    }

        /**
     * Creates a new (empty) session
     *
     * @return A unique session ID.
     */
    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        List<DiffAutomaton<String>> history = new ArrayList<>();
        sessionHistory.put(sessionId, history);
        return sessionId;
        }

    /**
     * Stores automata for an existing session.
     *
     * @param sessionId The session ID.
     * @param automaton The automata to be stored.
     */
    public void store(String sessionId, DiffAutomaton<String> automaton) {
        if (!sessionHistory.containsKey(sessionId)) {
            throw new IllegalArgumentException("Session ID not found.");
        }
        sessionHistory.get(sessionId).add(automaton);
    }

    /**
     * Retrieves a specific automata from session history.
    *
    * @param sessionId The session ID.
    * @param index The index of the stored automaton.
    * @return The requested automaton, or null if not found.
    */
    private DiffAutomaton<String> getAutomaton(String sessionId, int index) {
        List<DiffAutomaton<String>> history = sessionHistory.get(sessionId);

        if (history == null || index < 0 || index >= history.size()) {
            return null;
        }
        return history.get(index);
    }

    /**
     *  Retrieves the reference automata
     *
     * @param sessionId The session ID.
     * @return The reference automata.
     */
    public DiffAutomaton<String> getReferenceAutomata(String sessionId) {
        return getAutomaton( sessionId, 0);
    }

    /**
     *  Retrieves the subject automata
     *
     * @param sessionId The session ID.
     * @return The subject automata
     */
    public DiffAutomaton<String> getSubjectAutomata(String sessionId) {
        return getAutomaton( sessionId, 1);
    }
    
    /**
     * Retrieves the latest stored diff automaton from session history.
     *
     * @param sessionId The session ID.
     * @return The latest automaton, or null if no history exists.
     */
    public DiffAutomaton<String> getLatestDiffAutomaton(String sessionId) {
        List<DiffAutomaton<String>> history = sessionHistory.get(sessionId);

        if (history == null || history.isEmpty() || history.size() < 3) {
            return null;
        }
        return history.get(history.size() - 1); // Get the last element
    }

    /**
     * Terminates a session by removing it from session history.
     *
     * @param sessionId The ID of the session to be removed.
     * @return DeleteSessionResponseDTO 
     */
    public DeleteSessionResponseDTO terminateSession(String sessionId) {
        if (!sessionHistory.containsKey(sessionId)) {
            return new DeleteSessionResponseDTO("Session " + sessionId + " not found");
        }
        else {  
            return new DeleteSessionResponseDTO("Session " + sessionId + " deleted successfully.");
        }
    }
}

