package com.robotzero.counter;

import com.robotzero.di.TimerConfiguration;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(TimerConfiguration.class)
public class ClockApp {

  public static void main(String[] args) {
    // This is how normal Spring Boot app would be launched
    //SpringApplication.run(JavafxWeaverExampleApplication.class, args);

    Application.launch(ClockFx.class, args);
  }
}
