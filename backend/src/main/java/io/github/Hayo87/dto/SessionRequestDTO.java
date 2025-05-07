package io.github.Hayo87.dto;

import io.github.Hayo87.domain.rules.AutomataType;

/**
 * Represents a client request to start a new session.
 * 
 * @param type          The type of automaton for the session
 * @param reference     The reference input(dot format)
 * @param subject       The subject input (dot format)
 */
public record SessionRequestDTO (
    AutomataType type,
    String reference,
    String subject
){}

