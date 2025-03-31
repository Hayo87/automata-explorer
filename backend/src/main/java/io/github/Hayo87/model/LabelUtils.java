package io.github.Hayo87.model;

public class LabelUtils {

    public static String extractOutput(String label) {
        String[] parts = label.split("/", 2);
        return parts.length == 2 ? parts[1] : label;
    }

    public static String extractInput(String label) {
        String[] parts = label.split("/", 2);
        return parts.length == 2 ? parts[0] : label;
    }
}
