
package com.androidproject.sudiet.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;

public class PressureReading extends RealmObject {
    @PrimaryKey
    private long id;

    private double minReading;
    private double maxReading;
    private Date created;

    public PressureReading() {
    }

    public PressureReading(double minReading, double maxReading, Date created) {
        // mm/Hg
        this.minReading = minReading;
        this.maxReading = maxReading;
        this.created = created;
    }

    public double getMinReading() {
        return minReading;
    }

    public void setMinReading(double minReading) {
        this.minReading = minReading;
    }

    public double getMaxReading() {
        return maxReading;
    }

    public void setMaxReading(double maxReading) {
        this.maxReading = maxReading;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
