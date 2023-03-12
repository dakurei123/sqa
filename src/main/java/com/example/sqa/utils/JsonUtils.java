package com.example.sqa.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class JsonUtils {

    private ObjectMapper om;

    private JsonUtils() {
        om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public ObjectMapper getObjectMapper() {
        return om;
    }

    private static JsonUtils singleton;

    public static JsonUtils getInstance() {
        if (singleton == null) {
            singleton = new JsonUtils();
        }
        return singleton;
    }

    public static String writeToStringWithoutException(Object obj) {
        try {
            if (obj == null) return null;
            return getInstance().getObjectMapper().writeValueAsString(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

}