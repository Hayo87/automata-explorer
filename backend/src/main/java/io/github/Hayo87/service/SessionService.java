package io.github.Hayo87.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffAutomaton;

import io.github.Hayo87.dto.DeleteSessionResponseDTO;
import io.github.Hayo87.model.SessionData;

/**
 * Manages session creation, storage and deletion
 * 
 * @author Marijn Verheul
 */
@Service
public class SessionService {

    private final Map<String, SessionData> sessions = new HashMap<>();

    public SessionService() {
    }

        /**
     * Creates a new (empty) session
     *
     * @return A string with the unique session ID.
     */
    public String createSession(String dotReference, String dotSubject) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new SessionData(dotReference, dotSubject));
        return sessionId;
        }

    /**
     * Stores reference automata for an existing session.
     *
     * @param sessionId The session ID.
     * @param automaton The automata to be stored.
     */
    public void storeReference(String sessionId, DiffAutomaton<String> reference) {
        SessionData session = getSession(sessionId);
        session.setReference(reference);
    }

    /**
     * Stores subject automata for an existing session.
     *
     * @param sessionId The session ID.
     * @param automaton The automata to be stored.
     */
    public void storeSubject(String sessionId, DiffAutomaton<String> subject) {
        SessionData session = getSession(sessionId);
        session.setSubject(subject);
    }

    /**
     * Stores difference automata for an existing session.
     *
     * @param sessionId The session ID.
     * @param automaton The automata to be stored.
     */
    public void storeDifference(String sessionId, DiffAutomaton<String> automaton) {
        SessionData session = getSession(sessionId);
        session.setDiffAutomaton(automaton);
    }

    /**
     *  Retrieves the reference automata
     *
     * @param sessionId The session ID.
     * @return The reference automata.
     */
    public DiffAutomaton<String> getReferenceAutomata(String sessionId) {
        SessionData session = getSession(sessionId);
        return session.getReference();
    }

    /**
     *  Retrieves the subject automata
     *
     * @param sessionId The session ID.
     * @return The subject automata
     */
    public DiffAutomaton<String> getSubjectAutomata(String sessionId) {
        SessionData session = getSession(sessionId);
        return session.getSubject();
    }
    
    /**
     * Retrieves the latest stored diff automaton from session history.
     *
     * @param sessionId The session ID.
     * @return The latest automaton, or null if no history exists.
     */
    public DiffAutomaton<String> getLatestDiffAutomaton(String sessionId) {
        SessionData session = getSession(sessionId);
        return session.getDiffAutomaton();
    }

    /**
     * Terminates a session by removing it from session history.
     *
     * @param sessionId The ID of the session to be removed.
     * @return DeleteSessionResponseDTO 
     */
    public DeleteSessionResponseDTO terminateSession(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            return new DeleteSessionResponseDTO("Session " + sessionId + " not found");
        }
        else {  
            sessions.remove(sessionId);
            return new DeleteSessionResponseDTO("Session " + sessionId + " deleted successfully.");
        }
    }

    private SessionData getSession (String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new IllegalArgumentException("Session ID not found.");
        }
        return sessions.get(sessionId);
    }
}

