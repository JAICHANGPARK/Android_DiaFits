package com.dreamwalker.diabetesfits.consts.isens;

import java.util.UUID;

/**
 * Created by isens on 2015. 10. 13..
 */
public class PremierNConst {

    public final static int BLE_SCAN_DURATION = 5000;

    public final static String PREMIER_N_BLE = "CareSens 0701";

    public final static String INTENT_BLE_EXTRA_DATA 			= "com.isens.standard.ble.BLE_EXTRA_DATA";
    public final static String INTENT_BLE_CONNECTED_DEVICE		= "com.isens.standard.ble.BLE_CONNECTED_DEVICE";
    public final static String INTENT_BLE_BONDED = "air.SmartLog.android.ble.BLE_BONDED";
    public final static String INTENT_BLE_BOND_NONE = "air.SmartLog.android.ble.BLE_BOND_NONE";
    public final static String INTENT_BLE_DEVICECONNECTED = "air.SmartLog.android.ble.BLE_DEVICECONNECTED";
    public final static String INTENT_BLE_DEVICEDISCONNECTED = "air.SmartLog.android.ble.BLE_DEVICEDISCONNECTED";
    public final static String INTENT_BLE_SERVICEDISCOVERED = "air.SmartLog.android.ble.BLE_SERVICEDISCOVERED";
    public final static String INTENT_BLE_ERROR = "air.SmartLog.android.ble.BLE_ERROR";
    public final static String INTENT_BLE_DEVICENOTSUPPORTED = "air.SmartLog.android.ble.BLE_DEVICENOTSUPPORTED";
    public final static String INTENT_BLE_OPERATESTARTED = "air.SmartLog.android.ble.BLE_OPERATESTARTED";
    public final static String INTENT_BLE_OPERATECOMPLETED = "air.SmartLog.android.ble.BLE_OPERATECOMPLETED";
    public final static String INTENT_BLE_OPERATEFAILED = "air.SmartLog.android.ble.BLE_OPERATEFAILED";
    public final static String INTENT_BLE_OPERATENOTSUPPORTED = "air.SmartLog.android.ble.BLE_OPERATENOTSUPPORTED";
    public final static String INTENT_BLE_DATASETCHANGED = "air.SmartLog.android.ble.BLE_DATASETCHANGED";
    public final static String INTENT_BLE_READ_SERIALNUMBER = "air.SmartLog.android.ble.BLE_READ_SERIALNUMBER";
    public final static String INTENT_BLE_READ_MANUFACTURER = "air.SmartLog.android.ble.BLE_READ_MANUFACTURER";
    public final static String INTENT_BLE_READ_SOFTWARE_REV = "air.SmartLog.android.ble.BLE_READ_SOFTWARE_REVISION";
    public final static String INTENT_BLE_RACPINDICATIONENABLED = "air.SmartLog.android.ble.BLE_RACPINDICATIONENABLED";
    public final static String INTENT_BLE_SEQUENCECOMPLETED = "air.SmartLog.android.ble.BLE_SEQUENCECOMPLETED";
    public final static String INTENT_BLE_REQUEST_COUNT = "air.SmartLog.android.ble.BLE_REQUESTCOUNT";
    public final static String INTENT_BLE_READCOMPLETED = "air.SmartLog.android.ble.BLE_READCOMPLETED";

    //Service
    public final static UUID BLE_SERVICE_GLUCOSE		= UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_SERVICE_DEVICE_INFO	= UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_SERVICE_CUSTOM     	= UUID.fromString("0000FFF0-0000-1000-8000-00805f9b34fb");
    //Characteristic
    public final static UUID BLE_CHAR_GLUCOSE_SERIALNUM	= UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_SOFTWARE_REVISION	= UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_MEASUREMENT= UUID.fromString("00002A18-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_CONTEXT	= UUID.fromString("00002A34-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_GLUCOSE_RACP		= UUID.fromString("00002A52-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_CUSTOM_TIME	    = UUID.fromString("0000FFF1-0000-1000-8000-00805f9b34fb");
    //Descriptor
    public final static UUID BLE_DESCRIPTOR_DESCRIPTOR	= UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}
