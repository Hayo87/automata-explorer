package io.github.Hayo87.model;
/**
 * Utility class for processing and extracting parts of structured edge labels.
 * <p>
 * Labels are expected to follow the format: <code>input/output</code>,
 * where the input or output may need to be extracted and cleaned for processing
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
}
