package com.robotzero.counter.repository;

import com.robotzero.counter.domain.SavedTimer;

import java.time.LocalTime;

public interface SavedTimerRepository {

    void create(String name, LocalTime savedTimer);

    SavedTimer selectLatest();

    void deleteAll();
}
