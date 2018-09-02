package com.dreamwalker.diabetesfits.database.model;

import java.util.Date;

import io.realm.RealmObject;

public class Fitness extends RealmObject {

    private String userValue;
    private String type;
    private String date;
    private String time;
    private String timestamp;
    private long longTs;
    private Date datetime;

}
