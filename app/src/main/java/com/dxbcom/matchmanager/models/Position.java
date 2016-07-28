package com.dxbcom.matchmanager.models;

import com.dxbcom.matchmanager.utils.JSONUtils;

import org.json.JSONObject;

/**
 * Created by mohammed on 6/29/16.
 */
public class Position extends Model {

    private String mName;

    public Position() {
    }

    public Position(String id, String name) {
        mId = id;
        mName = name;
    }

    public Position(JSONObject jsonObject) {
        setId(JSONUtils.getString(jsonObject, "id", ""));
        setName(JSONUtils.getString(jsonObject, "name", ""));
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void copyFrom(Model model) {

    }
}
