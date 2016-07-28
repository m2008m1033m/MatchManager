package com.dxbcom.matchmanager.models;

import com.dxbcom.matchmanager.utils.JSONUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mohammed on 6/30/16.
 */
public class Event extends Model {

    public enum Type {
        GOAL,
        CHANGE,
        WARNING,
        KICK
    }

    private String mPlayerName;
    private String mTeamId;
    private int mPlayerNumber;
    private Type mType;
    private int mMinute;
    private Date mDate;
    private String mName;
    private String mNotes;

    public Event() {

    }

    public Event(JSONObject jsonObject) {
        setId(JSONUtils.getString(jsonObject, "id", ""));
        setPlayerName(JSONUtils.getString(jsonObject, "playerName", ""));
        setTeamId(JSONUtils.getString(jsonObject, "teamId", ""));
        setPlayerNumber(JSONUtils.getInt(jsonObject, "playerNumber", 0));
        setType(JSONUtils.getString(jsonObject, "type", ""));
        setMinute(JSONUtils.getInt(jsonObject, "minute", 0));
        setDate(JSONUtils.getString(jsonObject, "date", "1970-01-01'T'00:00:00"));
        setName(JSONUtils.getString(jsonObject, "name", ""));
        setNotes(JSONUtils.getString(jsonObject, "notes", ""));
    }


    public String getPlayerName() {
        return mPlayerName;
    }

    public void setPlayerName(String playerName) {
        mPlayerName = playerName;
    }

    public int getPlayerNumber() {
        return mPlayerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        mPlayerNumber = playerNumber;
    }

    public String getTeamId() {
        return mTeamId;
    }

    public void setTeamId(String teamId) {
        mTeamId = teamId;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public String getTypeAsString() {
        switch (mType) {
            case GOAL:
                return "goal";
            case KICK:
                return "kick";
            case CHANGE:
                return "change";
            case WARNING:
                return "warning";
        }
        return null;
    }

    public void setType(String type) {
        switch (type) {
            case "goal":
                mType = Type.GOAL;
                break;
            case "kick":
                mType = Type.KICK;
                break;
            case "change":
                mType = Type.CHANGE;
                break;
            case "warning":
                mType = Type.WARNING;
                break;
        }
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDateAsString(String format) {
        return new SimpleDateFormat(format, Locale.US).format(mDate);
    }

    public void setDate(String date) {
        try {
            mDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    @Override
    public void copyFrom(Model model) {

    }
}
