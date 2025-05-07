package io.github.Hayo87.model;

/**
 * Utility class for handeling label formatting and conversion logic related 
 * to automaton labels. 
 */
public class LabelUtils {

    /**
     * Extracts the output part from a label of the form <code>input/output</code>.
     * 
     * @param label in the form "input/output"
     * @return the trimmed output part if present, or the original label
     */
    public static String extractOutput(String label) {
        String[] parts = label.split("/", 2);
        return parts.length == 2 ? parts[1].trim() : label;
    }

    /**
     * Extracts the input part from a label of the form <code>input/output</code>.
     * 
     * @param label in the form "input/output"
     * @return the trimmed input part if present, or the original label
     */
    public static String extractInput(String label) {
        String[] parts = label.split("/", 2);
        return parts.length == 2 ? parts[0].trim() : label;
    }

    /**
     * Build a new label of the form <code>input/output</code>
     * @param input
     * @param output
     * @return a formatted label
     */
    public static String build(String input, String output) {
        if (input.isEmpty() || output.isEmpty()) {
            return input.trim() + output.trim();
        } else {}
        return input.trim() + " / " + output.trim();
    }
    /**
     * Build a new synonymlabel of the form <code>↦ [input]</code>
     * @param input
     * @return a synonym label
     */
    public static String writeSynonymLabel(String input) {
        return "↦ [" + input + "]";
    }
}

