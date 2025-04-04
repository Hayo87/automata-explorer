package io.github.Hayo87.dto;

import java.util.List;

import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

public class FilterActionDTO {
    private FilterType type;
    private FilterSubtype subtype;      
    private String name;      
    private List<String> values;
    private int order; 

    public FilterActionDTO() {}

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public FilterSubtype getSubtype() {
        return subtype;
    }

    public void setSubType(FilterSubtype subtype) {
        this.subtype = subtype;
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
