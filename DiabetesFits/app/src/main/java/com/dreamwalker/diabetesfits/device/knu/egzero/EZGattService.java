/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dreamwalker.diabetesfits.device.knu.egzero;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class EZGattService {

    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"; //char
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    
    //Service
    public final static UUID BLE_HEART_RATE             = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_FITNESS_MACHINE        = UUID.fromString("00001826-0000-1000-8000-00805f9b34fb");

    //public final static UUID BLE_SERVICE_DEVICE_INFO	= UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
    //public final static UUID BLE_SERVICE_CUSTOM     	= UUID.fromString("0000FFF0-0000-1000-8000-00805f9b34fb");
    //Characteristic
    public final static UUID BLE_CHAR_HEART_RATE_MEASUREMENT	= UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_INDOOR_BIKE_DATA	        = UUID.fromString("00002AD2-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_TREADMILL_DATA            = UUID.fromString("00002ACD-0000-1000-8000-00805f9b34fb");

    //Descriptor
    public final static UUID BLE_DESCRIPTOR_DESCRIPTOR	= UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }


}
