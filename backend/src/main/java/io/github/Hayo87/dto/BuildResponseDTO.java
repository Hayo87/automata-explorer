package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.Hayo87.model.AutomataType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuildResponseDTO (
    AutomataType type,
    String message,
    JsonNode build,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ProcessingActionDTO> filters
){}
  