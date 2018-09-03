package com.dreamwalker.diabetesfits.database.model;

import java.util.Date;

import io.realm.RealmObject;

public class Fitness extends RealmObject {


    private String userValue;
    private String type;

    private String fitnessTime; //운동시간
    private String distance; // 운동 거리
    private String speed; // 운동 속도
    private String heartRate; // 심박수 

    private String date;
    private String time;
    private String timestamp;
    private long longTs;
    private Date datetime;



}
