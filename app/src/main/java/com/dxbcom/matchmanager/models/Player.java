package com.dxbcom.matchmanager.models;

import com.dxbcom.matchmanager.utils.JSONUtils;

import org.json.JSONObject;

/**
 * Created by mohammed on 6/29/16.
 */
public class Player extends Model {

    private String mName;
    private String mNumber;

    public Player() {
    }

    public Player(String id, String name) {
        mId = id;
        mName = name;
    }

    public Player(JSONObject jsonObject) {
        setId(JSONUtils.getString(jsonObject, "id", ""));
        setName(JSONUtils.getString(jsonObject, "name", ""));
        setNumber(JSONUtils.getString(jsonObject, "number", ""));
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void copyFrom(Model model) {

    }
}
