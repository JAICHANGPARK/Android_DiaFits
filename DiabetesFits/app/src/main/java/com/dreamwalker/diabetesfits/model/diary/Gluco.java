package com.dreamwalker.diabetesfits.model.diary;

import java.util.Date;

public class Gluco {

    private String userValue;
    private String type;
    private String date;
    private String time;
    private String timestamp;
    private long longTs;
    private Date datetime;
    private int changeValue;


    public Gluco(String userValue, String type, String date, String time, String timestamp, long longTs, Date datetime, int changeValue) {
        this.userValue = userValue;
        this.type = type;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.longTs = longTs;
        this.datetime = datetime;
        this.changeValue = changeValue;
    }

    public Gluco(String userValue, String type, String date, String time, String timestamp, long longTs, Date datetime) {
        this.userValue = userValue;
        this.type = type;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.longTs = longTs;
        this.datetime = datetime;
    }

    public int getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(int changeValue) {
        this.changeValue = changeValue;
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getLongTs() {
        return longTs;
    }

    public void setLongTs(long longTs) {
        this.longTs = longTs;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}
