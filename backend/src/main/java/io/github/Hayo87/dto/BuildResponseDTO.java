package io.github.Hayo87.dto;

public class BuildResponseDTO {
    private String action;
    private String status;
    private String message;
    private Object data;

    public BuildResponseDTO(String status, String message){
        this.status = status;
        this.message = message;
    }

    public BuildResponseDTO(String action, String status, String message) {
        this.action = action;
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public BuildResponseDTO(String action, String status, String message, Object data) {
        this.action = action;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
