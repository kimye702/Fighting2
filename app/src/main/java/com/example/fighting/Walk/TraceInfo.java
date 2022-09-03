package com.example.fighting.Walk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TraceInfo implements Serializable {
    private int walkTime;
    private double distance;
    private double pace;
    private String contents;
    private ArrayList<String> pictures;
    private Date startRun;
    private Date createdAt;

    public TraceInfo(String contents, int walkTime, double distance, double pace, ArrayList<String> pictures, Date startRun, Date createdAt){
        this.contents = contents;
        this.walkTime = walkTime;
        this.distance = distance;
        this.pace = pace;
        this.pictures = pictures;
        this.startRun = startRun;
        this.createdAt = createdAt;
    }


    public int getWalkTime(){return this.walkTime;}
    public void setWalkTime(int walkTime){this.walkTime =walkTime;}
    public double getDistance(){return this.distance;}
    public void setDistance(double distance){this.distance =distance;}
    public double getPace(){return this.pace;}
    public void setPace(double pace){this.pace = pace;}
    public String getContents(){return this.contents;}
    public void setContents(String contents){this.contents = contents;}
    public ArrayList<String> getPictures(){return this.pictures;}
    public void setPictures(ArrayList<String> pictures){this.pictures = pictures;}
    public Date getStartRun(){return this.startRun;}
    public void setStartRun(Date startRun){this.startRun = startRun;}
    public Date getCreatedAt(){return this.createdAt;}
    public void setCreatedAt(Date createdAt){this.createdAt = createdAt;}
}
