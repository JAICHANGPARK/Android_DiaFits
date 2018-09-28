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

package com.dreamwalker.diabetesfits.service.knu.treadmillzero;

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
import com.dreamwalker.diabetesfits.device.knu.treadmillzero.TMZConst;
import com.dreamwalker.diabetesfits.device.knu.treadmillzero.TMZGattService;
import com.dreamwalker.diabetesfits.model.fitness.Fitness;
import com.dreamwalker.diabetesfits.utils.auth.Authentication;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;



/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */

// TODO: 2018-08-27 에르고 미터 서비스 처리  
public class TMZeroSyncService extends Service {
    private final static String TAG = TMZeroSyncService.class.getSimpleName();

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


    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(TMZGattService.HEART_RATE_MEASUREMENT);

    BluetoothGattCharacteristic mHeartRateMeasurementCharacteristic;
    BluetoothGattCharacteristic mIndoorBikeCharacteristic;
    BluetoothGattCharacteristic mTreadmillCharacteristic;

    BluetoothGattCharacteristic mDateTimeSyncCharacteristic;  //시간 동기화 특성
    BluetoothGattCharacteristic mDateTimeCharacteristic;   // 아직 정해진것 없음
    BluetoothGattCharacteristic mAuthCharacteristic;  // 인증 특성
    BluetoothGattCharacteristic mDataContextCharacteristic;   // 데이터 컨텍스트
    BluetoothGattCharacteristic mDataSyncCharacteristic;   // 데이터 전송
    BluetoothGattCharacteristic mDataResultCharacteristic; // 결과


    boolean heartRateDescriptor = false;
    boolean indoorBikeDescriptor = false;
    boolean treadmillDescriptor = false;
    boolean dataSyncDescriptor = false;
    boolean dataResultDescriptor = false;

    private void initCharacteristics() {
        mHeartRateMeasurementCharacteristic = null;
        mIndoorBikeCharacteristic = null;
        mTreadmillCharacteristic = null;

        mDateTimeSyncCharacteristic = null;
        mDateTimeCharacteristic = null;
        mAuthCharacteristic = null;
        mDataContextCharacteristic = null;
        mDataSyncCharacteristic = null;

        mDataResultCharacteristic = null;
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TMZConst.ACTION_SERVICE_SCAN_DONE);
        intentFilter.addAction(TMZConst.ACTION_FIRST_PHASE_DONE);
        intentFilter.addAction(TMZConst.ACTION_SECOND_PHASE_DONE);
        intentFilter.addAction(TMZConst.INTENT_BLE_OPERATESTARTED);

