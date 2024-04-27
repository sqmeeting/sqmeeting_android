package frtc.sdk.util;


import android.util.Base64;

import java.nio.charset.Charset;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import frtc.sdk.log.Log;

public class AESUtil {

    private static final String QR_CODE_KEY = "aab7097c02c0493093755c734d150aaf";

    private static final String TAG = AESUtil.class.getSimpleName();

    private static final String ALGORITHM = "AES";
    private static final String MODE = "ECB";
    private static final String PADDING = "PKCS5Padding";
    private static final String TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final int KEY_SIZE = 256; // bit

    public static String encrypt(String content) {
        try {
            byte[] contentByte = content.getBytes(CHARSET);

            SecretKeySpec secretKeySpec = getSecretKeySpec(QR_CODE_KEY);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] cipherByte = cipher.doFinal(contentByte);

            String base64Ciphertext = Base64.encodeToString(cipherByte, Base64.NO_WRAP);
            return base64Ciphertext;
        } catch (Exception e) {
            Log.e(TAG, "encrypt failed:" + e);
        }
        return null;
    }

    public static String decrypt(String base64Plaintext) {
        try {
            byte[] content = Base64.decode(base64Plaintext, Base64.NO_WRAP);
            SecretKeySpec secretKeySpec = getSecretKeySpec(QR_CODE_KEY);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] decryptResult = cipher.doFinal(content);

            String plaintext = new String(decryptResult, CHARSET);
            return plaintext;
        } catch (Exception e) {
            Log.e(TAG, "decrypt failed:" + e);
        }
        return null;
    }

    private static SecretKeySpec getSecretKeySpec(String key) {
        try {
            byte[] keyByte = key.getBytes(CHARSET);
            byte[] realKeyByte = Arrays.copyOfRange(keyByte, 0, (KEY_SIZE/8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(realKeyByte, ALGORITHM);
            return secretKeySpec;
        } catch (Exception e) {
            Log.e(TAG, "decrypt failed:" + e);
            throw new RuntimeException("generate SecretKeySpec error");
        }
    }

}
