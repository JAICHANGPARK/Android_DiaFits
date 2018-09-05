package com.dreamwalker.diabetesfits.model.fitness;

import java.util.Date;

public class Fitness {


    private String userValue;   // 여기에 뭘 넣야야 할까
    private String type;        // 여기에 뭘 넣어야 할까

    private String fitnessTime; //운동시간
    private String distance; // 운동 거리
    private String speed; // 운동 속도
    private String heartRate; // 심박수

    private String date;
    private String time;
    private String timestamp;
    private long longTs;
    private Date datetime;

    public Fitness() {
    }

    public Fitness(String userValue, String type, String fitnessTime, String distance, String speed,
                   String heartRate, String date, String time, String timestamp, long longTs, Date datetime) {
        this.userValue = userValue;
        this.type = type;
        this.fitnessTime = fitnessTime;
        this.distance = distance;
        this.speed = speed;
        this.heartRate = heartRate;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.longTs = longTs;
        this.datetime = datetime;
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

    public String getFitnessTime() {
        return fitnessTime;
    }

    public void setFitnessTime(String fitnessTime) {
        this.fitnessTime = fitnessTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
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
