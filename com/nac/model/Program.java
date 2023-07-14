package com.nac.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by andreikaralkou on 1/15/14.
 */
public class Program implements Serializable{
    private String airportName = "";
    private String location = "";
    private String comment = "";

    private List<Test> testList;

    public Program(String airportName, String location, String comment) {
        this.airportName = airportName;
        this.location = location;
        this.comment = comment;
    }

    public Program(String airportName, String location, String comment, List<Test> testList) {
        this(airportName, location, comment);
        this.testList = testList;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Test> getTestList() {
        return testList;
    }

    public void setTestList(List<Test> testList) {
        this.testList = testList;
    }
}
