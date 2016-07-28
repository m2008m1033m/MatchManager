package com.dxbcom.matchmanager.models;

import com.dxbcom.matchmanager.utils.JSONUtils;
import com.dxbcom.matchmanager.utils.MiscUtils;

import org.json.JSONObject;

/**
 * Created by mohammed on 6/29/16.
 */
public class Tournament extends Model {

    private String mName;

    public Tournament() {
    }

    public Tournament(String id, String name) {
        mId = id;
        mName = name;
    }

    public Tournament(JSONObject jsonObject) {
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
