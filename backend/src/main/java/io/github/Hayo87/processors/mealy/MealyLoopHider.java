package io.github.Hayo87.processors.mealy;

import org.springframework.stereotype.Component;

import io.github.Hayo87.model.Mealy;
import io.github.Hayo87.processors.general.AbstractLoopHider;

@Component
public class MealyLoopHider extends AbstractLoopHider<Mealy> {}