package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildResponseDTO {
    private String action;
    private String status;
    private String message;
    private JsonNode build;
    private List<FilterActionDTO> filters;

    public BuildResponseDTO(String status, String message){
        this(null, status, message, null, null);
    }

    public BuildResponseDTO(String action, String status, String message) {
        this(action, status, message, null, null);
    }

    public BuildResponseDTO(String action, String status, String message, JsonNode build) {
        this(action, status, message, build, null);
    }

    public BuildResponseDTO(String action, String status, String message, JsonNode build ,List<FilterActionDTO> filters ) {
        this.action = action;
        this.status = status;
        this.message = message;
        this.build = build;
        this.filters = filters; 
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public JsonNode getBuild() { return build; }
    public void setData(JsonNode data) { this.build = data; }

    public List<FilterActionDTO> getFilters() {return this.filters;}
    public void setFilters (List<FilterActionDTO> filters) {
        this.filters = filters;
    }

}
