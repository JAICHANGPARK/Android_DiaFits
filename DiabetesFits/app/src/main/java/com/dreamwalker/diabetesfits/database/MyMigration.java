package com.dreamwalker.diabetesfits.database;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * 피트니스 모델 추가 한뒤 데이터베이스 Migration 클래스.
 * 제작 : 박제창
 */
public class MyMigration implements RealmMigration {

    private static final String TAG = "MyMigration";

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e(TAG, "MyMigration migrate: -->  " + oldVersion);

        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            /**
             *    private String userValue;
             *    private String type;
             *
             *    private String fitnessTime; //운동시간
             *    private String distance; // 운동 거리
             *    private String speed; // 운동 속도
             *    private String heartRate; // 심박수
             *
             *    private String date;
             *    private String time;
             *    private String timestamp;
             *    private long longTs;
             *    private Date datetime;
             *
             */

            schema.create("Fitness")
                    .addField("userValue", String.class)
                    .addField("type", String.class)
                    .addField("fitnessTime", String.class)
                    .addField("distance", String.class)
                    .addField("speed", String.class)
                    .addField("heartRate", String.class)
                    .addField("date", String.class)
                    .addField("time", String.class)
                    .addField("timestamp", String.class)
                    .addField("longTs", long.class)
                    .addField("datetime", Date.class);

            oldVersion++;
        }

        if (oldVersion == 1) {
            /**
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
             *
             */
            schema.get("Fitness")
                    .addField("selectTypeDetail", String.class)
                    .addField("selectRpeExpression", String.class)
                    .addField("rpeScore", String.class)
                    .addField("kcal", String.class);


//            schema.create("Fitness")
//                    .addField("userValue", String.class)
//                    .addField("type", String.class)
//                    .addField("fitnessTime", String.class)
//                    .addField("distance", String.class)
//                    .addField("speed", String.class)
//                    .addField("heartRate", String.class)
//                    .addField("date", String.class)
//                    .addField("time", String.class)
//                    .addField("timestamp", String.class)
//                    .addField("longTs", long.class)
//                    .addField("datetime", Date.class);

            oldVersion++;
        }


    }

    public int hashCode() {
        return MyMigration.class.hashCode();
    }

    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }
        return object instanceof MyMigration;
    }


}
