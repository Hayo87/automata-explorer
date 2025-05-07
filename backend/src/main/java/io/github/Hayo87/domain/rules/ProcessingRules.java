package io.github.Hayo87.domain.rules;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.Hayo87.domain.rules.ProcessingModel.ProcessingRule;
import io.github.Hayo87.domain.rules.ProcessingModel.Stage;
import io.github.Hayo87.domain.rules.ProcessingModel.SubType;
import io.github.Hayo87.domain.rules.ProcessingModel.Type;
import io.github.Hayo87.dto.ProcessingOptionDTO;

/**
 * Provides a centralize list of valid {@ ProcessingRule} combinations for processing logic. 
 */
public final class ProcessingRules {
    public static final List<ProcessingRule> ALL_RULES = List.of(new ProcessingRule(Stage.PRE, AutomataType.STRING, Type.HIDER, SubType.LOOP),
        new ProcessingRule(Stage.POST, AutomataType.STRING, Type.HIDER, SubType.LOOP),
        new ProcessingRule(Stage.PRE, AutomataType.STRING, Type.HIDER, SubType.LABEL),
        new ProcessingRule(Stage.POST, AutomataType.STRING, Type.HIDER, SubType.LABEL),

        new ProcessingRule(Stage.PRE, AutomataType.MEALY, Type.HIDER, SubType.LOOP),
        new ProcessingRule(Stage.POST, AutomataType.MEALY, Type.HIDER, SubType.LOOP),
        new ProcessingRule(Stage.PRE, AutomataType.MEALY, Type.SYNONYM, SubType.INPUT),
        new ProcessingRule(Stage.PRE, AutomataType.MEALY, Type.SYNONYM, SubType.OUTPUT),
        new ProcessingRule(Stage.PRE, AutomataType.MEALY, Type.HIDER, SubType.INPUT),
        new ProcessingRule(Stage.PRE, AutomataType.MEALY, Type.HIDER, SubType.OUTPUT),
        new ProcessingRule(Stage.POST, AutomataType.MEALY, Type.HIDER, SubType.INPUT),
        new ProcessingRule(Stage.POST, AutomataType.MEALY, Type.HIDER, SubType.OUTPUT)
    );

    private ProcessingRules(){} // prevent instantiation, utility class

    /**
     * Get a grouped list of processing options available for the given
     * automata type.
     * @param type automata type
     * @return grouped list of matching rules
     */
    public static List<ProcessingOptionDTO> optionsFor(AutomataType type) {
        return forType(type).stream()
            .collect(Collectors.groupingBy(rule -> Map.entry(rule.stage(), rule.type()),
                Collectors.mapping(ProcessingRule::subType, Collectors.toList())
                ))
                .entrySet().stream()
                .map(entry -> new ProcessingOptionDTO(
                    entry.getKey().getKey(),
                    entry.getKey().getValue(),
                    entry.getValue()
                ))
                .toList();
    }

    /**
     * Returns all processing rules for the given automata type
     * @param type automata type
     * @return list of matching rules
     */
    public static List<ProcessingRule> forType(AutomataType type){
        return ALL_RULES.stream()
            .filter(rule -> rule.difftype() == type)
            .toList();
    }
}
