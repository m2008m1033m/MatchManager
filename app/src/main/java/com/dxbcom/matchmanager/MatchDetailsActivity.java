package com.dxbcom.matchmanager;

import android.app.AlertDialog;
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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxbcom.matchmanager.adapters.EventsAdapter;
import com.dxbcom.matchmanager.apis.MatchesApis;
import com.dxbcom.matchmanager.apis.Result;
import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.Event;
import com.dxbcom.matchmanager.models.Match;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.utils.Notifications;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mohammed on 6/30/16.
 */
public class MatchDetailsActivity extends AppCompatActivity {

    public static final String MATCH_ID = "match_id";

    private static final int CREATE_EVENT_CODE = 0;

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    //private TabLayout mTabLayout;
    private ImageView mTeam1Logo;
    private ImageView mTeam2Logo;
    private TextView mClub1Name;
    private TextView mClub2Name;
    private TextView mTeam1Name;
    private TextView mTeam2Name;
    private TextView mGoals;
    private FloatingActionButton mFAB;

    private EventsAdapter mEventsAdapter;
    private String mMatchId;
    private Match mMatch;

    private AlertDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_details_activity);

        mMatchId = getIntent().getStringExtra(MATCH_ID);
        if (mMatchId == null) finish();

        /**
         * references
         */
        initReferences();

        /**
         * init
         */
        initRecyclerView();

        /**
         * fill
         */
        fill();

        /**
         * refresh
         */
        refreshItems();

        /**
         * event
         */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notifications.showListDialog(MatchDetailsActivity.this, getString(R.string.create_event), new String[]{
                        getString(R.string.goal),
                        getString(R.string.change),
                        getString(R.string.warning),
                        getString(R.string.kick)
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = null;
                        switch (which) {
                            case 0:
                                type = "goal";
                                break;
                            case 1:
                                type = "change";
                                break;
                            case 2:
                                type = "warning";
                                break;
                            case 3:
                                type = "kick";
                                break;
                        }
                        Intent i = new Intent(MatchDetailsActivity.this, CreateEventActivity.class);
                        i.putExtra(CreateEventActivity.MATCH_ID, mMatchId);
                        i.putExtra(CreateEventActivity.EVENT_TYPE, type);
                        i.putExtra(CreateEventActivity.TEAM_TYPE_ID, mMatch.getTeam1Id());
                        startActivityForResult(i, CREATE_EVENT_CODE);
                    }
                });
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGoals();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_CODE && resultCode == 1)
            refreshItems();
    }

    private void initReferences() {
        mCoordinatorLayout = ((CoordinatorLayout) findViewById(R.id.coordinator_layout));
        mToolbar = ((Toolbar) findViewById(R.id.toolbar));
        mSwipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout));
        mRecyclerView = ((RecyclerView) findViewById(R.id.recycler_view));
        //mTabLayout = ((TabLayout) findViewById(R.id.tab_layout));
        mTeam1Logo = ((ImageView) findViewById(R.id.team_1_logo));
        mTeam2Logo = ((ImageView) findViewById(R.id.team_2_logo));
        mClub1Name = ((TextView) findViewById(R.id.club_1_name));
        mClub2Name = ((TextView) findViewById(R.id.club_2_name));
        mTeam1Name = ((TextView) findViewById(R.id.team_1_name));
        mTeam2Name = ((TextView) findViewById(R.id.team_2_name));
        mGoals = ((TextView) findViewById(R.id.goals));
        mFAB = ((FloatingActionButton) findViewById(R.id.floating_action_button));
    }

    private void initRecyclerView() {
        mEventsAdapter = new EventsAdapter(new EventsAdapter.OnActionListener() {
            @Override
            public void onItemClicked(final int position, final Event event) {
                Notifications.showYesNoDialog(MatchDetailsActivity.this, getString(R.string.confirm), getString(R.string.are_you_sure_you_want_to_delete_this_event), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MatchesApis.deleteEvent(event.getId(), new ApiListeners.OnActionExecutedListener() {
                            @Override
                            public void onExecuted(Result result) {
                                if (result.isSucceeded())
                                    mEventsAdapter.remove(position);
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEventsAdapter);
    }

    private void fill() {
        showProgress(true);
        MatchesApis.get(mMatchId, new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                showProgress(false);
                if (result.isSucceeded() && item != null) {
                    mMatch = ((Match) item);
                    mClub1Name.setText(mMatch.getClub1Name());
                    mClub2Name.setText(mMatch.getClub2Name());
                    mTeam1Name.setText(mMatch.getTeam1Name());
                    mTeam2Name.setText(mMatch.getTeam2Name());
                    setGoals();

                    Picasso.with(getApplicationContext())
                            .load(R.drawable.logo) //TODO team 1 logo
                            .placeholder(R.drawable.placeholder)
                            .into(mTeam1Logo);

                    Picasso.with(getApplicationContext())
                            .load(R.drawable.logo) // TODO team 2 logo
                            .placeholder(R.drawable.placeholder)
                            .into(mTeam2Logo);

                } else {
                    Notifications.showYesNoDialog(MatchDetailsActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fill();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void refreshItems() {
        mSwipeRefreshLayout.setRefreshing(true);
        MatchesApis.events(mMatchId, new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    mEventsAdapter.clearAndAddAll(items);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    Notifications.showSnackBar(mCoordinatorLayout, result.getMessages().get(0));
                }
            }
        });
    }

    private void refreshGoals() {
        MatchesApis.get(mMatchId, new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                if (result.isSucceeded() && item != null) {
                    mMatch = ((Match) item);
                    setGoals();
                } else {
                    refreshGoals();
                }
            }
        });
    }

    private void setGoals() {
        mGoals.setText(mMatch.getTeam1Goals() + ":" + mMatch.getTeam2Goals());
    }

    private void showProgress(boolean show) {
        if (show && mProgressDialog == null) {
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        } else if (!show && mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

}
