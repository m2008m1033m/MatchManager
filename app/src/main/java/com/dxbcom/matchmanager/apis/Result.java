package com.dxbcom.matchmanager.apis;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mohammed on 2/19/16.
 */
public class Result {
    private boolean isSucceeded;


    /**
     * 0x02 cancelled
     */
    private String mCode;
    private ArrayList<String> mMessages = new ArrayList<>();
    private Map<String, Object> mExtra;

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public void setIsSucceeded(boolean isSucceeded) {
        this.isSucceeded = isSucceeded;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public ArrayList<String> getMessages() {
        return mMessages;
    }

    public void setExtra(Map<String, Object> extra) {
        mExtra = extra;
    }

    public Map<String, Object> getExtra() {
        return mExtra;
    }

}
