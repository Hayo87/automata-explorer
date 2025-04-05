package io.github.Hayo87.model;
/**
 * Utility class for processing and extracting parts of structured edge labels.
 * <p>
 * Labels are expected to follow the format: <code>input/output</code>,
 * where the input or output may need to be extracted and cleaned for processing
 */

import io.github.Hayo87.type.FilterSubtype;

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
     * @return
     */
    public static String build(String input, String output) {
        if (input.isEmpty() || output.isEmpty()) {
            return input.trim() + output.trim();
        } else {}
        return input.trim() + "/" + output.trim();
    }

    /**
     * Replaces the input part in a label of the form <code>input/output</code>
     * @param label
     * @param newInput
     * @return
     */
    public static String replaceInput(String label, String newInput) {
        String output = extractOutput(label);
        return build(newInput, output);
    }

    /**
     * Replaces the output part in a label of the form <code>input/output</code>
     * @param label
     * @param newOutput
     * @return
     */
    public static String replaceOutput(String label, String newOutput) {
        String input = extractInput(label);
        return build(input, newOutput);
    }

 
    public static String writeSynonymLabel(String label, String synonymName, FilterSubtype subtype) {
        return switch (subtype) {
            case INPUT -> replaceInput(label, "ƒ_in(" + synonymName + ")");
            case OUTPUT -> replaceOutput(label, "ƒ_out(" + synonymName + ")");
            default -> label;
        };
    }
}

