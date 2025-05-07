package io.github.Hayo87.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.Hayo87.dto.SessionResponseDTO;
import io.github.Hayo87.model.AutomataType;

/**
 * Manages session creation, storage and deletion
 * 
 */
@Service
public class SessionService {
    private final Map<String, SessionData> sessions = new HashMap<>();

    public SessionService() {
    }

    /**
     * Creates a new session, by storing the session inputs.
     *
     * @return string with the unique session ID.
     */
    public String createSession(AutomataType type, String dotReference, String dotSubject) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new SessionData(type, dotReference, dotSubject));
        return sessionId;
        }

    /**
     * Retrieves the sessionData object from the session storage
     * @param sessionId
     * @return the sessionData object
     */
    public SessionData getSession (String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            throw new IllegalArgumentException("Session ID not found.");
        }
        return sessions.get(sessionId);
    }

    /**
     * Terminates a session by removing it from session history.
     *
     * @param sessionId The ID of the session to be removed.
     * @return DeleteSessionResponseDTO 
     */
    public SessionResponseDTO terminateSession(String sessionId) {
        if (!sessions.containsKey(sessionId)) {
            return new SessionResponseDTO(sessionId, " Session not found", null);
        }
        else {  
            sessions.remove(sessionId);
            return new SessionResponseDTO( sessionId, " Deleted succesfully", null);
        }
    }
}

