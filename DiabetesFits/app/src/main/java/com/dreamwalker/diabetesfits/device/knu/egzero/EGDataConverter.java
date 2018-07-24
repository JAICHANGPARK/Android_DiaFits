package com.dreamwalker.diabetesfits.device.knu.egzero;

public class EGDataConverter {


    public EGDataConverter() {
    }

    // TODO: 2018-07-24 static으로 설정하면 객체 생성없이 사용 가능해요 - 박제창
    public static String parseBluetoothData(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        float floatVal;

        final int intVal = (data[3] << 8) & 0xff00 | (data[2] & 0xff);
        floatVal = (float) intVal / 100;
        return String.format("%.2f", floatVal);
    }

    public static String parseTreadmillData(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        float floatVal;


        final int intVal = (data[4] << 16) & 0xff0000 | (data[3] << 8) & 0x00ff00 | (data[2] & 0xff);
        return String.format("%d m", intVal);
    }
}
