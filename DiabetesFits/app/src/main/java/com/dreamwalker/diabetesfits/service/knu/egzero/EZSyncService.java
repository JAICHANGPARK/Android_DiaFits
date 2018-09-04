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

package com.dreamwalker.diabetesfits.service.knu.egzero;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.dreamwalker.diabetesfits.device.knu.egzero.EGDataConverter;
import com.dreamwalker.diabetesfits.device.knu.egzero.EZGattService;
import com.dreamwalker.diabetesfits.utils.auth.Authentication;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;

import static com.dreamwalker.diabetesfits.device.knu.egzero.EGZeroConst.SYNC_ALL_DATA;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */

// TODO: 2018-08-27 에르고 미터 서비스 처리  
public class EZSyncService extends Service {
    private final static String TAG = EZSyncService.class.getSimpleName();

    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> characteristicReadQueue = new LinkedList<BluetoothGattCharacteristic>();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_DATA_AVAILABLE";

    public final static String ACTION_HEART_RATE_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_HEART_RATE_AVAILABLE";
    public final static String ACTION_INDOOR_BIKE_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_INDOOR_BIKE_AVAILABLE";
    public final static String ACTION_TREADMILL_AVAILABLE = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_TREADMILL_AVAILABLE";


    // TODO: 2018-09-04 동기화를 위한 필터
    public final static String ACTION_SERVICE_SCAN_DONE = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_SERVICE_SCAN_DONE";
    public final static String ACTION_FIRST_PHASE_DONE = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_FIRST_PHASE_DONE";
    public final static String ACTION_SECOND_PHASE_DONE = "com.dreamwalker.diabetesfits.service.knu.egzero.ACTION_SECOND_PHASE_DONE";

    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(EZGattService.HEART_RATE_MEASUREMENT);

    BluetoothGattCharacteristic mHeartRateMeasurementCharacteristic;
    BluetoothGattCharacteristic mIndoorBikeCharacteristic;
    BluetoothGattCharacteristic mTreadmillCharacteristic;

    BluetoothGattCharacteristic mDateTimeSyncCharacteristic;  //시간 동기화 특성
    BluetoothGattCharacteristic mDateTimeCharacteristic;   // 아직 정해진것 없음
    BluetoothGattCharacteristic mAuthCharacteristic;  // 인증 특성
    BluetoothGattCharacteristic mDataContextCharacteristic;   // 데이터 컨텍스트
    BluetoothGattCharacteristic mDataSyncCharacteristic;   // 데이터 전송


    boolean heartRateDescriptor = false;
    boolean indoorBikeDescriptor = false;
    boolean treadmillDescriptor = false;

    boolean dataSyncDescriptor = false;

    private void initCharacteristics() {
        mHeartRateMeasurementCharacteristic = null;
        mIndoorBikeCharacteristic = null;
        mTreadmillCharacteristic = null;

        mDateTimeSyncCharacteristic = null;
        mDateTimeCharacteristic = null;
        mAuthCharacteristic = null;
        mDataContextCharacteristic = null;
        mDataSyncCharacteristic = null;

    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SERVICE_SCAN_DONE);
        intentFilter.addAction(ACTION_FIRST_PHASE_DONE);
        intentFilter.addAction(ACTION_SECOND_PHASE_DONE);

