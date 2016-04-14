package com.queen.counter.service;

import com.queen.animator.Animator;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

public class Ticker {

    private final Animator animator;
    private final InMemoryCachedServiceLocator cache;

    public Ticker(final Animator animator, final InMemoryCachedServiceLocator cache) {
        this.animator = animator;
        this.cache = cache;
    }

    public void tick(List<Rectangle> rectangles, List<Text> labels) {

    }

    private LocalTime clockTick(LocalTime current, Function<LocalTime, LocalTime> tick) {
        return tick.apply(current);
    }
}
