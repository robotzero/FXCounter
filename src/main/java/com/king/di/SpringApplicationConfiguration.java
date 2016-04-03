package com.king.di;

import com.king.animator.Animator;
import com.king.configuration.SceneConfiguration;
import com.king.counter.cache.InMemoryCachedServiceLocator;
import com.king.counter.clock.ClockPresenter;
import com.king.counter.service.Populator;
import com.king.counter.service.Scroller;
import com.king.counter.cache.InMemoryCachedServiceLocator;
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