        return intentFilter;
    }

    private boolean firstPhaseCheckerFlag = false;
    private boolean secondPhaseCheckerFlag = false;
    private boolean finalPhaseResult = false;

    ArrayList<Fitness> fitnessArrayList = new ArrayList<>();

    // TODO: 2018-09-10 서비스 내의 브로드캐스트 리시버 - 박제창 
    private final BroadcastReceiver mySyncReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action) {
                case TMZConst.ACTION_SERVICE_SCAN_DONE:
                    Log.e(TAG, "onReceive: " + "서비스 스캔 끝 : 동기화 시작 ");
                    if (mDateTimeSyncCharacteristic != null) {
                        firstPhaseCheckerFlag = syncDateTimeToDevice(mDateTimeSyncCharacteristic);
                        Log.e(TAG, "onReceive: ACTION_SERVICE_SCAN_DONE --> " + firstPhaseCheckerFlag);
                    }
                    break;

                case TMZConst.ACTION_FIRST_PHASE_DONE:
                    Log.e(TAG, "onReceive: " + "디바이스 인증 시작 ");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mAuthCharacteristic != null) {
                                try {
                                    secondPhaseCheckerFlag = authDevicePhase(mAuthCharacteristic);
                                    Log.e(TAG, "onReceive: ACTION_FIRST_PHASE_DONE --> " + secondPhaseCheckerFlag);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 500);

                    Log.e(TAG, "onReceive: " + "디바이스 인증 완료 ");
                    break;

                case TMZConst.ACTION_SECOND_PHASE_DONE:
                    Log.e(TAG, "onReceive: " + "데이터 동기화 시작  ");

                    boolean secondDescriptorResult = enableDataSyncNotification(mBluetoothGatt);
                    Log.e(TAG, "secondDescriptorResult: " + secondDescriptorResult);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mDataContextCharacteristic != null) {
                                finalPhaseResult = syncDataFromDevice(mDataContextCharacteristic);
                                Log.e(TAG, "onReceive: 쓰기 결과 --> " + finalPhaseResult);
                            }
                        }
                    }, 500);
                    break;
                case TMZConst.INTENT_BLE_OPERATESTARTED:
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
                intentAction = TMZConst.ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.e(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.e(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = TMZConst.ACTION_GATT_DISCONNECTED;
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

                    if (TMZGattService.BLE_HEART_RATE.equals(service.getUuid())) {
                        mHeartRateMeasurementCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_HEART_RATE_MEASUREMENT);
                    } else if (TMZGattService.BLE_FITNESS_MACHINE.equals(service.getUuid())) {

                        mIndoorBikeCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_INDOOR_BIKE_DATA);
                        mTreadmillCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_TREADMILL_DATA);
                        Log.e(TAG, "onServicesDiscovered: bike" + mIndoorBikeCharacteristic.getUuid().toString());
                        Log.e(TAG, "onServicesDiscovered: tread" + mTreadmillCharacteristic.getUuid().toString());

                        if (mIndoorBikeCharacteristic != null) {
                            Log.e(TAG, "onServicesDiscovered: " + "mIndoorBikeCharacteristic set");
                        }
                        if (mTreadmillCharacteristic != null) {
                            Log.e(TAG, "onServicesDiscovered: " + "mTreadmillCharacteristic set");
                        }
                    } else if (TMZGattService.BLE_DATE_TIME.equals(service.getUuid())) {

                        mDateTimeSyncCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_DATE_TIME_SYNC);
                        mDataResultCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_RESULT);

                        Log.e(TAG, "onServicesDiscovered: dtSync " + mDateTimeSyncCharacteristic.getUuid().toString());
                        Log.e(TAG, "onServicesDiscovered: result " + mDataResultCharacteristic.getUuid().toString());

                        if (mDateTimeSyncCharacteristic != null) {
                            Log.e(TAG, "onServicesDiscovered: " + "mDateTimeSyncCharacteristic set");
                        }
                        if (mDataResultCharacteristic != null) {
                            // TODO: 2018-09-10 결과를 받기위한 노티피케이션 디스크립션 설정 on
                            dataResultDescriptor = enableResultNotification(gatt);
                            Log.e(TAG, "onServicesDiscovered: " + "mDataResultCharacteristic set");
                        }
                    } else if (TMZGattService.BLE_DEVICE_AUTH.equals(service.getUuid())) {

                        mAuthCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_DEVICE_AUTH);
                        Log.e(TAG, "onServicesDiscovered: device auth " + mAuthCharacteristic.getUuid().toString());

                    } else if (TMZGattService.BLE_DATA_SYNC.equals(service.getUuid())) {

                        mDataContextCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_DATA_CONTEXT);
                        mDataSyncCharacteristic = service.getCharacteristic(TMZGattService.BLE_CHAR_DATA_SYNC);

                        Log.e(TAG, "onServicesDiscovered: data context " + mDataContextCharacteristic.getUuid().toString());
                        Log.e(TAG, "onServicesDiscovered: data sync " + mDataSyncCharacteristic.getUuid().toString());

                        if (mDataSyncCharacteristic != null) {
                            Log.e(TAG, "mDataSyncCharacteristic: " + mDataSyncCharacteristic);
                        }
                    }
                }
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onCharacteristicRead: " + "Called");
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {


                // TODO: 2018-09-05 만약 데이터 결과 디스크립터가 설정되면
                if (dataResultDescriptor) {
                    Log.e(TAG, "onDescriptorWrite: " + "------- dataResultDescriptor Descriptor Write Done");
                    Log.e(TAG, "onDescriptorWrite: dataSyncDescriptor -> " + dataSyncDescriptor);
                    broadcastUpdate(TMZConst.ACTION_SERVICE_SCAN_DONE);
                    dataResultDescriptor = false; // TODO: 2018-09-05 콜백이 다시 발생하면 다시 시간 동기화를 진행하기때문에 플레그를 끈다.
                }

                if (dataSyncDescriptor) {
                    Log.e(TAG, "onDescriptorWrite: " + "------- dataSyncDescriptor Descriptor Write Done");
                    dataSyncDescriptor = false;
                }

            } else {
                Log.d(TAG, "Callback: Error writing GATT Descriptor: " + status);
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            Log.e(TAG, "onCharacteristicChanged: " + uuid.toString());
//            if (EZGattService.BLE_CHAR_INDOOR_BIKE_DATA.equals(uuid)) {
//                broadcastBikeUpdate(ACTION_INDOOR_BIKE_AVAILABLE, characteristic);
//            } else if (EZGattService.BLE_CHAR_TREADMILL_DATA.equals(uuid)) {
//                broadcastTreadmillUpdate(ACTION_TREADMILL_AVAILABLE, characteristic);
//            } else if (EZGattService.BLE_CHAR_HEART_RATE_MEASUREMENT.equals(uuid)) {
//                broadcastHeartRateUpdate(ACTION_HEART_RATE_AVAILABLE, characteristic);
//
//            }

            if (TMZGattService.BLE_CHAR_RESULT.equals(uuid)) {
                Log.e(TAG, "onCharacteristicChanged: BLE_CHAR_RESULT " + characteristic.getUuid().toString());
                byte[] receivedValue = characteristic.getValue();
                for (byte b : receivedValue) {
                    Log.e(TAG, "onCharacteristicRead: 결과 받은 데이터 " + b);
                }

                if (receivedValue != null) {

                    if (firstPhaseCheckerFlag) {
                        if (receivedValue[0] == 0x02 && receivedValue[2] == 0x03) {
                            if (receivedValue[1] == 0x01) {
                                Log.e(TAG, "onCharacteristicChanged 시간 동기화 : " + "성공");
                                broadcastUpdate(TMZConst.ACTION_FIRST_PHASE_DONE);
                                broadcastUpdate(TMZConst.ACTION_FIRST_DONE);
                            }
                        }
                    }

                    if (secondPhaseCheckerFlag) {
                        if (receivedValue[0] == 0x02 && receivedValue[2] == 0x03) {
                            if (receivedValue[1] == 0x02) {
                                Log.e(TAG, "onCharacteristicChanged 디바이스 인증 : " + "성공");
                                broadcastUpdate(TMZConst.ACTION_SECOND_PHASE_DONE);
                                broadcastUpdate(TMZConst.ACTION_SECOND_DONE);
//                                dataSyncDescriptor = enableDataSyncNotification(gatt);
                            }
                        }
                    }

                    if (finalPhaseResult) {
                        if (receivedValue[0] == 0x02 && receivedValue[2] == 0x03) {
                            if (receivedValue[1] == 0x03) {
                                Log.e(TAG, "onCharacteristicChanged 전송 완료  : " + "성공");
                                // TODO: 2018-09-05 데이터 전송 완료 엑티비티 전환

                                Gson gson = new Gson();
                                String gsonConverted = gson.toJson(fitnessArrayList);
                                Log.e(TAG, "gson 변경 : " + gsonConverted);
                                broadcastFinalUpdate(TMZConst.ACTION_FINAL_DONE, gsonConverted);
                            }
                        }
                    }
                }
            }

            // TODO: 2018-09-04 데이터 동기화 서비스 및 동기화 처리 - 박제창

            else if (TMZGattService.BLE_CHAR_DATA_SYNC.equals(uuid)) {
                byte[] data = characteristic.getValue();

                int index = ((data[0] << 8 & 0xff00) | (data[1] & 0xff));

                int receivedYear = ((data[2] << 8 & 0xff00) | (data[3] & 0xff));
                int receivedMonth = (data[4] & 0xff);
                int receivedDay = (data[5] & 0xff);
                int receivedHour = (data[6] & 0xff);
                int receivedMinute = (data[7] & 0xff);
                int receivedSecond = (data[8] & 0xff);

                int receivedFitnessTime = ((data[9] << 8 & 0xff00) | (data[10] & 0xff));

                final int intVal = (data[11] << 8) & 0xff00 | (data[12] & 0xff);
                float receivedFitnessSpeed = (float) intVal / 100;

                final int intVal2 = (data[13] << 8) & 0xff00 | (data[14] & 0xff);
                float receivedFitnessDistance = (float) intVal2 / 100;

                int receivedFitnessHeartRate = data[15] & 0xff;

                int receivedFitnessKCal = ((data[16] << 8 & 0xff00) | (data[17] & 0xff));

                Log.e(TAG, "onCharacteristicChanged: " + index + "|" + receivedYear + "|" + receivedMonth +
                        "|" + receivedDay + "|" + receivedHour + "|" + receivedMinute + "|" + receivedSecond
                        + "|" + receivedFitnessTime
                        + "|" + receivedFitnessSpeed
                        + "|" + receivedFitnessDistance
                        + "|" + receivedFitnessHeartRate
                        + "|" + receivedFitnessKCal
                );


                Date receivedDate = getDate(receivedYear, receivedMonth, receivedDay, receivedHour, receivedMinute, receivedSecond);
                Log.e(TAG, "onCharacteristicChanged: receivedDate -->  " + receivedDate.getTime());
                Log.e(TAG, "onCharacteristicChanged: receivedDate -->  " + receivedDate.toString());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss", Locale.KOREA);
                SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);

                String saveDate = simpleDateFormat.format(receivedDate);
                String saveTime = simpleTimeFormat.format(receivedDate);
                String saveDateTime = simpleDateTimeFormat.format(receivedDate);
                long saveTimestamp = receivedDate.getTime();

                if (data != null && data.length > 0) {

                    fitnessArrayList.add(new Fitness("0", "0", String.valueOf(receivedFitnessTime),
                            String.valueOf(receivedFitnessDistance),
                            String.valueOf(receivedFitnessSpeed),
                            String.valueOf(receivedFitnessHeartRate),
                            saveDate,
                            saveTime,
                            saveDateTime,
                            saveTimestamp,
                            receivedDate
                    ));

//                    final StringBuilder stringBuilder = new StringBuilder(data.length);
//                    for (byte byteChar : data)
//                        stringBuilder.append(String.format("%02X ", byteChar));
//                    intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                }

//                for (byte b : data) {
//                    Log.e(TAG, "onCharacteristicChanged: " + b);
//                }

//                broadcastHeartRateUpdate(ACTION_HEART_RATE_AVAILABLE, characteristic);
            } else {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }


    };

    public static Date getDate(int year, int month, int date, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, date, hour, minute, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    private boolean authDevicePhase(BluetoothGattCharacteristic characteristic) throws Exception {
        byte[] authByte = Authentication.encrypt(TMZConst.AUTH_MESSAGE);
        characteristic.setValue(authByte);
        return mBluetoothGatt.writeCharacteristic(mAuthCharacteristic);

//        broadcastUpdate(INTENT_BLE_OPERATESTARTED);
//        characteristic.setValue(new byte[authByte.length]);


    }

    private boolean syncDataFromDevice(BluetoothGattCharacteristic characteristic) {

        if (mDataSyncCharacteristic == null) {
            return false;
        }

        byte[] contextByte = {0x02, 0x00, 0x03};

//        broadcastUpdate(INTENT_BLE_OPERATESTARTED);

//        characteristic.setValue(new byte[contextByte.length]);

        characteristic.setValue(contextByte);
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    private boolean syncDateTimeToDevice(BluetoothGattCharacteristic characteristic) {

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

        Log.e(TAG, "syncDateTimeToDevice: 현재시간" + year + month + date + hour + min + sec);

        temp[0] = (byte) ((year >> 8) & 0xff);
        temp[1] = (byte) (year & 0xff);
        temp[2] = (byte) (month & 0xff);
        temp[3] = (byte) (date & 0xff);
        temp[4] = (byte) (hour & 0xff);
        temp[5] = (byte) (min & 0xff);
        temp[6] = (byte) (sec & 0xff);

        broadcastUpdate(TMZConst.INTENT_BLE_OPERATESTARTED);

        characteristic.setValue(new byte[temp.length]);
        characteristic.setValue(temp);
        return mBluetoothGatt.writeCharacteristic(characteristic);

    }


    // TODO: 2018-09-05 여기 이후 코드는 ..


    public void readCharacteristic(String characteristicName) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        BluetoothGattService s = mBluetoothGatt.getService(TMZGattService.BLE_HEART_RATE);
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
        final BluetoothGattDescriptor descriptor = mHeartRateMeasurementCharacteristic.getDescriptor(TMZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean checkResult = gatt.writeDescriptor(descriptor);
        return checkResult;
    }

    private boolean enableIndoorBikeNotification(final BluetoothGatt gatt) {
        if (mIndoorBikeCharacteristic == null) {
            return false;
        }
        gatt.setCharacteristicNotification(mIndoorBikeCharacteristic, true);
        final BluetoothGattDescriptor descriptor = mIndoorBikeCharacteristic.getDescriptor(TMZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean checkState = gatt.writeDescriptor(descriptor);
        return checkState;
    }

    private boolean enableTreadmillNotification(final BluetoothGatt gatt) {
        if (mTreadmillCharacteristic == null) {
            return false;
        }
        gatt.setCharacteristicNotification(mTreadmillCharacteristic, true);
        final BluetoothGattDescriptor d = mTreadmillCharacteristic.getDescriptor(TMZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean result = gatt.writeDescriptor(d);
        return result;
    }

    private boolean enableResultNotification(final BluetoothGatt gatt) {
        if (mDataResultCharacteristic == null) {
            return false;
        }
        boolean notiResult = gatt.setCharacteristicNotification(mDataResultCharacteristic, true);
        Log.e(TAG, "enableResultNotification: " + notiResult);
        final BluetoothGattDescriptor d = mDataResultCharacteristic.getDescriptor(TMZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean result = gatt.writeDescriptor(d);
        return result;
    }

    private boolean enableDataSyncNotification(final BluetoothGatt gatt) {
        if (mDataSyncCharacteristic == null) {
            return false;
        }
        boolean notiResult = gatt.setCharacteristicNotification(mDataSyncCharacteristic, true);
        Log.e(TAG, "enableDataSyncNotification: " + notiResult);
        final BluetoothGattDescriptor d = mDataSyncCharacteristic.getDescriptor(TMZGattService.BLE_DESCRIPTOR_DESCRIPTOR);
        d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean result = gatt.writeDescriptor(d);
        return result;
    }


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 최종 값을 전달 받는
     *
     * @param action
     * @param data
     */
    private void broadcastFinalUpdate(final String action, final String data) {
        final Intent intent = new Intent(action);
        intent.putExtra(TMZConst.EXTRA_FINAL_DATA, data);
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
        intent.putExtra(TMZConst.EXTRA_DATA, String.valueOf(heartRate));

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

        intent.putExtra(TMZConst.EXTRA_DATA, dis);
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
        intent.putExtra(TMZConst.EXTRA_DATA, result);
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
            intent.putExtra(TMZConst.EXTRA_DATA, String.valueOf(heartRate));
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
                intent.putExtra(TMZConst.EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public static float bytearray2float(byte[] b) {
        ByteBuffer buf = ByteBuffer.wrap(b);

        return buf.getFloat();
    }

    public class LocalBinder extends Binder {
        public TMZeroSyncService getService() {
            return TMZeroSyncService.this;
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
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();


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
        Log.e(TAG, "서비스 연결 되고 블루수트 연결 시도합니다.");
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
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(TMZGattService.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.writeDescriptor(descriptor);
        } else if (TMZGattService.BLE_CHAR_INDOOR_BIKE_DATA.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(TMZGattService.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.writeDescriptor(descriptor);
        } else if (TMZGattService.BLE_CHAR_TREADMILL_DATA.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(TMZGattService.CLIENT_CHARACTERISTIC_CONFIG));
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
