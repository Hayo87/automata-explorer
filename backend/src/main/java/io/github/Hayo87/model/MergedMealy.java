package io.github.Hayo87.model;

public class MergedMealy extends Mealy {
    private final String addedOutput;

    public MergedMealy(String input, String removedOutput, String addedOutput){
        super(input, removedOutput);
        this.addedOutput = addedOutput;
    }

    public String addedOutput() {
        return addedOutput;
    }
}
