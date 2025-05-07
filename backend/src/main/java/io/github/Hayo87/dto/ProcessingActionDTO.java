package io.github.Hayo87.dto;

import java.util.List;

import io.github.Hayo87.domain.rules.ProcessingModel.Stage;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;

/**
 * Represents a user requested processing action. The `stage`, `kind` , `type` and `subtype`
 * define the processing rule to be applied. The `name` and value are user inputs. 
 */
public record ProcessingActionDTO (
    Stage stage,
    Type type,
    SubType subtype,
    int order,
    String name,            // optional
    List<String> values     // optional
    ) {}

    
