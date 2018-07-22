package com.dreamwalker.diabetesfits.device.isens;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by isens on 2015. 10. 13..
 */
public class ScannerServiceParser {
    private static final int FLAGS_BIT = 0x01;
    private static final int SERVICES_MORE_AVAILABLE_16_BIT = 0x02;
    private static final int SERVICES_COMPLETE_LIST_16_BIT = 0x03;
    private static final int SERVICES_MORE_AVAILABLE_32_BIT = 0x04;
    private static final int SERVICES_COMPLETE_LIST_32_BIT = 0x05;
    private static final int SERVICES_MORE_AVAILABLE_128_BIT = 0x06;
    private static final int SERVICES_COMPLETE_LIST_128_BIT = 0x07;
    private static final int SHORTENED_LOCAL_NAME = 0x08;
    private static final int COMPLETE_LOCAL_NAME = 0x09;

    private static final byte LE_LIMITED_DISCOVERABLE_MODE = 0x01;
    private static final byte LE_GENERAL_DISCOVERABLE_MODE = 0x02;

    public final static UUID BLE_SERVICE_GLUCOSE		= UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_SERVICE_CUSTOM_TIME_MC	= UUID.fromString("11223344-5566-7788-9900-AABBCCDDEEFF");

    private int packetLength = 0;
    private boolean mIsValidSensor = false;
    private String mRequiredUUID;
    private static ScannerServiceParser mParserInstance;

    /**
     * singleton implementation of ScannerServiceParser
     */
    public static synchronized ScannerServiceParser getParser() {
        if (mParserInstance == null) {
            mParserInstance = new ScannerServiceParser();
        }
        return mParserInstance;
    }

    public boolean isValidSensor() {
        return mIsValidSensor;
    }

    /**
     * Checks if device is connectible (as Android cannot get this information directly we just check if it has GENERAL DISCOVERABLE or LIMITED DISCOVERABLE flag set) and has required service UUID in
     * the advertising packet. The service UUID may be <code>null</code>.
     * <p>
     * For further details on parsing BLE advertisement packet data see https://developer.bluetooth.org/Pages/default.aspx Bluetooth Core Specifications Volume 3, Part C, and Section 8
     * </p>
     */
    public static boolean decodeDeviceAdvData(byte[] data) {
//		final String uuid = requiredUUID != null ? requiredUUID.toString() : null;

        UUID[] filterUUID = { BLE_SERVICE_CUSTOM_TIME_MC, BLE_SERVICE_GLUCOSE };

        if (data != null) {
            boolean connectible = false;
            boolean valid = false;
            if (connectible && valid)
                return true;
            int fieldLength, fieldName;
            int packetLength = data.length;
            for (int index = 0; index < packetLength; index++) {
                fieldLength = data[index];
                if (fieldLength == 0) {
                    return connectible && valid;
                }
                fieldName = data[++index];

                for(int uuidCount = 0; uuidCount < filterUUID.length; uuidCount++){
                    final String uuid = filterUUID != null ? filterUUID[uuidCount].toString() : null;
                    if (uuid != null) {
                        if (fieldName == SERVICES_MORE_AVAILABLE_16_BIT || fieldName == SERVICES_COMPLETE_LIST_16_BIT) {
                            for (int i = index + 1; i < index + fieldLength - 1; i += 2)
                                valid = valid || decodeService16BitUUID(uuid, data, i, 2);
                        } else if (fieldName == SERVICES_MORE_AVAILABLE_32_BIT || fieldName == SERVICES_COMPLETE_LIST_32_BIT) {
                            for (int i = index + 1; i < index + fieldLength - 1; i += 4)
                                valid = valid || decodeService32BitUUID(uuid, data, i, 4);
                        } else if (fieldName == SERVICES_MORE_AVAILABLE_128_BIT || fieldName == SERVICES_COMPLETE_LIST_128_BIT) {
                            for (int i = index + 1; i < index + fieldLength - 1; i += 16)
                                valid = valid || decodeService128BitUUID(uuid, data, i, 16);
                        }
                    }
                }
                if (!connectible && fieldName == FLAGS_BIT) {
                    int flags = data[index + 1];
                    connectible = (flags & (LE_GENERAL_DISCOVERABLE_MODE | LE_LIMITED_DISCOVERABLE_MODE)) > 0;
                }
                index += fieldLength - 1;
            }
            return connectible && valid;
        }
        return false;
    }


    public static String decodeDeviceName(byte[] data) {
        String name = null;
        int fieldLength, fieldName;
        int packetLength = data.length;
        for (int index = 0; index < packetLength; index++) {
            fieldLength = data[index];
            if (fieldLength == 0)
                break;
            fieldName = data[++index];

            if (fieldName == COMPLETE_LOCAL_NAME || fieldName == SHORTENED_LOCAL_NAME) {
                name = decodeLocalName(data, index + 1, fieldLength - 1);
                break;
            }
            index += fieldLength - 1;
        }
        return name;
    }

    /**
     * Decodes the local name
     */
    public static String decodeLocalName(byte[] data, int start, int length) {
        try {
            return new String(data, start, length, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
//			Log.e(TAG, "Unable to convert the complete local name to UTF-8", e);
            return null;
        } catch (final IndexOutOfBoundsException e) {
//			Log.e(TAG, "Error when reading complete local name", e);
            return null;
        }
    }

    /**
     * check for required Service UUID inside device
     */
    private static boolean decodeService16BitUUID(String uuid, byte[] data, int startPosition, int serviceDataLength) {
        String serviceUUID = Integer.toHexString(decodeUuid16(data, startPosition));
        String requiredUUID = uuid.substring(4, 8);

        return serviceUUID.equals(requiredUUID);
    }

    /**
     * check for required Service UUID inside device
     */
    private static boolean decodeService32BitUUID(String uuid, byte[] data, int startPosition, int serviceDataLength) {
        String serviceUUID = Integer.toHexString(decodeUuid16(data, startPosition + serviceDataLength - 4));
        String requiredUUID = uuid.substring(4, 8);

        return serviceUUID.equals(requiredUUID);
    }

    /**
     * check for required Service UUID inside device
     */
    private static boolean decodeService128BitUUID(String uuid, byte[] data, int startPosition, int serviceDataLength) {
        String serviceUUID = Integer.toHexString(decodeUuid16(data, startPosition + serviceDataLength - 4));
        String requiredUUID = uuid.substring(4, 8);

        return serviceUUID.equals(requiredUUID);
    }

    private static int decodeUuid16(final byte[] data, final int start) {
        final int b1 = data[start] & 0xff;
        final int b2 = data[start + 1] & 0xff;

        return (b2 << 8 | b1);
    }
}