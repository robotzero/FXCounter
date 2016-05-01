package com.queen.di;

import com.airhacks.afterburner.views.FXMLView;
import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.clock.ClockPresenter;
import com.queen.counter.clock.ClockView;
import com.queen.counter.domain.Clocks;
import com.queen.counter.domain.UIService;
import com.queen.counter.service.Populator;
import com.queen.counter.service.Scroller;
import com.queen.counter.service.Ticker;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.reactfx.EventSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class SpringApplicationConfiguration {

    private Animator animator = new Animator();
    private InMemoryCachedServiceLocator cache = new InMemoryCachedServiceLocator();
    private EventSource eventSource = new EventSource();
    private FXMLView clockView = new ClockView();
    private UIService uiService = new UIService();

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
//        Pane secondsPane = (Pane) clockView.getView().getChildrenUnmodifiable().stream().filter(p -> p.getId().contains("seconds")).findFirst().get();
        //System.out.println(this.clockView.getViewWithoutRootContainer());
//        Pane minutesPane = (Pane) clockView.getView().getChildrenUnmodifiable().stream().filter(p -> p.getId().contains("minutes")).findFirst().get();
//        System.out.println(secondsPane);
        Populator populator = new Populator(uiService, clocks);

        return populator;
        //return new Populator(Stream.of((Group)secondsPane.getChildren().get(0), (Group)minutesPane.getChildren().get(0)));
    }

    @Bean
    public Scroller scroller() {
        return new Scroller(animator, cache, clocks, eventSource);
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
}