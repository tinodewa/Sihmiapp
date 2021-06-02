package com.roma.android.sihmi.model.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LoadDataState {
    @PrimaryKey
    public int id;
    public boolean isLoaded;
    public long timeLoaded;

    public LoadDataState(boolean isLoaded, long timeLoaded) {
        this.id = 1;
        this.isLoaded = isLoaded;
        this.timeLoaded = timeLoaded;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public long getTimeLoaded() {
        return timeLoaded;
    }

    public void setTimeLoaded(long timeLoaded) {
        this.timeLoaded = timeLoaded;
    }
}
