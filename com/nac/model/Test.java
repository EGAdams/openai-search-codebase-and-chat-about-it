package com.nac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreikaralkou on 1/15/14.
 */
public class Test implements Serializable{
    private String operator = "";
    private String offset = "";
    private long testDate;
    private int runCount;
    private long programId;
    private float average;
    private float average13;
    private float average23;
    private float average33;
    private float testSpeed;
    private String condition = "";
    private List<Run> runList = new ArrayList<Run>();

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public long getTestDate() {
        return testDate;
    }

    public void setDate(long testDate) {
        this.testDate = testDate;
    }

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public float getTestSpeed() {
        return testSpeed;
    }

    public void setTestSpeed(float testSpeed) {
        this.testSpeed = testSpeed;
    }

    public List<Run> getRunList() {
        return runList;
    }

    public void setRunList(List<Run> runList) {
        this.runList = runList;
    }

    public void addRun(Run run) {
        runList.add(run);
    }

    public float getAverage13() {
        return average13;
    }

    public void setAverage13(float average13) {
        this.average13 = average13;
    }

    public float getAverage23() {
        return average23;
    }

    public void setAverage23(float average23) {
        this.average23 = average23;
    }

    public float getAverage33() {
        return average33;
    }

    public void setAverage33(float average33) {
        this.average33 = average33;
    }

    public float calculateAverage() {
        if (runList.size() > 0) {
            float sum = 0;
            for (Run run : runList) {
                sum += run.getValue();
            }
            return sum / runList.size();
        }
        return 0f;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }
}
