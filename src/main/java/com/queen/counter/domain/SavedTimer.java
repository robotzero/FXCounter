package com.queen.counter.domain;

import java.time.Instant;
import java.time.LocalTime;

public class SavedTimer {

    private String name;
    private LocalTime savedTimer;
    private Instant created;

    public LocalTime getSavedTimer() {
        return savedTimer;
    }

    public void setSavedTimer(LocalTime savedTimer) {
        this.savedTimer = savedTimer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}
