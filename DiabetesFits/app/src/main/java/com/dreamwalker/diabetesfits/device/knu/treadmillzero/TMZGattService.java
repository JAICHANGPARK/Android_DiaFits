package com.dreamwalker.diabetesfits.device.knu.treadmillzero;

import java.util.UUID;

public class TMZGattService {

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"; //char
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    //Service
    public final static UUID BLE_HEART_RATE             = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_FITNESS_MACHINE        = UUID.fromString("00001826-0000-1000-8000-00805f9b34fb");

}
