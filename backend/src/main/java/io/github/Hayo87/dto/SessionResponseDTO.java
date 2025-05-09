package io.github.Hayo87.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response on a client session request. 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionResponseDTO(
    String sessionId,
    List<ProcessingOptionDTO> processingOptions
){}




