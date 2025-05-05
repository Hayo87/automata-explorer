package io.github.Hayo87.model;

/**
 * Represents a Mealy transition with an input and an output.
 * 
 * <p> This record can be used a transition property in {@code DiffAutomaton<Mealy>} </p>
 * 
 * @param input the input for the transition
 * @param output, the output for the transition
 */
public record Mealy(String input, String output) {

    @Override
    public String toString() {
        return LabelUtils.build(input, output);
    }
}