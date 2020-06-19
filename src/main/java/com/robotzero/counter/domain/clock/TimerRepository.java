package com.robotzero.counter.domain.clock;

import com.robotzero.counter.entity.Clock;
import java.time.LocalTime;

public interface TimerRepository {
  void create(String name, LocalTime savedTimer);

  Clock selectLatest();

  void deleteAll();
}
