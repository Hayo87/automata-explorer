package io.github.Hayo87.dto;

import java.util.List;

import io.github.Hayo87.domain.rules.ProcessingModel.Stage;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;

/**
 * Represents a grouped processing option for a stage and type.
 * 
 */
public record ProcessingOptionDTO(
    Stage stage,
    Type type,
    List<SubType> subtypes
) {}
