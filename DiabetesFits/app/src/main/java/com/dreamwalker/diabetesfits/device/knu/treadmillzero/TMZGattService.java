package com.dreamwalker.diabetesfits.device.knu.treadmillzero;

import java.util.UUID;

public class TMZGattService {

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"; //char
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    //Service
    public final static UUID BLE_HEART_RATE                     = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_FITNESS_MACHINE                = UUID.fromString("00001826-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_DATE_TIME                      = UUID.fromString("0000ddd0-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_DEVICE_AUTH                    = UUID.fromString("0000eee0-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_DATA_SYNC                      = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    //Characteristic
    public final static UUID BLE_CHAR_HEART_RATE_MEASUREMENT    = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_INDOOR_BIKE_DATA          = UUID.fromString("00002AD2-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_TREADMILL_DATA            = UUID.fromString("00002ACD-0000-1000-8000-00805f9b34fb");


    public final static UUID BLE_CHAR_DATE_TIME_SYNC            = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_DATE_TIME                 = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_RESULT                    = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    public final static UUID BLE_CHAR_DEVICE_AUTH               = UUID.fromString("0000eee1-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_DATA_CONTEXT              = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_DATA_SYNC                 = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    //Descriptor
    public final static UUID BLE_DESCRIPTOR_DESCRIPTOR	        = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}