        return intentFilter;
    }

    private boolean firstPhaseCheckerFlag = false;

    private final BroadcastReceiver mySyncReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case ACTION_SERVICE_SCAN_DONE:
                    Log.e(TAG, "onReceive: " + "서비스 스캔 끝 : 동기화 시작 " );
                    if (mDateTimeSyncCharacteristic != null) {
                        syncDateTimeToDevice(mDateTimeSyncCharacteristic);
                        firstPhaseCheckerFlag = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBluetoothGatt.readCharacteristic(mDateTimeSyncCharacteristic);
                            }
                        }, 1000);
                    }
                    break;
                case ACTION_FIRST_PHASE_DONE:
                    try {
                        byte[] authByte = Authentication.encrypt();
                        if (mAuthCharacteristic != null){
                            mAuthCharacteristic.setValue(authByte);
                            mBluetoothGatt.writeCharacteristic(mAuthCharacteristic);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothGatt.readCharacteristic(mAuthCharacteristic);
                                }
                            }, 1000);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "onReceive: " + "시간 동기화 완료 " );
                    break;

                case ACTION_SECOND_PHASE_DONE:
                    Log.e(TAG, "onReceive: " + "데이터 동기화  " );
                    if (mDataContextCharacteristic != null){
                        byte[] contextByte = new byte[]{0x02, SYNC_ALL_DATA, 0x03};
                        mDataContextCharacteristic.setValue(contextByte);
                        mBluetoothGatt.writeCharacteristic(mDataContextCharacteristic);
                    }
                    break;
            }
        }
    };

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt = gatt;
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                initCharacteristics();
                for (BluetoothGattService service : gatt.getServices()) {
                    Log.e(TAG, "onServicesDiscovered: " + service.getUuid().toString());

                    if (EZGattService.BLE_HEART_RATE.equals(service.getUuid())) {
                        mHeartRateMeasurementCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_HEART_RATE_MEASUREMENT);
                        heartRateDescriptor = enableHeartRateNotification(gatt);
                        //setCharacteristicNotification(mHeartRateMeasurementCharacteristic, true);
                        //gatt.setCharacteristicNotification(mHeartRateMeasurementCharacteristic, true);
                    } else if (EZGattService.BLE_FITNESS_MACHINE.equals(service.getUuid())) {

                        mIndoorBikeCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_INDOOR_BIKE_DATA);
                        mTreadmillCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_TREADMILL_DATA);
                        Log.e(TAG, "onServicesDiscovered: bike" + mIndoorBikeCharacteristic.getUuid().toString());
                        Log.e(TAG, "onServicesDiscovered: tread" + mTreadmillCharacteristic.getUuid().toString());

                        if (mIndoorBikeCharacteristic != null) {
                            //indoorBikeDescriptor = enableIndoorBikeNotification(gatt);
                            //setCharacteristicNotification(mIndoorBikeCharacteristic, true);
                            Log.e(TAG, "onServicesDiscovered: " + "mIndoorBikeCharacteristic set");
                        }
                        if (mTreadmillCharacteristic != null) {
                            //treadmillDescriptor = enableTreadmillNotification(gatt);
                            //setCharacteristicNotification(mTreadmillCharacteristic, true);
                            Log.e(TAG, "onServicesDiscovered: " + "mTreadmillCharacteristic set");
                        }
                    } else if (EZGattService.BLE_DATE_TIME.equals(service.getUuid())) {

                        mDateTimeSyncCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_DATE_TIME_SYNC);
                        mDateTimeCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_DATE_TIME);

                        Log.e(TAG, "onServicesDiscovered: dtSync " + mDateTimeSyncCharacteristic.getUuid().toString());
                        Log.e(TAG, "onServicesDiscovered: dt " + mDateTimeCharacteristic.getUuid().toString());

                        if (mDateTimeSyncCharacteristic != null) {
                            Log.e(TAG, "onServicesDiscovered: " + "mIndoorBikeCharacteristic set");
                        }
                        if (mDateTimeCharacteristic != null) {
                            Log.e(TAG, "onServicesDiscovered: " + "mTreadmillCharacteristic set");
                        }
                    } else if (EZGattService.BLE_DEVICE_AUTH.equals(service.getUuid())) {

                        mAuthCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_DEVICE_AUTH);
                        Log.e(TAG, "onServicesDiscovered: device auth " + mAuthCharacteristic.getUuid().toString());

                    } else if (EZGattService.BLE_DATA_SYNC.equals(service.getUuid())) {

                        mDataContextCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_DATA_CONTEXT);
                        mDataSyncCharacteristic = service.getCharacteristic(EZGattService.BLE_CHAR_DATA_SYNC);

                        Log.e(TAG, "onServicesDiscovered: data context " + mDataContextCharacteristic.getUuid().toString());
                        Log.e(TAG, "onServicesDiscovered: data sync " + mDataSyncCharacteristic.getUuid().toString());

                        if (mDateTimeSyncCharacteristic != null) {
                            broadcastUpdate(ACTION_SERVICE_SCAN_DONE);
                        }
                    }
                }
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (EZGattService.BLE_CHAR_DATE_TIME_SYNC.equals(characteristic.getUuid())) {
                    Log.e(TAG, "onCharacteristicRead: BLE_CHAR_DATE_TIME_SYNC" + characteristic.getUuid().toString());
                    byte[] receivedValue = characteristic.getValue();
                    if (receivedValue != null) {
                        if (receivedValue[0] == 0x02 && receivedValue[2] == 0x03) {
                            if (receivedValue[1] == 0x00) {

                                broadcastUpdate(ACTION_FIRST_PHASE_DONE);
                            }
                        }
                    }
                }

                if (EZGattService.BLE_CHAR_DEVICE_AUTH.equals(characteristic.getUuid())) {
                    Log.e(TAG, "onCharacteristicRead: BLE_CHAR_DEVICE_AUTH" + characteristic.getUuid().toString());
                    byte[] receivedValue = characteristic.getValue();
                    if (receivedValue != null) {
                        if (receivedValue[0] == 0x02 && receivedValue[2] == 0x03) {
                            if (receivedValue[1] == 0x00) {
                                Log.e(TAG, "onCharacteristicRead: " + "장비 인증까지 완료" );
                                broadcastUpdate(ACTION_SECOND_PHASE_DONE);
                            }
                        }
                    }
                }

