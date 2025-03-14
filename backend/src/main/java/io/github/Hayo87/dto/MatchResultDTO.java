package io.github.Hayo87.dto;

public class MatchResultDTO {
    private final int groups;  
    private final Object data; 

    public MatchResultDTO(int groups, Object data) {
        this.groups = groups;
        this.data = data;
    }

    public int getGroups() { return groups; }
    public Object getData() { return data; }
}
