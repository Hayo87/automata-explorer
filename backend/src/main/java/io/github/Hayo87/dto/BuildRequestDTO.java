package io.github.Hayo87.dto;

import java.util.List;
/**
 * Represents a client request to start a build with certain processing actions.
 */
public record BuildRequestDTO(
    List<ProcessingActionDTO> actions
){}