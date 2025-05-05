package io.github.Hayo87.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.Hayo87.handlers.DiffHandler;
import io.github.Hayo87.handlers.MealyDiffHandler;
import io.github.Hayo87.handlers.StringDiffHandler;
import io.github.Hayo87.model.AutomataType;

@Service
public class HandlerService {

    private final Map<AutomataType, DiffHandler<?>> handlerMap;

    public HandlerService(StringDiffHandler stringHandler, MealyDiffHandler mealyHandler) {
        this.handlerMap = Map.of(
            AutomataType.STRING, stringHandler,
            AutomataType.MEALY, mealyHandler
        );
    }

    public DiffHandler<?> getHandler(AutomataType type) {
        return Optional.ofNullable(handlerMap.get(type))
            .orElseThrow(() -> new IllegalArgumentException("No handler for type: " + type));
    }
}