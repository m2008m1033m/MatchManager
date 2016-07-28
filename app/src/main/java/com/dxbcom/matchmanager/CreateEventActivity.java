package com.dxbcom.matchmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dxbcom.matchmanager.adapters.PlayersSpinnerAdapter;
import com.dxbcom.matchmanager.apis.MatchesApis;
import com.dxbcom.matchmanager.apis.MiscApis;
import com.dxbcom.matchmanager.apis.PlayersApis;
import com.dxbcom.matchmanager.apis.Result;
import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.Club;
import com.dxbcom.matchmanager.models.Match;
import com.dxbcom.matchmanager.models.MatchHalf;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.models.Player;
import com.dxbcom.matchmanager.utils.Notifications;

import java.util.ArrayList;

/**
 * Created by mohammed on 6/30/16.
 */
public class CreateEventActivity extends AppCompatActivity {

    public static final String MATCH_ID = "match_id";
    public static final String EVENT_TYPE = "event_type";
    public static final String TEAM_TYPE_ID = "team_type";

    private LinearLayout mTeamContainer;
    private LinearLayout mPlayerSpinnerText;
    private LinearLayout mPlayerEditEdit;
    private LinearLayout mChangePlayerSection;
    private Spinner mClubs;
    private Spinner mPlayersNames;
    private Spinner mChangePlayerSpinner;
    private EditText mPlayerName;
    private EditText mPlayerNumberEdit;
    private TextView mMinute;
    private TextView mNotes;
    private Button mConfirmButton;
    private Spinner mMatchHalfSpinner;

    public String mMatchId;
    public String mEventType;
    public String mTeamTypeId;

