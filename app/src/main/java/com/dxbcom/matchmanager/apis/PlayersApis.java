package com.dxbcom.matchmanager.apis;

import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.models.Player;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by mohammed on 6/29/16.
 */
public class PlayersApis {

    public static void list(String teamId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("PlayerList", listener, new Stub.ModelParser() {
            @Override
            public ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>(len);
                for (int i = 0; i < len; i++)
                    items.add(new Player(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams("teamId", teamId), "players_list");
    }

}
