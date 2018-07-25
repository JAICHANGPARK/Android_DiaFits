package com.dreamwalker.diabetesfits.database.model;

import java.util.Date;

import io.realm.RealmObject;

public class Glucose extends RealmObject {

    private String userValue;
    private String type;
    private String date;
    private String time;
    private String timestamp;
    private long longTs;
    private Date datetime;


    public Glucose() {
    }

    public Glucose(String userValue, String type, String date, String time, String timestamp, long longTs, Date datetime) {
        this.userValue = userValue;
        this.type = type;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.longTs = longTs;
        this.datetime = datetime;
    }

    public Glucose(String userValue, String type, String date, String time, String timestamp, Date datetime) {
        this.userValue = userValue;
        this.type = type;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.datetime = datetime;
    }


    public Glucose(String value, String type, String date, String time, String timestamp) {
        this.userValue = value;
        this.type = type;
        this.date = date;
        this.time = time;
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

    public String getValue() {
        return userValue;
    }

    public void setValue(String value) {
        this.userValue = value;
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
}
