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


    public ArrayList<Met> getTreadmillMetData(){
        ArrayList<Met> metArrayList = new ArrayList<>();
        metArrayList.add(new Met("3.5", 2.67f));
        metArrayList.add(new Met("3.6", 2.67f));
        metArrayList.add(new Met("3.7", 2.67f));
        metArrayList.add(new Met("3.8", 2.67f));
        metArrayList.add(new Met("3.9", 2.67f));
        metArrayList.add(new Met("4.0", 2.67f));
        metArrayList.add(new Met("4.1", 2.67f));
        metArrayList.add(new Met("4.2", 2.67f));
        metArrayList.add(new Met("4.3", 2.67f));
        metArrayList.add(new Met("4.4", 2.67f));
        metArrayList.add(new Met("4.5", 2.67f));
        metArrayList.add(new Met("4.6", 2.67f));
        metArrayList.add(new Met("4.7", 2.67f));
        metArrayList.add(new Met("4.8", 2.67f));
        metArrayList.add(new Met("4.9", 2.67f));
        metArrayList.add(new Met("5.0", 2.67f));
        metArrayList.add(new Met("5.1", 2.67f));
        metArrayList.add(new Met("5.2", 2.67f));
        metArrayList.add(new Met("5.3", 2.67f));
        metArrayList.add(new Met("5.4", 2.67f));
        metArrayList.add(new Met("5.5", 2.67f));
        metArrayList.add(new Met("5.6", 2.67f));
        metArrayList.add(new Met("5.7", 2.67f));
        metArrayList.add(new Met("5.8", 2.67f));
        metArrayList.add(new Met("5.9", 2.67f));



        return metArrayList;
    }
}
