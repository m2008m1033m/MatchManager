package com.dxbcom.matchmanager.apis;

import android.support.annotation.Nullable;

import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.Event;
import com.dxbcom.matchmanager.models.Match;
import com.dxbcom.matchmanager.models.Model;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MatchesApis {

    public static void list(@Nullable String season, @Nullable String teamId, @Nullable String tournamentId, ApiListeners.OnItemsArrayLoadedListener listener) {

        RequestParams params = new RequestParams();
        if (season != null)
            params.put("season", season);
        if (teamId != null)
            params.put("teamId", teamId);
        if (tournamentId != null)
            params.put("tournamentId", tournamentId);

        Stub.get("matches/list", listener, new Stub.ModelParser() {

            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++)
                    items.add(new Match(jsonArray.getJSONObject(i)));

                return items;
            }
        }, params, "matches_list");
    }

    public static void get(String id, ApiListeners.OnItemLoadedListener listener) {

        Stub.get("matcheById", listener, new Stub.ModelParser() {
            @Override
            public Model parseItem(String response) throws JSONException {
                return new Match(new JSONObject(response));
            }
        }, new RequestParams("id", id), "matches_get");
    }

    public static void create(Date date, String time, String season, String clubId, String tournamentId, String teamId, String field, String hostedLocally, ApiListeners.OnActionExecutedListener listener) {

        Result r = new Result();
        r.setIsSucceeded(true);
        Map<String, Object> data = new HashMap<>();
        data.put("matchId", "0");
        r.setExtra(data);
        listener.onExecuted(r);

        if (true) return;
        RequestParams params = new RequestParams();
        params.put("date", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(date));
        params.put("time", time);
        params.put("season", season);
        params.put("clubId", clubId);
        params.put("tournamentId", tournamentId);
        params.put("teamId", teamId);
        params.put("field", field);
        params.put("hostedLocally", hostedLocally);

        Stub.post("", listener, null, params, "matches_create");

    }

    public static void delete(String matchId, ApiListeners.OnActionExecutedListener listener) {
        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onExecuted(r);
        if (true) return;
        Stub.post("", listener, null, new RequestParams("matchId", matchId), "matches_delete_event");
    }

    public static void teamFormation(String matchId, String[] majorPlayersIds, String[] positionsIds, String[] secondaryPlayersIds, String captainId, ApiListeners.OnActionExecutedListener listener) {

        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onExecuted(r);

        if (true) return;
        RequestParams params = new RequestParams();

        params.add("matchId", matchId);
        params.add("captainId", captainId);

        for (String id : majorPlayersIds)
            params.add("majorPlayersIds[]", id);

        for (String id : positionsIds)
            params.add("positionsIds[]", id);

        for (String id : secondaryPlayersIds)
            params.add("secondaryPlayersIds[]", id);

        Stub.post("", listener, null, params, "matches_team_formation");

    }

    public static void events(String id, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.add("id", id);

        Stub.get("matches/events", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++)
                    items.add(new Event(jsonArray.getJSONObject(i)));
                return items;
            }
        }, params, "matches_events");
    }

    public static void createEvent(@Nullable String clubId, String matchId, String eventType, String matchHalf, @Nullable String playerId, @Nullable String playerName, @Nullable String playerNumber, String minute, @Nullable String notes, ApiListeners.OnActionExecutedListener listener) {

        RequestParams params = new RequestParams();
        if (clubId != null) params.put("clubId", clubId);
        params.put("eventType", eventType);
        params.put("matchId", matchId);
        params.put("matchHalf", matchHalf);
        if (playerId != null) params.put("playerId", playerId);
        if (playerName != null) params.put("playerName", playerName);
        if (playerNumber != null) params.put("playerNumber", playerNumber);
        params.put("minute", minute);
        if (notes != null) params.put("notes", notes);

        Stub.post("event/add", listener, null, params, "matches_create_event");
    }

    public static void deleteEvent(String eventId, ApiListeners.OnActionExecutedListener listener) {
        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onExecuted(r);
        if (true) return;
        Stub.post("", listener, null, new RequestParams("eventId", eventId), "matches_delete_event");
    }


}
