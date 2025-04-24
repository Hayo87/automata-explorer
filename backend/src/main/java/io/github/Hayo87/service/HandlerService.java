package io.github.Hayo87.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.Hayo87.model.Handlers.DiffHandler;
import io.github.Hayo87.model.Handlers.MealyDiffHandler;
import io.github.Hayo87.model.Handlers.StringDiffHandler;
import io.github.Hayo87.type.DiffType;

@Service
public class HandlerService {

    private final Map<DiffType, DiffHandler<?>> handlerMap;

    public HandlerService(StringDiffHandler stringHandler, MealyDiffHandler mealyHandler) {
        this.handlerMap = Map.of(
            DiffType.STRING, stringHandler,
            DiffType.MEALY, mealyHandler
        );
    }

    public DiffHandler<?> getHandler(DiffType type) {
        return Optional.ofNullable(handlerMap.get(type))
            .orElseThrow(() -> new IllegalArgumentException("No handler for type: " + type));
    }
}