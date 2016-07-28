package com.dxbcom.matchmanager.models;

import com.dxbcom.matchmanager.utils.JSONUtils;

import org.json.JSONObject;

/**
 * Created by Yaser on 7/20/2016.
 */
public class MatchHalf extends Model {

    private String mName;

    public MatchHalf() {

    }

    public MatchHalf(String id, String name) {
        setId(id);
        setName(name);
    }

    public MatchHalf(JSONObject jsonObject) {
        setId(JSONUtils.getString(jsonObject, "id", ""));
        setName(JSONUtils.getString(jsonObject, "name", ""));
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    @Override
    public void copyFrom(Model model) {

    }
}
