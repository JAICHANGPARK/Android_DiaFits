package com.dreamwalker.diabetesfits.utils.auth;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Authentication {

    private static final String TAG = "Authentication";

    /**
     * 암호화 키 16자리
     */
    public static String key = "2222000011118888";
    public static byte[] keys = new byte[]{0x02, 0x02, 0x02, 0x02, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x01, 0x08, 0x08, 0x08, 0x08,};
    public static String  message = "9876543210000001";

    /**
     * AES 방식의 암호화
     *
     * @param message 암호화 대상 문자열
     * @return String 암호화 된 문자열
     * @throws Exception
     */
    public static byte[] encrypt() throws Exception {

        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(keys, "AES");

        // Instantiate the cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(message.getBytes());
        byte[] resizing = new byte[16];

        for(int i = 0; i < 16; i++){
            resizing[i] = encrypted[i];
        }
        for (byte anEncrypted : resizing) {
            Log.e(TAG, "onCreate: " + String.format("0x%x", anEncrypted));
        }

        return resizing;
    }

    /**
     * AES 방식의 복호화
     *
     * @param message 복호화 대상 문자열
     * @return String 복호화 된 문자열
     * @throws Exception
     */
    public static String decrypt(byte[] encrypted) throws Exception {

        // use key coss2
        SecretKeySpec skeySpec = new SecretKeySpec(keys, "AES");
        Cipher dcipher = Cipher.getInstance("AES");
        dcipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = dcipher.doFinal(encrypted);
        String originalString = new String(original);
        Log.e(TAG, "onCreate: " + originalString);
        return originalString;
    }


}
