package com.queen.di;

import com.airhacks.afterburner.views.FXMLView;
import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.clock.ClockPresenter;
import com.queen.counter.clock.ClockView;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import com.queen.counter.service.OffsetCalculator;
import com.queen.counter.service.Populator;
import com.queen.counter.service.Scroller;
import com.queen.counter.service.Ticker;
import org.reactfx.EventSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringApplicationConfiguration {

    private InMemoryCachedServiceLocator cache = new InMemoryCachedServiceLocator();
    private Animator animator = new Animator(cache);
    private EventSource eventSource = new EventSource();
    private FXMLView clockView = new ClockView();
    private UIService uiService = new UIService(cache);
    private OffsetCalculator offsetCalculator = new OffsetCalculator(uiService, animator);

    private Clocks clocks = new Clocks(eventSource);

    @Bean
    public FXMLView clockView() {
        return clockView;
    }

    @Bean
    public ClockPresenter clockPresenter() {
        return new ClockPresenter();
    }

    @Bean
    public SceneConfiguration sceneConfiguration() {
        return new SceneConfiguration();
    }

    @Bean
    public Animator animator() {
        return animator;
    }

    @Bean
    public Populator populator() {

        Populator populator = new Populator(uiService, clocks);

        return populator;
    }

    @Bean
    public Scroller scroller() {
        return new Scroller(animator, clocks, uiService, offsetCalculator);
    }

    @Bean
    public Ticker ticker() {
        return new Ticker(animator, cache);
    }

    @Bean
    public InMemoryCachedServiceLocator serviceLocator() {
        return cache;
    }

    @Bean
    public Clocks clocks() {
        return clocks;
    }

    @Bean
    public UIService uiService() {
        return uiService;
    }

    @Bean
    public OffsetCalculator offsetCalculator() {
        return offsetCalculator;
    }
}