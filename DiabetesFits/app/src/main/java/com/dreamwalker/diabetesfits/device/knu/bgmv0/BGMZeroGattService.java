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

package com.dreamwalker.diabetesfits.device.knu.bgmv0;

import java.util.UUID;


public class BGMZeroGattService {

    public static String HEART_RATE_MEASUREMENT                 = "00002a37-0000-1000-8000-00805f9b34fb"; //char
    public static String CLIENT_CHARACTERISTIC_CONFIG           = "00002902-0000-1000-8000-00805f9b34fb";

    //Service
    public final static UUID BLE_DATA_CONTROL                   = UUID.fromString("713d0000-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_TEST_SERVICE                   = UUID.fromString("713d0001-0000-1000-8000-00805f9b34fb");

    //Characteristic
    public final static UUID BLE_CHAR_CONTROL                   = UUID.fromString("713d0001-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_RESULT_CALLBACK           = UUID.fromString("713d0002-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_DATA_SYNC                 = UUID.fromString("713d0003-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_DEBUG_ONE                 = UUID.fromString("713d0004-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_DEBUG_TWO                 = UUID.fromString("713d0005-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_CHAR_DEBUG_THREE               = UUID.fromString("713d0006-0000-1000-8000-00805f9b34fb");

    public final static UUID BLE_CHAR_TEST                      = UUID.fromString("713d0001-0000-1000-8000-00805f9b34fb");

    //Descriptor
    public final static UUID BLE_DESCRIPTOR_DESCRIPTOR	        = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}
