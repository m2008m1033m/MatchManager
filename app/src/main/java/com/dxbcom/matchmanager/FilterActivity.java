package com.dxbcom.matchmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.dxbcom.matchmanager.apis.MiscApis;
import com.dxbcom.matchmanager.apis.Result;
import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.models.Season;
import com.dxbcom.matchmanager.models.Club;
import com.dxbcom.matchmanager.models.Tournament;
import com.dxbcom.matchmanager.utils.Notifications;

import java.util.ArrayList;


public class FilterActivity extends AppCompatActivity {

    private Spinner mSeasons;
    private Spinner mClubs;
    private Spinner mTournaments;
    private Button mApplyFilterButton;
    private AlertDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        setTitle(getString(R.string.filter_matches));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        /**
         * references
         */
        mSeasons = ((Spinner) findViewById(R.id.season));
        mClubs = ((Spinner) findViewById(R.id.club));
        mTournaments = ((Spinner) findViewById(R.id.tournament));
        mApplyFilterButton = ((Button) findViewById(R.id.apply_filter));

        /**
         * start loading necessary information
         */
        loadSeasons();

        /**
         * init event
         */
        mApplyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String season = mSeasons.getSelectedItemPosition() == 0 ? null : ((Season) mSeasons.getSelectedItem()).getId();
                String teamId = mClubs.getSelectedItemPosition() == 0 ? null : ((Club) mClubs.getSelectedItem()).getId();
                String tournamentId = mTournaments.getSelectedItemPosition() == 0 ? null : ((Tournament) mTournaments.getSelectedItem()).getId();

                Intent i = new Intent();
                i.putExtra(MainActivity.SEASON, season);
                i.putExtra(MainActivity.TEAM_ID, teamId);
                i.putExtra(MainActivity.TOURNAMENT_ID, tournamentId);

                setResult(0, i);
                finish();
            }
        });

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

    private void loadSeasons() {
        showProgress(true);
        MiscApis.seasons(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    items.add(0, new Season(getString(R.string.select_season)));
                    ArrayAdapter adapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSeasons.setAdapter(adapter);
                    loadTeams();
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(FilterActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadSeasons();
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

    private void loadTeams() {
        showProgress(true);
        MiscApis.clubs(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    items.add(0, new Club("", getString(R.string.select_club)));
                    ArrayAdapter adapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mClubs.setAdapter(adapter);
                    loadTournaments();
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(FilterActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadTeams();
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

    private void loadTournaments() {
        showProgress(true);
        MiscApis.tournaments(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    items.add(0, new Tournament("", getString(R.string.select_tournament)));
                    ArrayAdapter adapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mTournaments.setAdapter(adapter);
                    showProgress(false);
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(FilterActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadTournaments();
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


    private void showProgress(boolean show) {
        if (show && mProgressDialog == null) {
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        } else if (!show && mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
