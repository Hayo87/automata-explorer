package io.github.Hayo87.dto;

import java.util.List;

import io.github.Hayo87.model.Processors.ProcessingModel.Stage;
import io.github.Hayo87.model.Processors.ProcessingModel.SubType;
import io.github.Hayo87.model.Processors.ProcessingModel.Type;
import io.github.Hayo87.type.AutomataType;

/**
 * Represents a user configured processing action. The `stage`, `kind` , `type` and `subtype`
 * define the processing rule to be applied. The `name` and value are user inputs. 
 */

public record ProcessingActionDTO (
    Stage stage,
    AutomataType difftype,
    Type type,
    SubType subType,
    int order,
    String name,            // optional
    List<String> values     // optional
    ) {}

    
