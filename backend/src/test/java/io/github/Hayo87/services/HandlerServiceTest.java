package io.github.Hayo87.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import io.github.Hayo87.domain.handlers.DiffHandler;
import io.github.Hayo87.domain.handlers.MealyDiffHandler;
import io.github.Hayo87.domain.handlers.StringDiffHandler;
import io.github.Hayo87.domain.rules.AutomataType;

public class HandlerServiceTest {

    private HandlerService handlerService;
    private StringDiffHandler stringHandler;
    private MealyDiffHandler mealyHandler;

    @BeforeEach
    void setup(){
        stringHandler = mock(StringDiffHandler.class);
        mealyHandler = mock(MealyDiffHandler.class);

        handlerService = new HandlerService(stringHandler, mealyHandler);
    }

    @Test
    void getHandler_withValidInput_returnsCorrect(){
        DiffHandler<?> handlerm = handlerService.getHandler(AutomataType.MEALY);
        assertSame(mealyHandler, handlerm);

        DiffHandler<?> handlers = handlerService.getHandler(AutomataType.STRING);
        assertSame(stringHandler, handlers);
    }

    @Test
    void getHandler_withInvalidInput_throwsException(){
        assertThrows(IllegalArgumentException.class, () ->  {
            handlerService.getHandler(AutomataType.valueOf("invalid"));
        });
    }
}




