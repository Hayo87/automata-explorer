package io.github.Hayo87.domain.processors.mealy;

import org.springframework.stereotype.Component;

import io.github.Hayo87.domain.model.Mealy;
import io.github.Hayo87.domain.processors.general.AbstractLoopHider;

@Component
public class MealyLoopHider extends AbstractLoopHider<Mealy> {}