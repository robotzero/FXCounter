package com.king.di;

import com.king.animator.Animator;
import com.king.configuration.SceneConfiguration;
import com.king.counter.clock.ClockPresenter;
import com.king.counter.service.Populator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringApplicationConfiguration {

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
        return new Animator();
    }

    @Bean
    public Populator populator() {
        return new Populator();
    }
}