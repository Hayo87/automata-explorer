package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * 
 * Represents the standard automata build structure to include in a buildResponse.  
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuildDTO(
    List<Node> nodes,
    List<Edge> edges
) {
    public record Node(
        int id,
        NodeAttributes attributes
    ){}

    public record NodeAttributes(
       String label,
       Boolean isInitial,
       String diffkind 
    ) {}

    public record Edge(
        String id,
        int source,
        int target,
        EdgeAttributes attributes
    ) {}

    public record EdgeAttributes(
        String diffkind,
        String labeltext, 
        List<LabelEntry> label
    ){}

    public record LabelEntry(
        LabelType type,          
        String value,
        String diffkind
    ){}

    public enum LabelType {INPUT, OUTPUT, LABEL}

      
}
