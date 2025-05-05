package io.github.Hayo87.model.Processors;

import io.github.Hayo87.type.AutomataType;

/**
 * Contains all enums and data structurse for processing actions.
 * 
 */
public class ProcessingModel {

    /**
     * Indicates when the processing action is applied: before(PRE) of after(POST) the build. 
     */
    public enum Stage{PRE, POST}

    /**
     * The general category of the a processing action. 
     */
    public enum Type{SYNONYM, HIDER}

    /**
     * The subcategory of the a processing action. 
     */
    public enum SubType{INPUT, OUTPUT, LOOP, LABEL}

    /**
     * A valid combination for processing consisting of:
     * 
     * @param stage     The processing stage
     * @param difftype  The automaton type
     * @param type      The processing category
     * @param subtype   the processing sub-category
     */
    public record processingRule(Stage stage, AutomataType difftype, Type type, SubType subType) {}

    private ProcessingModel() {} // prevent instantiation
}
