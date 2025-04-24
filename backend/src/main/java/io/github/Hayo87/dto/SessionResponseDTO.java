package io.github.Hayo87.dto;

public class SessionResponseDTO {
    private String sessionId;
    private String message;

    public SessionResponseDTO(String sessionId, String message) {
        this.sessionId = sessionId;
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }
    public String getMessage() {
        return message;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}



