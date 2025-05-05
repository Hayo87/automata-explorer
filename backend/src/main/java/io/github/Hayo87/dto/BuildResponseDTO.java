package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.Hayo87.type.AutomataType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildResponseDTO {
    private AutomataType type;
    private String message;
    private JsonNode build;
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ProcessingActionDTO> filters;

    public BuildResponseDTO(String message){
        this(null, message, null);
    }

    public BuildResponseDTO(AutomataType type, String message){
        this(type, message, null);
    }

    public BuildResponseDTO(AutomataType type, String message, JsonNode build) {
        this(type, message, build, null);
    }

    public BuildResponseDTO(AutomataType type,  String message, JsonNode build ,List<ProcessingActionDTO> filters ) {
        this.type = type;
        this.message = message;
        this.build = build;
        this.filters = filters;  
    }

    public AutomataType getType() { return type; }
    public void setStatus(AutomataType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public JsonNode getBuild() { return build; }
    public void setBuild(JsonNode build) { this.build = build; }

    public List<ProcessingActionDTO> getFilters() {return this.filters;}
    public void setFilters (List<ProcessingActionDTO> filters) {
        this.filters = filters;
    }

}
