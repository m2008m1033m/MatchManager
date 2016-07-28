package com.dxbcom.matchmanager.apis;

import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.MatchHalf;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.models.Position;
import com.dxbcom.matchmanager.models.Season;
import com.dxbcom.matchmanager.models.Club;
import com.dxbcom.matchmanager.models.Team;
import com.dxbcom.matchmanager.models.Tournament;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by mohammed on 6/29/16.
 */
public class MiscApis {

    public static void seasons(ApiListeners.OnItemsArrayLoadedListener listener) {
        ArrayList<Model> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(new Season("Season" + i));
        }
        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onLoaded(r, items);

        if (true) return;
        Stub.get("", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    items.add(new Season(jsonArray.getString(i)));
                }

                return items;
            }
        }, null, "misc_seasons");
    }

    public static void clubs(ApiListeners.OnItemsArrayLoadedListener listener) {
        ArrayList<Model> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Club club = new Club();
            club.setId(String.valueOf(i));
            club.setName("Club" + i);
            items.add(club);
        }
        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onLoaded(r, items);

        if (true) return;
        Stub.get("", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    items.add(new Club(jsonArray.getJSONObject(i)));
                }

                return items;
            }
        }, null, "misc_teams");
    }

    public static void tournaments(ApiListeners.OnItemsArrayLoadedListener listener) {
        ArrayList<Model> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tournament tournament = new Tournament();
            tournament.setId(String.valueOf(i));
            tournament.setName("Tournament" + i);
            items.add(tournament);
        }
        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onLoaded(r, items);

        if (true) return;
        Stub.get("", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    items.add(new Tournament(jsonArray.getJSONObject(i)));
                }

                return items;
            }
        }, null, "misc_tournaments");
    }

    public static void teams(ApiListeners.OnItemsArrayLoadedListener listener) {
        ArrayList<Model> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Team team = new Team();
            team.setId(String.valueOf(i));
            team.setName("Team" + i);
            items.add(team);
        }
        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onLoaded(r, items);

        if (true) return;
        Stub.get("", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    items.add(new Team(jsonArray.getJSONObject(i)));
                }

                return items;
            }
        }, null, "misc_types");
    }

    public static void positions(ApiListeners.OnItemsArrayLoadedListener listener) {
        ArrayList<Model> items = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            Position position = new Position();
            position.setId(String.valueOf(i));
            position.setName("Position" + i);
            items.add(position);
        }
        Result r = new Result();
        r.setIsSucceeded(true);
        listener.onLoaded(r, items);

        if (true) return;
        Stub.get("", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    items.add(new Position(jsonArray.getJSONObject(i)));
                }

                return items;
            }
        }, null, "misc_positions");
    }

    public static void matchHalves(ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("matchHalfList", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    items.add(new MatchHalf(jsonArray.getJSONObject(i)));
                }

                return items;
            }
        }, null, "misc_match_half");
    }

}
