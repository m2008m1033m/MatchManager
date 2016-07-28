package com.dxbcom.matchmanager.core;


import android.support.annotation.Nullable;

import com.dxbcom.matchmanager.apis.Result;
import com.dxbcom.matchmanager.models.Model;

import java.util.ArrayList;

/**
 * Created by mohammed on 2/19/16.
 */
public class ApiListeners {
    public interface OnActionExecutedListener {
        void onExecuted(Result result);
    }

    public interface OnItemLoadedListener {
        void onLoaded(Result result, @Nullable Model item);
    }

    public interface OnItemsArrayLoadedListener {
        void onLoaded(Result result, @Nullable ArrayList<Model> items);
    }
}
