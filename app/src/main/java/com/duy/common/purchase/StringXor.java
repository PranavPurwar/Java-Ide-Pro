package com.duy.common.purchase;

import android.support.annotation.NonNull;
import android.util.Base64;


public class StringXor {
    @NonNull
    public static String encode(String s, String key) {
        byte[] xor = xor(s.getBytes(), key.getBytes());
        byte[] encoded = Base64.encode(xor, Base64.DEFAULT);
        return new String(encoded);
    }

    @NonNull
    public static String encode(String s) {
        byte[] bytes = s.getBytes();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        return new String(encode);
    }

    @NonNull
    public static String decode(String s, String key) {
        byte[] decode = Base64.decode(s.getBytes(), Base64.DEFAULT);
        byte[] xor = xor(decode, key.getBytes());
        return new String(xor);
    }

    @NonNull
    public static String decode(String s) {
        byte[] decode = Base64.decode(s.getBytes(), Base64.DEFAULT);
        return new String(decode);
    }

    private static byte[] xor(byte[] src, byte[] key) {
        byte[] out = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = (byte) (src[i] ^ key[i % key.length]);
        }
        return out;
    }

}
