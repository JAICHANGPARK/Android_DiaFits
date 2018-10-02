package com.dreamwalker.diabetesfits.database.model;

import java.util.Date;

import io.realm.RealmObject;

public class Fitness extends RealmObject {

    /**
     * userInputMap.put("selectType", selectType);
     * userInputMap.put("selectTypeDetail", selectTypeDetail);
     * userInputMap.put("selectRpeExpression", selectRpeExpression);
     * userInputMap.put("fitnessTime", fitnessTime);
     * userInputMap.put("fitnessDistance",fitnessDistance);
     * userInputMap.put("fitnessSpeed", fitnessSpeed);
     * userInputMap.put("rpeScore", rpeScore);
     * userInputMap.put("hourOfDay", String.valueOf(hourOfDay));
     * userInputMap.put("minute", String.valueOf(minute));
     * userInputMap.put("timestamp", String.valueOf(gregorianCalendar.getTimeInMillis()));
     */

    private String userValue;
    private String type; // 선택한 운동 종류
    private String selectTypeDetail; // 운동 상세
    private String selectRpeExpression; // rpe 정보
    private String fitnessTime; //운동시간
    private String distance; // 운동 거리
    private String speed; // 운동 속도
    private String heartRate; // 심박수
    private String rpeScore; // 운동자각인지도 rpe 점수
    private String kcal; // 소모 열량

    private String date;
    private String time;
    private String timestamp;
    private long longTs;
    private Date datetime;

    public Fitness() {
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

    public String getSelectTypeDetail() {
        return selectTypeDetail;
    }

    public void setSelectTypeDetail(String selectTypeDetail) {
        this.selectTypeDetail = selectTypeDetail;
    }

    public String getSelectRpeExpression() {
        return selectRpeExpression;
    }

    public void setSelectRpeExpression(String selectRpeExpression) {
        this.selectRpeExpression = selectRpeExpression;
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

    public String getRpeScore() {
        return rpeScore;
    }

    public void setRpeScore(String rpeScore) {
        this.rpeScore = rpeScore;
    }

    public String getKcal() {
        return kcal;
    }

    public void setKcal(String kcal) {
        this.kcal = kcal;
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
