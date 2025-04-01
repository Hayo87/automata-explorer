package io.github.Hayo87.dto;

import java.util.List;

import io.github.Hayo87.type.BuildType;

public class BuildRequestDTO {
    private BuildType action;
    private String input;
    private List<FilterActionDTO> filters; 

    public BuildRequestDTO() {}

    public BuildRequestDTO(BuildType action, String input) {
        this.action = action;
        this.input = input;
    }

    public BuildType getAction() {
        return action;
    }

    public void setAction(BuildType action) {
        this.action = action;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public List<FilterActionDTO> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterActionDTO> filters) {
        this.filters = filters;
    }
}


