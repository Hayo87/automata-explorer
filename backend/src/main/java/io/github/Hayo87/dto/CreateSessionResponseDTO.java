package io.github.Hayo87.dto;

public class CreateSessionResponseDTO {
    private String sessionId;

    public CreateSessionResponseDTO(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

