package com.dreamwalker.diabetesfits.device.isens;

/**
 * Created by isens on 2015. 10. 13..
 */
public class GlucoseRecord { //0: 초기값
    public int sequenceNumber = 0;

    public long time = 0; // glucose time

    public float glucoseData = 0; // glucose value

    public int flag_cs = 0; // 1: control solution

    public int flag_hilow = 0; //-1: low, 1: high

    public int flag_context = 0; //1: ble context data (complete data)

    public int flag_meal = 0; //-1: before meal, 1: after meal

    public int flag_fasting = 0; //1: fasting

    public int flag_ketone = 0; //1: ketone

    public int flag_nomark = 0; //no-mark

    public int timeoffset = 0; //time offset
}
