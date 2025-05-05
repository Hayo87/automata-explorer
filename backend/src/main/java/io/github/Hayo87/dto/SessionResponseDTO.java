package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionResponseDTO(
    String sessionId,
    String message,
    List<ProcessingOptionDTO> processingOptions
){}




