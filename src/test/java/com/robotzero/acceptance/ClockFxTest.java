package com.robotzero.acceptance;

import com.robotzero.acceptance.di.TestSpringApplicationConfiguration;
import com.robotzero.counter.ClockApp;
import com.robotzero.di.TimerConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest(classes = {TimerConfiguration.class, TestSpringApplicationConfiguration.class}, webEnvironment = WebEnvironment.NONE)
public class ClockFxTest {
  @Autowired
  ConfigurableApplicationContext applicationContext;

  @Test
  public void contextLoads() {
    System.out.println(applicationContext);
  }
}
