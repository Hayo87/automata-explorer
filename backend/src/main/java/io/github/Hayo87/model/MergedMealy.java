package io.github.Hayo87.model;

import io.github.Hayo87.processors.mealy.MealyMerger;

/**
 * Represents a merged Mealy transition with an input and two outputs.
 * 
 * This class is used for additional merging of transitions in {@link MealyMerger} </p>
 * 
 * @param input the input for the transition
 * @param removedOutput the output with diffkind removed
 * @param addedOutput the output with diffkind added 
 */
public class MergedMealy extends Mealy {
    private final String addedOutput;

    public MergedMealy(String input, String removedOutput, String addedOutput){
        super(input, removedOutput);
        this.addedOutput = addedOutput;
    }

    public String addedOutput() {
        return addedOutput;
    }

    /**
     * Writes a string of the form <code>input/removedOutput[R],addedOutput[A]</code>.
     */
    @Override
    public String toString() {
        String outputLabel = super.output() +"[R], " + addedOutput + "[A]";
        return LabelUtils.build(super.input(), outputLabel);
    }
}
