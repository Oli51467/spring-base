package com.sdu.base.gateway.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.GlobalBouncyCastleProvider;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;

import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    public static final String key = "rfoin3gj3409rf3er43rf34grevdsgq4325234r2";

    public static String createToken(Long id) {
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        DateTime now = DateTime.now();
        DateTime expTime = now.offsetNew(DateField.HOUR, 24);
        Map<String, Object> payload = new HashMap<>();
        // 签发时间
        payload.put(JWTPayload.ISSUED_AT, now);
        // 过期时间
        payload.put(JWTPayload.EXPIRES_AT, expTime);
        // 生效时间
        payload.put(JWTPayload.NOT_BEFORE, now);
        // 内容
        payload.put("id", id);
        String token = JWTUtil.createToken(payload, key.getBytes());
        return token;
    }

    public static boolean validate(String token) {
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
        // validate包含了verify
        boolean validate = jwt.validate(0);
        return validate;
    }

    public static void main(String[] args) {
        System.out.println(createToken(11L));
        System.out.println(validate(createToken(11L)));
        System.out.println(getJSONObject(createToken(11L)).get("id"));
    }

    public static JSONObject getJSONObject(String token) {
        GlobalBouncyCastleProvider.setUseBouncyCastle(false);
        JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.ISSUED_AT);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.NOT_BEFORE);
        return payloads;
    }

    /**
     * 根据Token获取uid
     */
    public static Long getUidOrNull(String token) {
        if (validate(token)) {
            return (Long) getJSONObject(token).get("id");
        } else {
            return null;
        }
    }
}
