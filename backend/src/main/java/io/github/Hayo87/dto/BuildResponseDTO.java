package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.Hayo87.type.DiffType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildResponseDTO {
    private DiffType type;
    private String message;
    private JsonNode build;
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FilterActionDTO> filters;

    public BuildResponseDTO(DiffType type, String message){
        this(type, message, null);
    }

    public BuildResponseDTO(DiffType type, String message, JsonNode build) {
        this(type, message, build, null);
    }

    public BuildResponseDTO(DiffType type,  String message, JsonNode build ,List<FilterActionDTO> filters ) {
        this.type = type;
        this.message = message;
        this.build = build;
        this.filters = filters;  
    }

    public DiffType getType() { return type; }
    public void setStatus(DiffType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public JsonNode getBuild() { return build; }
    public void setBuild(JsonNode build) { this.build = build; }

    public List<FilterActionDTO> getFilters() {return this.filters;}
    public void setFilters (List<FilterActionDTO> filters) {
        this.filters = filters;
    }

}
