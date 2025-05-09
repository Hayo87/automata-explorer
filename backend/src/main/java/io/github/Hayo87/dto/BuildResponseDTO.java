package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.Hayo87.domain.rules.AutomataType;
/**
 * Represents a response on a client initiated build request. 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuildResponseDTO (
    AutomataType type,
    BuildDTO build,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ProcessingActionDTO> actions
){}
  