//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
//                // TODO: 2018-07-24  Descriptor 쓰기가 완료될때 까지 기다려야 합니다. - 박제창
                if (heartRateDescriptor) {
                    indoorBikeDescriptor = enableIndoorBikeNotification(gatt);
                    heartRateDescriptor = false;
                } else if (indoorBikeDescriptor) {
                    treadmillDescriptor = enableTreadmillNotification(gatt);
                    indoorBikeDescriptor = false;
                } else if (treadmillDescriptor) {
                    dataSyncDescriptor = enableDataSyncNotification(gatt);
                    treadmillDescriptor = false;
                    Log.e(TAG, "onDescriptorWrite: " + "------- All Descriptor Write Done");
                } else if (dataSyncDescriptor) {
                    Log.e(TAG, "onDescriptorWrite: " + "------- All Descriptor Write Done");
                }
            } else {
                Log.d(TAG, "Callback: Error writing GATT Descriptor: " + status);
            }
//            descriptorWriteQueue.remove();  //pop the item that we just finishing writing
//            //if there is more to write, do it!
//            if(descriptorWriteQueue.size() > 0)
//                mBluetoothGatt.writeDescriptor(descriptorWriteQueue.element());
//            else if(characteristicReadQueue.size() > 0)
//                mBluetoothGatt.readCharacteristic(readQueue.element());
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                if (EZGattService.BLE_CHAR_HEART_RATE_MEASUREMENT.equals(descriptor.getCharacteristic().getUuid())) {
//                    enableHeartRateNotification(gatt);
//                }
//                if (EZGattService.BLE_CHAR_INDOOR_BIKE_DATA.equals(descriptor.getCharacteristic().getUuid())) {
//                    enableIndoorBikeNotification(gatt);
//                }
//                if (EZGattService.BLE_CHAR_TREADMILL_DATA.equals(descriptor.getCharacteristic().getUuid())) {
//                    enableTreadmillNotification(gatt);
//                }
//            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
//                if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_NONE) {
//                    Log.e(TAG, "onDescriptorWrite: Error");
//                    // broadcastUpdate(PremierNConst.INTENT_BLE_ERROR, "error", " (" + status + ")");
//                }
//            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            Log.e(TAG, "onCharacteristicChanged: " + uuid.toString());
            if (EZGattService.BLE_CHAR_INDOOR_BIKE_DATA.equals(uuid)) {
                broadcastBikeUpdate(ACTION_INDOOR_BIKE_AVAILABLE, characteristic);
            } else if (EZGattService.BLE_CHAR_TREADMILL_DATA.equals(uuid)) {
                broadcastTreadmillUpdate(ACTION_TREADMILL_AVAILABLE, characteristic);
            } else if (EZGattService.BLE_CHAR_HEART_RATE_MEASUREMENT.equals(uuid)) {
                broadcastHeartRateUpdate(ACTION_HEART_RATE_AVAILABLE, characteristic);
            }

            // TODO: 2018-09-04 데이터 동기화 서비스
            else if (EZGattService.BLE_CHAR_DATA_SYNC.equals(uuid)) {
                byte[] tmp = characteristic.getValue();
                for (byte b : tmp){
                    Log.e(TAG, "onCharacteristicChanged: " + b );
                }

//                broadcastHeartRateUpdate(ACTION_HEART_RATE_AVAILABLE, characteristic);
            }


            else {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }


    };

    private void syncDateTimeToDevice(BluetoothGattCharacteristic characteristic) {
        byte[] temp = new byte[7];
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        //현재 년도, 월, 일
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);


        //현재 (시,분,초)
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        temp[0] = (byte) ((year >> 8) & 0xff);
        temp[1] = (byte) (year & 0xff);
        temp[2] = (byte) (month & 0xff);
        temp[3] = (byte) (date & 0xff);
        temp[4] = (byte) (hour & 0xff);
        temp[5] = (byte) (min & 0xff);
        temp[6] = (byte) (sec & 0xff);
        for (byte b : temp) {
            Log.e(TAG, "syncDateTimeToDevice: " + String.format("0x%x", b));
        }

        characteristic.setValue(temp);
        mBluetoothGatt.writeCharacteristic(characteristic);

