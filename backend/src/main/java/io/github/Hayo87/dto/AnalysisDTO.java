package io.github.Hayo87.dto;

import java.util.List;
import java.util.Set;

public record AnalysisDTO(
    List<TwinAnalysis> twins, 
    List<GroupedTwinAnalysis> groupedTwins) {

    public record TwinAnalysis(int left, int right, Set<BuildDTO.Edge> causes) {}
    public record GroupedTwinAnalysis(Set<Integer> members, Set<BuildDTO.Edge> causes) {}
}