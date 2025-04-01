package io.github.Hayo87.dto;

import java.util.List;

import io.github.Hayo87.type.FilterSubtype;
import io.github.Hayo87.type.FilterType;

public class FilterActionDTO {
    private FilterType type;
    private FilterSubtype subtype;      
    private String name;      
    private List<String> values;

    public FilterActionDTO() {}

    public FilterActionDTO(FilterType type, FilterSubtype subtype, String name, List<String> values) {
        this.type = type;
        this.subtype = subtype;
        this.name = name;
        this.values = values;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public FilterSubtype getSubType() {
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