//        mDateTimeSyncCharacteristic.setValue();

    }

    public void readCharacteristic(String characteristicName) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        BluetoothGattService s = mBluetoothGatt.getService(EZGattService.BLE_HEART_RATE);
        BluetoothGattCharacteristic c = s.getCharacteristic(UUID.fromString(characteristicName));
        //put the characteristic into the read queue
        characteristicReadQueue.add(c);
        //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
        //GIVE PRECEDENCE to descriptor writes.  They must all finish first.
        if ((characteristicReadQueue.size() == 1) && (descriptorWriteQueue.size() == 0))
            mBluetoothGatt.readCharacteristic(c);
    }

    public void writeGattDescriptor(BluetoothGattDescriptor d) {
        //put the descriptor into the write queue
        descriptorWriteQueue.add(d);
        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if (descriptorWriteQueue.size() == 1) {
            mBluetoothGatt.writeDescriptor(d);
        }
    }

    private boolean enableHeartRateNotification(final BluetoothGatt gatt) {
        if (mHeartRateMeasurementCharacteristic == null) {
            return false;
        }
        gatt.setCharacteristicNotification(mHeartRateMeasurementCharacteristic, true);
        final BluetoothGattDescriptor descriptor = mHeartRateMeasurementCharacteristic.getDescriptor(EZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean checkResult = gatt.writeDescriptor(descriptor);
        return checkResult;
    }

    private boolean enableIndoorBikeNotification(final BluetoothGatt gatt) {
        if (mIndoorBikeCharacteristic == null) {
            return false;
        }
        gatt.setCharacteristicNotification(mIndoorBikeCharacteristic, true);
        final BluetoothGattDescriptor descriptor = mIndoorBikeCharacteristic.getDescriptor(EZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean checkState = gatt.writeDescriptor(descriptor);
        return checkState;
    }

    private boolean enableTreadmillNotification(final BluetoothGatt gatt) {
        if (mTreadmillCharacteristic == null) {
            return false;
        }
        gatt.setCharacteristicNotification(mTreadmillCharacteristic, true);
        final BluetoothGattDescriptor d = mTreadmillCharacteristic.getDescriptor(EZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean result = gatt.writeDescriptor(d);
        return result;
    }

    private boolean enableDataSyncNotification(final BluetoothGatt gatt) {
        if (mDataSyncCharacteristic == null) {
            return false;
        }
        gatt.setCharacteristicNotification(mDataSyncCharacteristic, true);
        final BluetoothGattDescriptor d = mDataSyncCharacteristic.getDescriptor(EZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean result = gatt.writeDescriptor(d);
        return result;
    }


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastHeartRateUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "Heart rate format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "Heart rate format UINT8.");
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        Log.d(TAG, String.format("Received heart rate: %d", heartRate));
        intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));

        // TODO: 2018-07-24 꼭 브로드 케스트를 전송해야 합니다. 나중에 삽질할 수도 있어요 - 박제창
        sendBroadcast(intent);

    }

    private void broadcastTreadmillUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
        Log.e(TAG, "BLE_CHAR_TREADMILL_DATA: " + "Flag--> " + flags);
        //int distance = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 2);
        //Log.e(TAG, "broadcastUpdate distance --> : " + distance);

        final byte[] data = characteristic.getValue();

        String dis = EGDataConverter.parseTreadmillData(data);
        Log.e(TAG, "broadcastUpdate distance --> : " + dis);
//            int distance = (data[2] >> 16) | (data[3] >> 8 ) | (data[4] & 0xFF);
//            Log.e(TAG, "broadcastUpdate distance --> : " + distance);

        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            //intent.putExtra(EXTRA_DATA, "distance : " + stringBuilder.toString() + "\n");
        }

        intent.putExtra(EXTRA_DATA, dis);
        sendBroadcast(intent);
    }

    private void broadcastBikeUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
        Log.e(TAG, "BLE_CHAR_INDOOR_BIKE_DATA: " + " flag -->  " + flags);
