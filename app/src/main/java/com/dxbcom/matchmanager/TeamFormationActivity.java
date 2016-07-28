package com.dxbcom.matchmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dxbcom.matchmanager.apis.MatchesApis;
import com.dxbcom.matchmanager.apis.MiscApis;
import com.dxbcom.matchmanager.apis.PlayersApis;
import com.dxbcom.matchmanager.apis.Result;
import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.customViews.TableLayoutWithRadioButton;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.models.Player;
import com.dxbcom.matchmanager.models.Position;
import com.dxbcom.matchmanager.utils.Notifications;

import java.util.ArrayList;

/**
 * Created by mohammed on 6/29/16.
 */
public class TeamFormationActivity extends AppCompatActivity {

    public static final String MATCH_ID = "match_id";
    public static final String TEAM_TYPE_ID = "team_type_id";

    private TableLayoutWithRadioButton mMajorPlayersContainer;
    private LinearLayout mSecondaryPlayersContainer;
    private Button mConfirmFormationButton;

    private AlertDialog mProgressDialog;

    private ArrayList mPlayers;
    private ArrayList mPositions;

    private ArrayList<Spinner> mMajorPlayersSpinners = new ArrayList<>();
    private ArrayList<Spinner> mSecondaryPlayersSpinners = new ArrayList<>();
    private ArrayList<Spinner> mPositionsSpinners = new ArrayList<>();

