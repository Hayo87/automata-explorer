package io.github.Hayo87.dto;

import java.io.Serializable;

public class MatchResultDTO implements Serializable {
    private final int groups;  
    private final Object data;

    public MatchResultDTO(int groups, Object data) {
        this.groups = groups;
        this.data = data;
    }

    public int getGroups() { return groups; }
    public Object getData() { return data; }
}
