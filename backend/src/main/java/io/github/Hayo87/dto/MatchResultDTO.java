package io.github.Hayo87.dto;

import java.io.Serializable;

public class MatchResultDTO implements Serializable {
    private final int groups;  // Total number of matched groups
    private final Object data; // Holds the actual structured response

    public MatchResultDTO(int groups, Object data) {
        this.groups = groups;
        this.data = data;
    }

    public int getGroups() { return groups; }
    public Object getData() { return data; }
}
