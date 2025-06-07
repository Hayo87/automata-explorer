package io.github.Hayo87.dto;

import java.util.Set;

public record TwinAnalysisDTO(int left, int right, Set<CauseDTO> causes) {}

