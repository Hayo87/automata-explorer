package io.github.Hayo87.model.MealyTransition;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

public class Mealy {
    private final DiffProperty<String> input;
    private final DiffProperty<String> output;
    private final DiffProperty<String> additionalInput;
    private final boolean dual;

    /**
     * Constructor to create a Mealy transition property with a single input and output
     * @param input
     * @param output
     * @param diffkind
     */
    public Mealy(String input, String output, DiffKind diffkind) {
        this.input = new DiffProperty<>(input, diffkind);
        this.output = new DiffProperty<>(output, diffkind); 
        this.additionalInput = null;
        this.dual = false;
    }

    /**
     * Constructor to create  a Mealy transition property with multiple inputs or outputs.
     * The diffkinds should differ for the different inputs or outputs.
     * @param input
     * @param output
     * @param additional
     * @param dualKind
     */
    public Mealy(DiffProperty<String> input, DiffProperty<String> output, DiffProperty<String> additional) {
        this.input = input;
        this.output = output;
        this.additionalInput = additional;
        this.dual= true;
    }

    public DiffProperty<String> getInput() { return input; }
    public DiffProperty<String> getOutput() { return output; }
    public DiffProperty<String> getAdditionalInput() { return additionalInput; }
    public boolean isDual() { return dual; }

}
   
