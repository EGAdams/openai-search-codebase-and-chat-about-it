package com.nac.model;

import java.io.Serializable;

/**
 * Created by andreikaralkou on 2/4/14.
 */
public class Run implements Serializable {
    private String condition = "";
    private long date;
    private float value;
    private long testId;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
