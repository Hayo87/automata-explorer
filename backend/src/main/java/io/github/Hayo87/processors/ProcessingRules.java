package io.github.Hayo87.processors;

import java.util.List;

import io.github.Hayo87.model.AutomataType;
import io.github.Hayo87.processors.ProcessingModel.Stage;
import io.github.Hayo87.processors.ProcessingModel.SubType;
import io.github.Hayo87.processors.ProcessingModel.Type;
import io.github.Hayo87.processors.ProcessingModel.processingRule;

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
