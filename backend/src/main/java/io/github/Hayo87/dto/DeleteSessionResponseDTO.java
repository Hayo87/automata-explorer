package io.github.Hayo87.dto;

public class DeleteSessionResponseDTO {
    private String message;

    public DeleteSessionResponseDTO() {
    }

    public DeleteSessionResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
