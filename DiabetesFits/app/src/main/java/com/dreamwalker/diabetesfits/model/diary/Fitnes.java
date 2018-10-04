package com.dreamwalker.diabetesfits.model.diary;

import java.util.Date;

public class Fitnes {

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


    public Fitnes(String type, String selectTypeDetail, String selectRpeExpression,
                  String fitnessTime, String distance, String speed, String rpeScore, String kcal,
                  String date, String time, String timestamp, long longTs, Date datetime) {
        this.type = type;
        this.selectTypeDetail = selectTypeDetail;
        this.selectRpeExpression = selectRpeExpression;
        this.fitnessTime = fitnessTime;
        this.distance = distance;
        this.speed = speed;
        this.rpeScore = rpeScore;
        this.kcal = kcal;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.longTs = longTs;
        this.datetime = datetime;
    }

    /**
     *         fitness.setType(userInputMap.get("selectType"));
     *                 fitness.setSelectTypeDetail(userInputMap.get("selectTypeDetail"));
     *                 fitness.setSelectRpeExpression(userInputMap.get("selectRpeExpression"));
     *                 fitness.setFitnessTime(userInputMap.get("fitnessTime"));
     *                 fitness.setDistance(userInputMap.get("fitnessDistance"));
     *                 fitness.setSpeed(userInputMap.get("fitnessSpeed"));
     *                 fitness.setRpeScore(userInputMap.get("rpeScore"));
     *                 fitness.setKcal(userKcal);
     *                 fitness.setDate(dateTime[0]);
     *                 fitness.setTime(dateTime[1]);
     *                 fitness.setTimestamp(userInputMap.get("timestamp"));
     *                 fitness.setLongTs(userTs);
     *                 fitness.setDatetime(userDateTimes);
     */



    public Fitnes(String userValue, String type, String selectTypeDetail, String selectRpeExpression, String fitnessTime, String distance, String speed,
                  String heartRate, String rpeScore, String kcal, String date, String time, String timestamp, long longTs, Date datetime) {
        this.userValue = userValue;
        this.type = type;
        this.selectTypeDetail = selectTypeDetail;
        this.selectRpeExpression = selectRpeExpression;
        this.fitnessTime = fitnessTime;
        this.distance = distance;
        this.speed = speed;
        this.heartRate = heartRate;
        this.rpeScore = rpeScore;
        this.kcal = kcal;
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