    private String mTeamTypeId;
    private String mMatchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_formation_activity);

        mMatchId = getIntent().getStringExtra(MATCH_ID);
        mTeamTypeId = getIntent().getStringExtra(TEAM_TYPE_ID);

        /**
         * action bar setup
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        setTitle(getString(R.string.team_formation));

        /**
         * references
         */
        mMajorPlayersContainer = ((TableLayoutWithRadioButton) findViewById(R.id.major_players));
        mSecondaryPlayersContainer = ((LinearLayout) findViewById(R.id.secondary_players));
        mConfirmFormationButton = ((Button) findViewById(R.id.confirm_formation));

        /**
         * load information
         */
        loadPlayers();

        /**
         * event
         */
        mConfirmFormationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] majorPlayersIds = new String[11];
                String[] positionsIds = new String[11];
                String[] secondaryPlayersIds = new String[getNumberOfSelectedSecondaryPlayers()];

                for (int i = 0; i < 11; i++) {
                    majorPlayersIds[i] = ((Player) mMajorPlayersSpinners.get(i).getSelectedItem()).getId();
                    positionsIds[i] = ((Position) mPositionsSpinners.get(i).getSelectedItem()).getId();
                    if (majorPlayersIds[i].isEmpty() || positionsIds[i].isEmpty()) {
                        Notifications.showAlertDialog(TeamFormationActivity.this, getString(R.string.error), getString(R.string.you_must_select_11_players_with_their_positions));
                        return;
                    }
                }

                int i = 0;
                for (Spinner s : mSecondaryPlayersSpinners) {
                    if (s.getSelectedItemPosition() == 0) continue;
                    secondaryPlayersIds[i++] = ((Player) s.getSelectedItem()).getId();
                }

                int selectedRowIdx = mMajorPlayersContainer.getSelectedRow();
                if (selectedRowIdx == -1) {
                    Notifications.showAlertDialog(TeamFormationActivity.this, getString(R.string.error), getString(R.string.you_must_select_a_captain));
                    return;
                }
                String captainId = ((Player) mMajorPlayersSpinners.get(selectedRowIdx).getSelectedItem()).getId();


                showProgress(true);
                MatchesApis.teamFormation(mMatchId, majorPlayersIds, positionsIds, secondaryPlayersIds, captainId, new ApiListeners.OnActionExecutedListener() {
                    @Override
                    public void onExecuted(Result result) {
                        if (result.isSucceeded()) {
                            Intent i = new Intent(TeamFormationActivity.this, MatchDetailsActivity.class);
                            i.putExtra(MatchDetailsActivity.MATCH_ID, mMatchId);
                            startActivity(i);
                            finish();
                        } else {
                            showProgress(false);
                            Notifications.showSnackBar(TeamFormationActivity.this, result.getMessages().get(0));
                        }
                    }
                });
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

    private void loadPlayers() {
        showProgress(true);
        PlayersApis.list(mTeamTypeId, new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    if (items.size() >= 11) {
                        mPlayers = items;
                        mPlayers.add(0, new Player("", getString(R.string.select_player)));
                        loadPositions();
                    } else {
                        Notifications.showAlertDialog(TeamFormationActivity.this, getString(R.string.error), getString(R.string.number_of_players_is_less_than_11)).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                    }
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(TeamFormationActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void loadPositions() {
        showProgress(true);
        MiscApis.positions(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    if (items.size() >= 11) {
                        mPositions = items;
                        mPositions.add(0, new Position("", getString(R.string.select_position)));
                        showProgress(false);
                        generateFields();
                    } else {
                        Notifications.showAlertDialog(TeamFormationActivity.this, getString(R.string.error), getString(R.string.number_of_positions_is_less_than_11)).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                    }
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(TeamFormationActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadPositions();
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

    private void generateFields() {
        ArrayAdapter playersAdapter = new ArrayAdapter<Player>(TeamFormationActivity.this, android.R.layout.simple_spinner_dropdown_item, mPlayers) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) return true;
                for (Spinner s : mMajorPlayersSpinners)
                    if (s.getSelectedItemPosition() == position)
                        return false;
                for (Spinner s : mSecondaryPlayersSpinners)
                    if (s.getSelectedItemPosition() == position)
                        return false;
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                if (isEnabled(position)) {
                    textView.setTextColor(ContextCompat.getColor(TeamFormationActivity.this, R.color.black));
                } else {
                    textView.setTextColor(ContextCompat.getColor(TeamFormationActivity.this, R.color.gray));
                }
                return textView;
            }
        };

        ArrayAdapter positionsAdapter = new ArrayAdapter<Position>(TeamFormationActivity.this, android.R.layout.simple_spinner_dropdown_item, mPositions) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) return true;
                for (Spinner s : mPositionsSpinners)
                    if (s.getSelectedItemPosition() == position)
                        return false;
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                if (isEnabled(position)) {
                    textView.setTextColor(ContextCompat.getColor(TeamFormationActivity.this, R.color.black));
                } else {
                    textView.setTextColor(ContextCompat.getColor(TeamFormationActivity.this, R.color.gray));
                }
                return textView;
            }
        };


        /**
         * Adding major players
         */
        for (int i = 0; i < 11; i++) {

            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


            /**
             * create the player spinner:
             */
            Spinner spinner;

            spinner = new Spinner(TeamFormationActivity.this);
            mMajorPlayersSpinners.add(spinner);
            spinner.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5));

            /**
             * add data
             */

            spinner.setAdapter(playersAdapter);

            /**
             * add player spinner to row
             */
            tr.addView(spinner);

            /**
             * create the position spinner:
             */
            spinner = new Spinner(TeamFormationActivity.this);
            mPositionsSpinners.add(spinner);
            spinner.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 5));

            /**
             * add data
             */

            spinner.setAdapter(positionsAdapter);

            /**
             * add player spinner to row
             */
            tr.addView(spinner);

            /**
             * Add the radio button
             */
            RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.addView(radioButton);


            /**
             * add row to table
             */
            mMajorPlayersContainer.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        /**
         * adding secondary players
         */
        for (int i = 0; i < mPlayers.size() - 11; i++) {
            Spinner spinner = new Spinner(this);
            mSecondaryPlayersSpinners.add(spinner);
            spinner.setAdapter(playersAdapter);
            spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mSecondaryPlayersContainer.addView(spinner);
        }
    }

    private int getNumberOfSelectedSecondaryPlayers() {
        int counter = 0;
        for (Spinner spinner : mSecondaryPlayersSpinners)
            if (spinner.getSelectedItemPosition() != 0)
                counter++;

        return counter;
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
