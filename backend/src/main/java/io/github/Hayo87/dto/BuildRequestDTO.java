package io.github.Hayo87.dto;

import java.util.List;

public class BuildRequestDTO {
    private List<FilterActionDTO> filters;

    public List<FilterActionDTO> getFilters() {
        return filters == null ? List.of() : filters;
    }
    
    public void setFilters(List<FilterActionDTO> filters) {
        this.filters = filters;
    }
}
    