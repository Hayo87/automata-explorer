package io.github.Hayo87.dto;

import java.util.List;

public class BuildRequestDTO {
    private List<ProcessingActionDTO> filters;

    public List<ProcessingActionDTO> getFilters() {
        return filters == null ? List.of() : filters;
    }
    
    public void setFilters(List<ProcessingActionDTO> filters) {
        this.filters = filters;
    }
}
    