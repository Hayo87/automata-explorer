package io.github.Hayo87.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.Hayo87.domain.handlers.DiffHandler;
import io.github.Hayo87.domain.handlers.MealyDiffHandler;
import io.github.Hayo87.domain.handlers.StringDiffHandler;
import io.github.Hayo87.domain.rules.AutomataType;

/**
 * Service to get the appropriate {@link DiffHandler} implementation based on the 
 * given automaton type.
 */
@Service
public class HandlerService {

    private final Map<AutomataType, DiffHandler<?>> handlerMap;

    public HandlerService(StringDiffHandler stringHandler, MealyDiffHandler mealyHandler) {
        this.handlerMap = Map.of(
            AutomataType.STRING, stringHandler,
            AutomataType.MEALY, mealyHandler
        );
    }
    
    /**
     * Retreives the diff handler for the specified automaton type.
     * @param type the automaton type
     * @return the corresponing diff handler
     */
    public DiffHandler<?> getHandler(AutomataType type) {
        return Optional.ofNullable(handlerMap.get(type))
            .orElseThrow(() -> new IllegalArgumentException("No handler for type: " + type));
    }
}