package com.dreamwalker.diabetesfits.model.isens;

/**
 * Created by KNU2017 on 2018-02-11.
 */

public class BloodSugar {

    String bsType;
    String bsValue;
    String bsDate;
    String bsTime;
    int typeValue;

    /**
     *
     * @Author JAICHANGPARK
     * 혈당계 데이터 동기화 모델
     * @param bsValue
     * @param bsTime
     * @param typeValue
     *
     */

    public BloodSugar(String bsValue, String bsTime, int typeValue) {
        this.bsValue = bsValue;
        this.bsTime = bsTime;
        this.typeValue = typeValue;
    }

    /**
     *  @author : JAICHANGPARK
     * 홈에서 사용
     * @param bsType
     * @param bsValue
     * @param bsTime
     */
    public BloodSugar(String bsType, String bsValue, String bsTime) {
        this.bsType = bsType;
        this.bsValue = bsValue;
        this.bsTime = bsTime;
    }

    /**
     * @author : JAICHANGPARK
     * 다이어리 에서 사용됨
     * @param bsType
     * @param bsValue
     * @param bsDate
     * @param bsTime
     */

    public BloodSugar(String bsType, String bsValue, String bsDate, String bsTime) {
        this.bsType = bsType;
        this.bsValue = bsValue;
        this.bsDate = bsDate;
        this.bsTime = bsTime;
    }

    public String getBsType() {
        return bsType;
    }

    public void setBsType(String bsType) {
        this.bsType = bsType;
    }

    public String getBsValue() {
        return bsValue;
    }

    public void setBsValue(String bsValue) {
        this.bsValue = bsValue;
    }

    public String getBsTime() {
        return bsTime;
    }

    public void setBsTime(String bsTime) {
        this.bsTime = bsTime;
    }

    public int getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }

    public String getBsDate() {
        return bsDate;
    }

    public void setBsDate(String bsDate) {
        this.bsDate = bsDate;
    }
}
