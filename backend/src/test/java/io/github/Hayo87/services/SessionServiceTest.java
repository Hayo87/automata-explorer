package io.github.Hayo87.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.Hayo87.controller.BadRequestException;
import io.github.Hayo87.domain.rules.AutomataType;
import io.github.Hayo87.dto.SessionResponseDTO;

public class SessionServiceTest {

    private SessionService sessionService;

    @BeforeEach
    void setup(){
    sessionService = new SessionService();
    }

    @Test
    void createSession_withValidInput_storesSession(){
        String id = sessionService.createSession(AutomataType.STRING, "reference", "subject");
        assertNotNull(id);
        assertDoesNotThrow(() -> sessionService.getSession(id));
    }

    @Test
    void getSession_wihtInvalidID_throwsException(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->  sessionService.getSession("invalid"));
        assertEquals("Session does not exist", e.getMessage());
    }

    @Test
    void terminateSession_withValidId_removesSession(){
        String id = sessionService.createSession(AutomataType.STRING, "reference", "subject");

        SessionResponseDTO response = sessionService.terminateSession(id);
        assertEquals(id + " deleted succesfully", response.sessionId());
    }

    @Test
    void terminateSession_withInvalidId_throwsException(){
        BadRequestException e = assertThrows(BadRequestException.class, () ->  sessionService.terminateSession("invalid"));
        assertEquals("Session does not exist", e.getMessage());
    }
}
