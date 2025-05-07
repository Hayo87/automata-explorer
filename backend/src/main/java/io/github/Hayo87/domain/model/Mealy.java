package io.github.Hayo87.domain.model;

import io.github.Hayo87.domain.rules.LabelUtils;

/**
 * Represents a Mealy transition with an input and an output.
 * 
 * <p> This record can be used a transition property in {@code DiffAutomaton<Mealy>} </p>
 * 
 * @param input the input for the transition
 * @param output, the output for the transition
 */
public class Mealy {
    private final String input;
    private final String output;

    public Mealy(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public String input(){
        return input;
    }

    public String output() {
        return output; 
    }

    /**
     * Writes a string of the form <code>input/output</code>.
     */
    @Override
    public String toString() {
        return LabelUtils.build(input, output);
    } 
}