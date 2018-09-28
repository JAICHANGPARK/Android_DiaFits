package com.dreamwalker.diabetesfits.device.knu.treadmillzero;

public class TMZConst {
    public static final String DEVICE_NAME = "KNU TM0";
    public static final byte SYNC_ALL_DATA = 0x00;
    public static final byte SYNC_USER_ID_DATA = 0x01;
    public static String AUTH_MESSAGE = "9876543210100001";

    public final static String ACTION_GATT_CONNECTED = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_DATA_AVAILABLE";

    public final static String ACTION_FIRST_DONE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_FIRST_DONE";
    public final static String ACTION_SECOND_DONE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_SECOND_DONE";
    public final static String ACTION_FINAL_DONE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_FINAL_DONE";
    public final static String ACTION_FIRST_FAILED = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_FIRST_FAILED";
    public final static String ACTION_SECOND_FAILED = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_SECOND_FAILED";
    public final static String ACTION_FINAL_FAILED = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_FINAL_FAILED";

    public final static String ACTION_HEART_RATE_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_HEART_RATE_AVAILABLE";
    public final static String ACTION_INDOOR_BIKE_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_INDOOR_BIKE_AVAILABLE";
    public final static String ACTION_TREADMILL_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_TREADMILL_AVAILABLE";

    // TODO: 2018-09-04 동기화를 위한 필터
    public final static String ACTION_SERVICE_SCAN_DONE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_SERVICE_SCAN_DONE";
    public final static String ACTION_FIRST_PHASE_DONE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_FIRST_PHASE_DONE";
    public final static String ACTION_SECOND_PHASE_DONE = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.ACTION_SECOND_PHASE_DONE";
    public final static String INTENT_BLE_OPERATESTARTED = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.INTENT_BLE_OPERATESTARTED";

    public final static String EXTRA_DATA = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.EXTRA_DATA";
    public final static String EXTRA_FINAL_DATA = "com.dreamwalker.diabetesfits.service.knu.treadmillzero.sync.EXTRA_FINAL_DATA";

}
