package com.dreamwalker.diabetesfits.model.diary;

import java.util.Date;

public class Global {

    private int tag;
    private String type;
    private String userValue;
    private String date;
    private String time;
    private String timestamp;
    private long longTs;
    private Date datetime;

    public Global(int tag, String type, String userValue, String date, String time, String timestamp, long longTs, Date datetime) {
        this.tag = tag;
        this.type = type;
        this.userValue = userValue;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.longTs = longTs;
        this.datetime = datetime;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
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
