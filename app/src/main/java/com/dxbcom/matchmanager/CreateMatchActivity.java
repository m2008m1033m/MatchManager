package com.dxbcom.matchmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dxbcom.matchmanager.apis.MatchesApis;
import com.dxbcom.matchmanager.apis.MiscApis;
import com.dxbcom.matchmanager.apis.Result;
import com.dxbcom.matchmanager.core.ApiListeners;
import com.dxbcom.matchmanager.models.Club;
import com.dxbcom.matchmanager.models.Model;
import com.dxbcom.matchmanager.models.Season;
import com.dxbcom.matchmanager.models.Tournament;
import com.dxbcom.matchmanager.models.Team;
import com.dxbcom.matchmanager.utils.Notifications;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mohammed on 6/29/16.
 */
public class CreateMatchActivity extends AppCompatActivity {

    private TextView mDate;
    private TextView mTime;
    private Spinner mSeasons;
    private Spinner mClubs;
    private Spinner mTournaments;
    private Spinner mTeams;
    private EditText mField;
    private CheckBox mHostedLocally;
    private Button mCreateMatchButton;

    private AlertDialog mProgressDialog;

    private final String dateFormat = "EEE, MMM dd, yyyy";
    private final String timeFormat = "hh:mm a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_match_activity);

        /**
         * action bar setup
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        setTitle(getString(R.string.create_new_match));

        /**
         * references
         */
        initReferences();

        /**
         * load spinners info
         */
        loadSeasons();

        /**
         * defaults:
         */
        mDate.setText(new SimpleDateFormat(dateFormat, Locale.US).format(Calendar.getInstance().getTime()));
        mTime.setText(new SimpleDateFormat(timeFormat, Locale.US).format(Calendar.getInstance().getTime()));

        /**
         * events
         */
        initEvents();
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
        mDate = ((TextView) findViewById(R.id.date));
        mTime = ((TextView) findViewById(R.id.time));
        mSeasons = ((Spinner) findViewById(R.id.season));
        mClubs = ((Spinner) findViewById(R.id.club));
        mTournaments = ((Spinner) findViewById(R.id.tournament));
        mTeams = ((Spinner) findViewById(R.id.team));
        mField = ((EditText) findViewById(R.id.field));
        mHostedLocally = ((CheckBox) findViewById(R.id.match_hosted_locally));
        mCreateMatchButton = ((Button) findViewById(R.id.create_match));
    }

    private void initEvents() {
        mDate.setOnClickListener(new View.OnClickListener() {
            private DatePickerDialog mDatePickerDialog;

            @Override
            public void onClick(View view) {
                if (mDatePickerDialog == null) {
                    final Calendar c = Calendar.getInstance();
                    mDatePickerDialog = new DatePickerDialog(CreateMatchActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, monthOfYear);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mDate.setText(new SimpleDateFormat(dateFormat, Locale.US).format(c.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    mDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
                }
                mDatePickerDialog.show();
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            private TimePickerDialog mTimePickerDialog;

            @Override
            public void onClick(View v) {
                if (mTimePickerDialog == null) {
                    final Calendar c = Calendar.getInstance();
                    mTimePickerDialog = new TimePickerDialog(CreateMatchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            c.set(Calendar.HOUR, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            mTime.setText(new SimpleDateFormat(timeFormat, Locale.US).format(c.getTime()));
                        }
                    }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false);
                }
                mTimePickerDialog.show();
            }
        });

        mCreateMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date date = new SimpleDateFormat(dateFormat, Locale.US).parse(mDate.getText().toString());
                    String time = mTime.getText().toString();
                    String season = ((Season) mSeasons.getSelectedItem()).getName();
                    String teamId = ((Club) mClubs.getSelectedItem()).getId();
                    String tournamentId = ((Tournament) mTournaments.getSelectedItem()).getId();
                    String typeId = ((Team) mTeams.getSelectedItem()).getId();
                    String field = mField.getText().toString().trim();
                    String hostedLocally = mHostedLocally.isChecked() ? "true" : "false";
                    if (field.isEmpty()) {
                        Notifications.showAlertDialog(CreateMatchActivity.this, getString(R.string.error), getString(R.string.the_field_cannot_be_empty));
                        return;
                    }
                    showProgress(true);
                    MatchesApis.create(date, time, season, teamId, tournamentId, typeId, field, hostedLocally, new ApiListeners.OnActionExecutedListener() {
                        @Override
                        public void onExecuted(Result result) {
                            Intent ii = new Intent(CreateMatchActivity.this, TeamFormationActivity.class);
                            ii.putExtra(TeamFormationActivity.MATCH_ID, "");
                            startActivity(ii);
                            finish();

                            if (true) return;

                            //TODO remove this crap above
                            if (result.isSucceeded()) {

                                String matchId = ((String) result.getExtra().get("matchId"));
                                if (matchId != null) {

                                    Intent i = new Intent(CreateMatchActivity.this, TeamFormationActivity.class);
                                    i.putExtra(TeamFormationActivity.MATCH_ID, matchId);
                                    i.putExtra(TeamFormationActivity.TEAM_TYPE_ID, ((Team) mClubs.getSelectedItem()).getId());
                                    startActivity(i);
                                    finish();

                                } else {
                                    showProgress(false);
                                    Notifications.showSnackBar(CreateMatchActivity.this, getString(R.string.unknonw_response_format));
                                }

                            } else {
                                showProgress(false);
                                Notifications.showSnackBar(CreateMatchActivity.this, result.getMessages().get(0));
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadSeasons() {
        showProgress(true);
        MiscApis.seasons(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateMatchActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSeasons.setAdapter(adapter);
                    loadClubs();
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(CreateMatchActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void loadClubs() {
        showProgress(true);
        MiscApis.clubs(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateMatchActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mClubs.setAdapter(adapter);
                    loadTournaments();
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(CreateMatchActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadClubs();
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
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateMatchActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mTournaments.setAdapter(adapter);
                    loadTeams();
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(CreateMatchActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void loadTeams() {
        showProgress(true);
        MiscApis.teams(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    ArrayAdapter adapter = new ArrayAdapter<>(CreateMatchActivity.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mTeams.setAdapter(adapter);
                    showProgress(false);
                } else {
                    showProgress(false);
                    Notifications.showYesNoDialog(CreateMatchActivity.this, getString(R.string.error), getString(R.string.failed_to_load_some_information), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void showProgress(boolean show) {
        if (show && mProgressDialog == null) {
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        } else if (!show && mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
