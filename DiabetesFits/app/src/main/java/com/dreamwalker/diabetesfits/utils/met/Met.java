package com.dreamwalker.diabetesfits.utils.met;

import java.util.ArrayList;

public class Met {

    String strength;
    float metValue;

    public Met() {
    }

    public Met(String strength, float metValue) {
        this.strength = strength;
        this.metValue = metValue;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public float getMetValue() {
        return metValue;
    }

    public void setMetValue(float metValue) {
        this.metValue = metValue;
    }


    public ArrayList<Met> getIndoorBikeMetData() {
        ArrayList<Met> metArrayList = new ArrayList<>();
        metArrayList.add(new Met("가볍게", 5.50f));
        metArrayList.add(new Met("보통으로", 7.00f));
        metArrayList.add(new Met("빠르게", 10.5f));
        return metArrayList;
    }
    public ArrayList<Met> getTreadmillWorkingMetData(){
        ArrayList<Met> metArrayList = new ArrayList<>();
        metArrayList.add(new Met("3.5", 2.67f));
        metArrayList.add(new Met("3.6", 2.71f));
        metArrayList.add(new Met("3.7", 2.76f));
        metArrayList.add(new Met("3.8", 2.81f));
        metArrayList.add(new Met("3.9", 2.86f));
        metArrayList.add(new Met("4.0", 2.91f));
        metArrayList.add(new Met("4.1", 2.95f));
        metArrayList.add(new Met("4.2", 3.00f));
        metArrayList.add(new Met("4.3", 3.05f));
        metArrayList.add(new Met("4.4", 3.10f));
        metArrayList.add(new Met("4.5", 3.15f));
        metArrayList.add(new Met("4.6", 3.19f));
        metArrayList.add(new Met("4.7", 3.24f));
        metArrayList.add(new Met("4.8", 3.29f));
        metArrayList.add(new Met("4.9", 3.34f));
        metArrayList.add(new Met("5.0", 3.39f));
        metArrayList.add(new Met("5.1", 3.43f));
        metArrayList.add(new Met("5.2", 3.48f));
        metArrayList.add(new Met("5.3", 3.53f));
        metArrayList.add(new Met("5.4", 3.58f));
        metArrayList.add(new Met("5.5", 3.63f));
        metArrayList.add(new Met("5.6", 3.67f));
        metArrayList.add(new Met("5.7", 3.72f));
        metArrayList.add(new Met("5.8", 3.77f));
        metArrayList.add(new Met("5.9", 3.82f));
        metArrayList.add(new Met("6.0", 3.87f));
        metArrayList.add(new Met("6.1", 3.91f));
        metArrayList.add(new Met("6.2", 3.96f));
        metArrayList.add(new Met("6.3", 4.01f));
        metArrayList.add(new Met("6.4", 4.06f));
        metArrayList.add(new Met("6.5", 4.11f));
        metArrayList.add(new Met("6.6", 4.15f));
        metArrayList.add(new Met("6.7", 4.20f));
        metArrayList.add(new Met("6.8", 4.25f));
        metArrayList.add(new Met("6.9", 4.29f));
        metArrayList.add(new Met("7.0", 4.33f));
        return metArrayList;
    }

    public ArrayList<Met> getTreadmillRunningMetData(){
        ArrayList<Met> metArrayList = new ArrayList<>();
        metArrayList.add(new Met("5.0", 5.76f));
        metArrayList.add(new Met("5.1", 5.86f));
        metArrayList.add(new Met("5.2", 5.95f));
        metArrayList.add(new Met("5.3", 6.05f));
        metArrayList.add(new Met("5.4", 6.14f));
        metArrayList.add(new Met("5.5", 6.24f));
        metArrayList.add(new Met("5.6", 6.33f));
        metArrayList.add(new Met("5.7", 6.43f));
        metArrayList.add(new Met("5.8", 6.52f));
        metArrayList.add(new Met("5.9", 6.62f));
        metArrayList.add(new Met("6.0", 6.71f));
        metArrayList.add(new Met("6.1", 6.81f));
        metArrayList.add(new Met("6.2", 6.90f));
        metArrayList.add(new Met("6.3", 7.00f));
        metArrayList.add(new Met("6.4", 7.10f));
        metArrayList.add(new Met("6.5", 7.19f));
        metArrayList.add(new Met("6.6", 7.29f));
        metArrayList.add(new Met("6.7", 7.38f));
        metArrayList.add(new Met("6.8", 7.48f));
        metArrayList.add(new Met("6.9", 7.57f));

        metArrayList.add(new Met("7.0", 7.67f));
        metArrayList.add(new Met("7.1", 7.76f));
        metArrayList.add(new Met("7.2", 7.86f));
        metArrayList.add(new Met("7.3", 7.95f));
        metArrayList.add(new Met("7.4", 8.05f));
        metArrayList.add(new Met("7.5", 8.14f));
        metArrayList.add(new Met("7.6", 8.24f));
        metArrayList.add(new Met("7.7", 8.33f));
        metArrayList.add(new Met("7.8", 8.43f));
        metArrayList.add(new Met("7.9", 8.52f));

        metArrayList.add(new Met("8.0", 8.62f));
        metArrayList.add(new Met("8.1", 8.71f));
        metArrayList.add(new Met("8.2", 8.81f));
        metArrayList.add(new Met("8.3", 8.90f));
        metArrayList.add(new Met("8.4", 9.00f));
        metArrayList.add(new Met("8.5", 9.10f));
        metArrayList.add(new Met("8.6", 9.19f));
        metArrayList.add(new Met("8.7", 9.29f));
        metArrayList.add(new Met("8.8", 9.38f));
        metArrayList.add(new Met("8.9", 9.48f));

        metArrayList.add(new Met("9.0", 9.57f));
        metArrayList.add(new Met("9.1", 9.67f));
        metArrayList.add(new Met("9.2", 9.76f));
        metArrayList.add(new Met("9.3", 9.86f));
        metArrayList.add(new Met("9.4", 9.95f));
        metArrayList.add(new Met("9.5", 10.05f));
        metArrayList.add(new Met("9.6", 10.14f));
        metArrayList.add(new Met("9.7", 10.24f));
        metArrayList.add(new Met("9.8", 10.33f));
        metArrayList.add(new Met("9.9", 10.43f));

        metArrayList.add(new Met("10.0", 10.52f));
        metArrayList.add(new Met("10.1", 10.62f));
        metArrayList.add(new Met("10.2", 10.71f));
        metArrayList.add(new Met("10.3", 10.81f));
        metArrayList.add(new Met("10.4", 10.90f));
        metArrayList.add(new Met("10.5", 11.00f));
        metArrayList.add(new Met("10.6", 11.10f));
        metArrayList.add(new Met("10.7", 11.19f));
        metArrayList.add(new Met("10.8", 11.29f));
        metArrayList.add(new Met("10.9", 11.38f));

        metArrayList.add(new Met("11.0", 11.48f));
        metArrayList.add(new Met("11.1", 11.57f));
        metArrayList.add(new Met("11.2", 11.67f));
        metArrayList.add(new Met("11.3", 11.76f));
        metArrayList.add(new Met("11.4", 11.86f));
        metArrayList.add(new Met("11.5", 11.95f));
        metArrayList.add(new Met("11.6", 12.05f));
        metArrayList.add(new Met("11.7", 12.14f));
        metArrayList.add(new Met("11.8", 12.24f));
        metArrayList.add(new Met("11.9", 12.33f));

        metArrayList.add(new Met("12.0", 12.43f));
        metArrayList.add(new Met("12.1", 12.52f));
        metArrayList.add(new Met("12.2", 12.62f));
        metArrayList.add(new Met("12.3", 12.71f));
        metArrayList.add(new Met("12.4", 12.81f));
        metArrayList.add(new Met("12.5", 12.90f));
        metArrayList.add(new Met("12.6", 13.00f));
        metArrayList.add(new Met("12.7", 13.10f));
        metArrayList.add(new Met("12.8", 13.19f));
        metArrayList.add(new Met("12.9", 13.29f));

        metArrayList.add(new Met("13.0", 13.38f));
        metArrayList.add(new Met("13.1", 13.48f));
        metArrayList.add(new Met("13.2", 13.57f));
        metArrayList.add(new Met("13.3", 13.67f));
        metArrayList.add(new Met("13.4", 13.76f));
        metArrayList.add(new Met("13.5", 13.86f));
        metArrayList.add(new Met("13.6", 13.95f));
        metArrayList.add(new Met("13.7", 14.05f));
        metArrayList.add(new Met("13.8", 14.14f));
        metArrayList.add(new Met("13.9", 14.24f));

        return metArrayList;
    }
}
