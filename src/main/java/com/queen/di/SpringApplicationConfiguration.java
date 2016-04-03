package com.queen.di;

import com.queen.animator.Animator;
import com.queen.configuration.SceneConfiguration;
import com.queen.counter.cache.InMemoryCachedServiceLocator;
import com.queen.counter.clock.ClockPresenter;
import com.queen.counter.service.Populator;
import com.queen.counter.service.Scroller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringApplicationConfiguration {

    private Animator animator = new Animator();

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
        return new Populator();
    }

    @Bean
    public Scroller scroller() {
        return new Scroller(animator);
    }

    @Bean
    public InMemoryCachedServiceLocator serviceLocator() {
        return new InMemoryCachedServiceLocator();
    }
}