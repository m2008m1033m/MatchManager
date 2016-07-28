package com.dxbcom.matchmanager.models;

import com.dxbcom.matchmanager.utils.JSONUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mohammed on 6/29/16.
 */
public class Match extends Model {

    private String mClub1Name;
    private String mClub2Name;
    private String mClub1Id;
    private String mClub2Id;
    private String mTeam1Name;
    private String mTeam2Name;
    private String mTeam1Id;
    private String mTeam2Id;
    private int mTeam1Goals;
    private int mTeam2Goals;
    private String mTournament;
    private Date mDate;


    public Match() {

    }

    public Match(JSONObject jsonObject) {
        setId(JSONUtils.getString(jsonObject, "id", ""));
        setTeam1Name(JSONUtils.getString(jsonObject, "team1Name", ""));
        setTeam2Name(JSONUtils.getString(jsonObject, "team2Name", ""));
        setClub1Id(JSONUtils.getString(jsonObject, "club1Id", ""));
        setClub2Id(JSONUtils.getString(jsonObject, "club2Id", ""));
        setClub1Name(JSONUtils.getString(jsonObject, "club1Name", ""));
        setClub2Name(JSONUtils.getString(jsonObject, "club2Name", ""));
        setTeam1Id(JSONUtils.getString(jsonObject, "team1Id", ""));
        setTeam2Id(JSONUtils.getString(jsonObject, "team2Id", ""));
        setTeam1Goals(JSONUtils.getInt(jsonObject, "team1Goals", 0));
        setTeam2Goals(JSONUtils.getInt(jsonObject, "team2Goals", 0));
        setTournament(JSONUtils.getString(jsonObject, "tournament", ""));
        setDate(JSONUtils.getString(jsonObject, "date", ""));
    }

    public String getClub1Name() {
        return mClub1Name;
    }

    public void setClub1Name(String club1Name) {
        mClub1Name = club1Name;
    }

    public String getClub1Id() {
        return mClub1Id;
    }

    public void setClub1Id(String club1Id) {
        mClub1Id = club1Id;
    }

    public String getClub2Id() {
        return mClub2Id;
    }

    public void setClub2Id(String club2Id) {
        mClub2Id = club2Id;
    }

    public String getClub2Name() {
        return mClub2Name;
    }

    public void setClub2Name(String club2Name) {
        mClub2Name = club2Name;
    }

    public String getTeam1Name() {
        return mTeam1Name;
    }

    public void setTeam1Name(String team1Name) {
        mTeam1Name = team1Name;
    }

    public String getTeam2Name() {
        return mTeam2Name;
    }

    public void setTeam2Name(String team2Name) {
        mTeam2Name = team2Name;
    }

    public String getTeam1Id() {
        return mTeam1Id;
    }

    public void setTeam1Id(String team1Id) {
        mTeam1Id = team1Id;
    }

    public String getTeam2Id() {
        return mTeam2Id;
    }

    public void setTeam2Id(String team2Id) {
        mTeam2Id = team2Id;
    }

    public int getTeam1Goals() {
        return mTeam1Goals;
    }

    public void setTeam1Goals(int team1Goals) {
        mTeam1Goals = team1Goals;
    }

    public int getTeam2Goals() {
        return mTeam2Goals;
    }

    public void setTeam2Goals(int team2Goals) {
        mTeam2Goals = team2Goals;
    }

    public String getTournament() {
        return mTournament;
    }

    public void setTournament(String tournament) {
        mTournament = tournament;
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

    @Override
    public void copyFrom(Model model) {

    }


}
