package io.github.Hayo87.model;

import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffKind;
import com.github.tno.gltsdiff.glts.lts.automaton.diff.DiffProperty;

public class Mealy {
    private final DiffProperty<String> input;
    private final DiffProperty<String> output;
    private final DiffProperty<String> additional;
    private final DualKind dualKind;

    /**
     * Constructor to create a Mealy transition property with a single input and output
     * @param input
     * @param output
     * @param diffkind
     */
    public Mealy(String input, String output, DiffKind diffkind) {
        this.input = new DiffProperty<>(input, diffkind);
        this.output = new DiffProperty<>(output, diffkind); 
        this.additional = null;
        this.dualKind = DualKind.NONE;
    }

    /**
     * Constructor to create  a Mealy transition property with multiple inputs or outputs.
     * The diffkinds should differ for the different inputs or outputs.
     * @param input
     * @param output
     * @param additional
     * @param dualKind
     */
    public Mealy(DiffProperty<String> input, DiffProperty<String> output, DiffProperty<String> additional, DualKind dualKind ) {
        this.input = input;
        this.output = output;
        this.additional = additional;
        this.dualKind = dualKind;
    }

    public DiffProperty<String> getInput() { return input; }
    public DiffProperty<String> getOutput() { return output; }
    public DiffProperty<String> getAdditional() { return additional; }
    public boolean isInputDual() { return dualKind == DualKind.INPUT; }
    public boolean isOutputDual() { return dualKind == DualKind.OUTPUT; }
    public boolean isDual() { return dualKind != DualKind.NONE; }

    public enum DualKind {
        NONE,
        INPUT,
        OUTPUT
    }
}
   
