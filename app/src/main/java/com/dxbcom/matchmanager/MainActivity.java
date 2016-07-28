package com.dxbcom.matchmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dxbcom.matchmanager.adapters.MatchesAdapter;
import com.dxbcom.matchmanager.apis.MatchesApis;
import com.dxbcom.matchmanager.apis.Result;
import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.Match;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.utils.Notifications;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int FILTER = 0;
    public static final String SEASON = "season";
    public static final String TEAM_ID = "team_id";
    public static final String TOURNAMENT_ID = "tournament_id";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFloatingActionButton;

    private MatchesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.matches));

        /**
         * references
         */
        mRecyclerView = ((RecyclerView) findViewById(R.id.recycler_view));
        mSwipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout));
        mCoordinatorLayout = ((CoordinatorLayout) findViewById(R.id.coordinator_layout));
        mFloatingActionButton = ((FloatingActionButton) findViewById(R.id.floating_action_button));

        /**
         * init
         */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MatchesAdapter(new MatchesAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position, Match match) {
                Intent i = new Intent(MainActivity.this, MatchDetailsActivity.class);
                i.putExtra(MatchDetailsActivity.MATCH_ID, match.getId());
                startActivity(i);
            }

            @Override
            public void onItemLongPressed(final int position, final Match match) {
                Notifications.showYesNoDialog(MainActivity.this, getString(R.string.confirm), getString(R.string.are_you_sure_you_want_to_delete_this_event), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MatchesApis.delete(match.getId(), new ApiListeners.OnActionExecutedListener() {
                            @Override
                            public void onExecuted(Result result) {
                                if (result.isSucceeded())
                                    mAdapter.remove(position);
                                else
                                    Notifications.showSnackBar(mCoordinatorLayout, result.getMessages().get(0));
                            }
                        });
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ignore
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mAdapter);


        /**
         * events
         */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadMatches(null);
            }
        });
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateMatchActivity.class));
            }
        });

        /**
         * load
         */
        mSwipeRefreshLayout.setRefreshing(true);
        reloadMatches(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activtity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                startActivityForResult(new Intent(this, FilterActivity.class), FILTER);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER & data != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            mAdapter.clear();
            reloadMatches(data);
        }
    }

    private void reloadMatches(Intent intent) {
        String season = null;
        String teamId = null;
        String tournamentId = null;

        if (intent != null) {
            season = intent.getStringExtra(SEASON);
            teamId = intent.getStringExtra(TEAM_ID);
            tournamentId = intent.getStringExtra(TOURNAMENT_ID);
        }

        MatchesApis.list(season, teamId, tournamentId, new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (result.isSucceeded() && items != null) {
                    mAdapter.clearAndAddAll(items);
                } else {
                    Notifications.showSnackBar(mCoordinatorLayout, result.getMessages().get(0));
                }
            }
        });
    }
}