    private AlertDialog mProgressDialog;
    private boolean mIsLocalTeam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity);

        /**
         * action bar setup
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        mMatchId = getIntent().getStringExtra(MATCH_ID);
        mEventType = getIntent().getStringExtra(EVENT_TYPE);
        mTeamTypeId = getIntent().getStringExtra(TEAM_TYPE_ID);

        if (mMatchId == null || mEventType == null || mTeamTypeId == null) finish();

        setTitle(getString(R.string.create_s_event, mEventType));

        /**
         * references
         */
        initReferences();

        /**
         * fill
         */
        loadPlayers();

        refreshFieldsBasedOnEvent();

        /**
         * events
         */
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areFieldsValid()) return;
                String clubId = mEventType.equals("goal") ? ((Club) mClubs.getSelectedItem()).getId() : null;
                String playerId = mIsLocalTeam ? ((Player) mPlayersNames.getSelectedItem()).getId() : null;
                String newPlayerId = mEventType.equals("change") ? ((Player) mChangePlayerSpinner.getSelectedItem()).getId() : null;
                String matchHalfId = ((MatchHalf) mMatchHalfSpinner.getSelectedItem()).getId();
                String playerName = mIsLocalTeam ? null : mPlayerName.getText().toString();
                String playerNumber = mIsLocalTeam ? null : mPlayerNumberEdit.getText().toString();
                String minute = mMinute.getText().toString();
                String notes = mNotes.getText().toString().trim().isEmpty() ? null : mNotes.getText().toString().trim();

                if (newPlayerId != null && newPlayerId.equals(playerId)) {
                    Notifications.showAlertDialog(CreateEventActivity.this, getString(R.string.error), getString(R.string.old_and_new_players_cannot_be_the_same));
                    return;
                }

                MatchesApis.createEvent(clubId, mMatchId, mEventType, matchHalfId, playerId, newPlayerId, playerName, playerNumber, minute, notes, new ApiListeners.OnActionExecutedListener() {
                    @Override
                    public void onExecuted(Result result) {
                        if (result.isSucceeded()) {
                            setResult(1);
                            finish();
                        } else {
                            Notifications.showSnackBar(CreateEventActivity.this, result.getMessages().get(0));
                        }
                    }
                });
            }
        });

        setLocalTeam(true);
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

    private void initReferences() {
        mTeamContainer = ((LinearLayout) findViewById(R.id.team_container));
        mPlayerSpinnerText = ((LinearLayout) findViewById(R.id.player_spinner_text));
        mPlayerEditEdit = ((LinearLayout) findViewById(R.id.player_edit_edit));
        mChangePlayerSection = ((LinearLayout) findViewById(R.id.change_player_section));
        mClubs = ((Spinner) findViewById(R.id.club));
        mPlayersNames = ((Spinner) findViewById(R.id.player_spinner));
        mChangePlayerSpinner = ((Spinner) findViewById(R.id.player_change_spinner));
        mMatchHalfSpinner = ((Spinner) findViewById(R.id.match_half_spinner));
        mPlayerName = ((EditText) findViewById(R.id.player_edit));
        mPlayerNumberEdit = ((EditText) findViewById(R.id.player_number_edit));
        mMinute = ((EditText) findViewById(R.id.minute));
        mNotes = ((EditText) findViewById(R.id.note));
        mConfirmButton = ((Button) findViewById(R.id.confirm));
    }

    private void loadPlayers() {
        showProgress(true);
        PlayersApis.list(mTeamTypeId, new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    ArrayAdapter adapter = new PlayersSpinnerAdapter(CreateEventActivity.this, R.layout.player_item, items);
                    mPlayersNames.setAdapter(adapter);

                    adapter = new PlayersSpinnerAdapter(CreateEventActivity.this, R.layout.player_item, items);
                    mChangePlayerSpinner.setAdapter(adapter);
                    loadMatchHalves();
                } else {
                    Notifications.showYesNoDialog(CreateEventActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadPlayers();
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

    private void loadMatchHalves() {
        showProgress(true);
        MiscApis.matchHalves(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateEventActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mMatchHalfSpinner.setAdapter(adapter);
                    fill();
                    showProgress(false);
                } else {
                    Notifications.showYesNoDialog(CreateEventActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadPlayers();
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


    private void fill() {
        if (!mEventType.equals("goal")) return;
        showProgress(true);
        MatchesApis.get(mMatchId, new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                showProgress(false);
                if (result.isSucceeded() && item != null) {
                    Match match = ((Match) item);
                    ArrayList<Club> clubs = new ArrayList<>(2);
                    clubs.add(new Club(match.getClub1Id(), match.getClub1Name()));
                    clubs.add(new Club(match.getClub2Id(), match.getClub2Name()));
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateEventActivity.this, android.R.layout.simple_spinner_item, clubs);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mClubs.setAdapter(adapter);
                    mClubs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Club club = ((Club) mClubs.getItemAtPosition(position));
                            setLocalTeam(club.getId().equals("1"));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } else {
                    Notifications.showYesNoDialog(CreateEventActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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


    private void setLocalTeam(boolean isLocalTeam) {
        mPlayerEditEdit.setVisibility(isLocalTeam ? View.GONE : View.VISIBLE);
        mPlayerSpinnerText.setVisibility(isLocalTeam ? View.VISIBLE : View.GONE);
        mIsLocalTeam = isLocalTeam;
    }

    private boolean areFieldsValid() {
        String playerName = mPlayerName.getText().toString().trim();
        String playerNumber = mPlayerNumberEdit.getText().toString().trim();
        if (!mIsLocalTeam && (playerName.isEmpty() || playerNumber.isEmpty())) {
            Notifications.showAlertDialog(CreateEventActivity.this, getString(R.string.error), getString(R.string.you_must_fill_in_the_player_name_and_number));
            return false;
        }

        String minutes = mMinute.getText().toString().trim();
        if (minutes.isEmpty()) {
            Notifications.showAlertDialog(CreateEventActivity.this, getString(R.string.error), getString(R.string.you_must_fill_in_the_minute_field));
            return false;
        }

        return true;
    }

    private void refreshFieldsBasedOnEvent() {
        mTeamContainer.setVisibility(mEventType.equals("goal") ? View.VISIBLE : View.GONE);
        mChangePlayerSection.setVisibility(mEventType.equals("change") ? View.VISIBLE : View.GONE);
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
