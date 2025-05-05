package io.github.Hayo87.model.Processors;

import io.github.Hayo87.model.Processors.ProcessingModel.*;
import io.github.Hayo87.type.AutomataType;

import java.util.List;

/**
 * Provides a centralize list of valid {@ ProcessingRule} combinations for processing logic. 
 */
public final class ProcessingRules {
    public static final List<processingRule> ALL_RULES = List.of(
        new processingRule(Stage.PRE, AutomataType.STRING, Type.HIDER, SubType.LOOP),
        new processingRule(Stage.POST, AutomataType.STRING, Type.HIDER, SubType.LOOP),
        new processingRule(Stage.PRE, AutomataType.STRING, Type.HIDER, SubType.LABEL),
        new processingRule(Stage.POST, AutomataType.STRING, Type.HIDER, SubType.LABEL),

        new processingRule(Stage.PRE, AutomataType.MEALY, Type.HIDER, SubType.LOOP),
        new processingRule(Stage.POST, AutomataType.MEALY, Type.HIDER, SubType.LOOP),
        new processingRule(Stage.PRE, AutomataType.MEALY, Type.SYNONYM, SubType.INPUT),
        new processingRule(Stage.PRE, AutomataType.MEALY, Type.SYNONYM, SubType.OUTPUT),
        new processingRule(Stage.PRE, AutomataType.MEALY, Type.HIDER, SubType.INPUT),
        new processingRule(Stage.PRE, AutomataType.MEALY, Type.HIDER, SubType.OUTPUT),
        new processingRule(Stage.POST, AutomataType.MEALY, Type.HIDER, SubType.INPUT),
        new processingRule(Stage.POST, AutomataType.MEALY, Type.HIDER, SubType.OUTPUT)
    );

    private ProcessingRules(){} // prevent instantiation, utility class
}
