package io.github.Hayo87.dto;

import java.util.List;

public class FilterActionDTO {
    private String type;      
    private String name;      
    private List<String> values;

    public FilterActionDTO() {}

    public FilterActionDTO(String type, String name, List<String> values) {
        this.type = type;
        this.name = name;
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
