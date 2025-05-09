package io.github.Hayo87.dto;

import java.util.List;

import io.github.Hayo87.domain.rules.ProcessingModel.Stage;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a user requested processing action. The `stage`, `kind` , `type` and `subtype`
 * define the processing rule to be applied. The `name` and value are user inputs. 
 */
public record ProcessingActionDTO (
    @NotNull Stage stage,
    @NotNull Type type,
    @NotNull SubType subtype,
    @NotNull @Min(1) @Max(99) Integer order,
    String name,            // optional
    List<String> values     // optional
    ) {}

    
