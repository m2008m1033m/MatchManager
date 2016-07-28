package com.dxbcom.matchmanager.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mohammed on 6/29/16.
 */
public class JSONUtils {
    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    /**
     * json getters:
     */
    public static String getString(JSONObject jsonObject, String name, String defaultValue) {
        try {
            String string = jsonObject.getString(name).trim();
            return string.equals("null") ? "" : string;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static String getUTF8String(JSONObject jsonObject, String name, String defaultValue) {
        try {
            String string = new String(jsonObject.getString(name).trim().getBytes(), "UTF-8");
            return string.equals("null") ? "" : string;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static int getInt(JSONObject jsonObject, String name, int defaultValue) {
        try {
            return Integer.parseInt(jsonObject.getString(name).trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static float getFloat(JSONObject jsonObject, String name, float defaultValue) {
        try {
            return Float.parseFloat(jsonObject.getString(name).trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static double getDouble(JSONObject jsonObject, String name, double defaultValue) {
        try {
            return Double.parseDouble(jsonObject.getString(name).trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean getBoolean(JSONObject jsonObject, String name, boolean defaultValue) {
        try {
            String val = jsonObject.getString(name).trim();
            return val.equals("1") ? true : val.equals("0") ? false : val.equals("true") ? true : false;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getJSONObject(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int idx) {
        try {
            return jsonArray.getJSONObject(idx);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
