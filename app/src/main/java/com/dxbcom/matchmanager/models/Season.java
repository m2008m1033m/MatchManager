package com.dxbcom.matchmanager.models;

/**
 * Created by mohammed on 6/29/16.
 */
public class Season extends Model {

    private String mName;

    public Season() {
    }

    public Season(String name) {
        setName(name);
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
