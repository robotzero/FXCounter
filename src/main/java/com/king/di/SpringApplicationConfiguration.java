package com.king.di;

import com.king.animator.Animator;
import com.king.animator.PointsRotator;
import com.king.configuration.SceneConfiguration;
import com.king.counter.clock.ClockPresenter;
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
        return new Animator(new PointsRotator());
    }

//    @Bean
//    public MyService getMyService() {
//        return new DefaultMyService(getMyAdapter());
//    }

//    @Bean
//    public MyOtherService getMyOtherService() {
//        return new DefaultMyOtherService(getMyAdapter());
//    }
//
//    @Bean
//    public MyAdapter getMyAdapter() {
//        return new DefaultMyAdapter();
//    }
}