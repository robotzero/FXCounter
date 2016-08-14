package com.queen.counter.repository;

import com.queen.counter.domain.SavedTimer;

import java.time.LocalTime;

public interface SavedTimerRepository {

    void create(String name, LocalTime savedTimer);

    SavedTimer selectLatest();

    void deleteAll();
}
