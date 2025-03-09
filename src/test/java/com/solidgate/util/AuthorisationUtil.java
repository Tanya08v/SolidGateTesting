package com.solidgate.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthorisationUtil {

    public static final String PUBLIC_KEY = "api_pk_ccb9f752_b660_4305_9cd8_45faf7cd8288"; // your public key
    public static final String SECRET_KEY = "api_sk_cc84b108_2222_4a93_b51b_f3c970f514cc";

    public static String generateSignature(String jsonString) {
        String text = PUBLIC_KEY + jsonString + PUBLIC_KEY;
        byte[] hashedBytes = Hashing.hmacSha512(SECRET_KEY.getBytes())
                .hashString(text, StandardCharsets.UTF_8).toString().getBytes();
        return Base64.getEncoder().encodeToString(hashedBytes);
    }
}