//            int speed = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
        final byte[] data = characteristic.getValue();

        String result = EGDataConverter.parseBluetoothData(data);
        //float speedFloat = bytearray2float(tmp);
        Log.e(TAG, "broadcastUpdate: parseSpeed --> " + result);

        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            //intent.putExtra(EXTRA_DATA, "speed : " + stringBuilder.toString() + "\n");
        }
        intent.putExtra(EXTRA_DATA, result);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
//        } else if (EZGattService.BLE_CHAR_INDOOR_BIKE_DATA.equals(characteristic.getUuid())) {
//            final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
//            Log.e(TAG, "BLE_CHAR_INDOOR_BIKE_DATA: " + " flag -->  " + flags);
////            int speed = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 2);
//            final byte[] data = characteristic.getValue();
//
//            String result = EGDataConverter.parseBluetoothData(data);
//            //float speedFloat = bytearray2float(tmp);
//            Log.e(TAG, "broadcastUpdate: parseSpeed --> " + result);
//
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for (byte byteChar : data) {
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                }
//                intent.putExtra(EXTRA_DATA, "speed : " + stringBuilder.toString() + "\n");
//            }
////            intent.putExtra(EXTRA_DATA, "속도 : " + String.valueOf(speed) + "\n");
//
//
//        } else if (EZGattService.BLE_CHAR_TREADMILL_DATA.equals(characteristic.getUuid())) {
//            final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
//            Log.e(TAG, "BLE_CHAR_TREADMILL_DATA: " + "Flag--> " + flags);
//            //int distance = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 2);
//            //Log.e(TAG, "broadcastUpdate distance --> : " + distance);
//
//            final byte[] data = characteristic.getValue();
//
//            String dis = EGDataConverter.parseTreadmillData(data);
//            Log.e(TAG, "broadcastUpdate distance --> : " + dis);
////            int distance = (data[2] >> 16) | (data[3] >> 8 ) | (data[4] & 0xFF);
////            Log.e(TAG, "broadcastUpdate distance --> : " + distance);
//
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for (byte byteChar : data) {
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                }
//                intent.putExtra(EXTRA_DATA, "distance : " + stringBuilder.toString() + "\n");
//            }
//
//        }
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public static float bytearray2float(byte[] b) {
        ByteBuffer buf = ByteBuffer.wrap(b);

        return buf.getFloat();
    }

    public class LocalBinder extends Binder {
        public EZSyncService getService() {
            return EZSyncService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        registerReceiver(mySyncReceiver, makeGattUpdateIntentFilter());
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mySyncReceiver);
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        if (mBluetoothManager != null && mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED) {
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        refreshDeviceCache(mBluetoothGatt);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    //[2016.06.10][leenain] clear gatt cache
    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        } catch (Exception localException) {
            Log.d("exception", "refreshing device");
        }
        return false;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(EZGattService.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.writeDescriptor(descriptor);
        } else if (EZGattService.BLE_CHAR_INDOOR_BIKE_DATA.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(EZGattService.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.writeDescriptor(descriptor);
        } else if (EZGattService.BLE_CHAR_TREADMILL_DATA.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(EZGattService.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.writeDescriptor(descriptor);
        }
//        if (EZGattService.BLE_CHAR_INDOOR_BIKE_DATA.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(EZGattService.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
//
//        if (EZGattService.BLE_CHAR_TREADMILL_DATA.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(EZGattService.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }

        return mBluetoothGatt.getServices();
    }


}
