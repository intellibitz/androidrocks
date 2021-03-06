package com.androidworks.navsys.wuffit.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.util.Log;
import com.androidworks.navsys.wuffit.R;
import com.androidworks.navsys.wuffit.content.Tracker;
import com.androidworks.navsys.wuffit.service.SMSHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SetupWuffIT extends ListActivity {
    private TextView msgView;
    private EditText editmsgView;
    private View setupView;
    private TextView footerCountText;

    private boolean isBound;
    private SMSHandler smsHandler;

    private ServiceConnection smsHandlerConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            smsHandler = ((SMSHandler.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            smsHandler = null;
        }
    };
    private static final int SETUP_ALL = 100;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_wuffit);

// get your phone number
        TelephonyManager telephonyManager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        String tel = telephonyManager.getLine1Number();

        Log.i("SetupWuffIT: VoiceMail Number ==> ", telephonyManager.getVoiceMailNumber()+" <==");

        msgView = (TextView) findViewById(R.id.wuffit_message_text);
        editmsgView = (EditText) findViewById(R.id.wuffit_message_edittext);

        String msg = msgView.getText().toString();
        if (tel != null) {
            msgView.setText(msg.replaceFirst("@", tel));
        }
        editmsgView.setText(msgView.getText());

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                findViewById(R.id.wuffit_message_edit).setVisibility(View.VISIBLE);
                findViewById(R.id.wuffit_message).setVisibility(View.GONE);
            }
        });
        final ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                findViewById(R.id.wuffit_message_edit).setVisibility(View.GONE);
                findViewById(R.id.wuffit_message).setVisibility(View.VISIBLE);
            }
        });

        editmsgView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    msgView.setText(editmsgView.getText());
                }
                return false;
            }
        });
        editmsgView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    save.performClick();
                }
            }
        });

        setupView = findViewById(R.id.wuffit_setup);
        setupView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog(SETUP_ALL);
            }
        });
        findViewById(R.id.main_menu).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
// Go to select trackers screen
                Intent intent = new Intent("com.androidworks.navsys.wuffit.ACTION_MAIN_MENU");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivity(intent);
            }
        });
        View footer = ((LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view, null);
        footerCountText = (TextView) footer.findViewById(R.id.text2);
        getListView().addFooterView(footer);

        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(SetupWuffIT.this,
                SMSHandler.class), smsHandlerConnection, Context.BIND_AUTO_CREATE);
        isBound = true;

    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTrackersOnView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(smsHandlerConnection);
            isBound = false;
        }
    }

    @Override
    protected Dialog onCreateDialog(int i) {
        switch (i) {
            case SETUP_ALL:
                return new AlertDialog.Builder(this)
                        .setTitle("Setup all Trackers")
                        .setMessage("This will send SMS to all available trackers. Do you want to continue?")
                        .setCancelable(true)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                smsHandler.setupWuffIT(msgView.getText().toString());
                                updateTrackersOnView();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
// cancel                               
                            }
                        })
                        .create();
        }
        return super.onCreateDialog(i);
    }

    private void updateTrackersOnView() {
        Cursor cursor = managedQuery(Tracker.Details.CONTENT_URI,
                new String[]{Tracker.Details._ID, Tracker.Details.NAME,
                        Tracker.Details.NUMBER}, null, null, null);
        int sz = cursor.getCount();
        footerCountText.setText("(" + sz + ")");
        setupView.setEnabled(sz > 0);

        Cursor setupCursor = managedQuery(Tracker.Setup.CONTENT_URI,
                new String[]{Tracker.Setup._FID, Tracker.Setup.SETUP_TIME, Tracker.Setup.REPLY_TIME},
                null, null, null);

        TrackerAdapter trackerAdapter = new TrackerAdapter
                (this, R.layout.setup_tracker_item, cursor,
                        new String[]{Tracker.Details.NAME, Tracker.Details.NUMBER, Tracker.Details.NAME, Tracker.Details.NUMBER},
                        new int[]{android.R.id.text1, android.R.id.text2, R.id.text3, R.id.text4});
        trackerAdapter.setSetupCursor(setupCursor);
        getListView().setAdapter(trackerAdapter);
    }

    class TrackerAdapter extends SimpleCursorAdapter {

        private Cursor setupCursor;

        public TrackerAdapter(Context context, int i, Cursor cursor, String[] strings, int[] ints) {
            super(context, i, cursor, strings, ints);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = super.getView(i, view, viewGroup);
            View ib = v.findViewById(R.id.setup);
            ib.setTag(getCursor().getString(2) + ":" + getCursor().getString(0));
            ib.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String[] s = ((String) view.getTag()).split(":");
                    smsHandler.setupWuffIT
                            (msgView.getText().toString(),
                                    s[0], s[1]);
                    updateTrackersOnView();
                }
            });
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            TextView setup = (TextView) view.findViewById(R.id.text3);
            TextView reply = (TextView) view.findViewById(R.id.text4);
            setupCursor.moveToPosition(cursor.getPosition());
            long rt = setupCursor.getLong(1);
            if (rt > 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(rt);
                DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
                setup.setText("setup on: " + sdf.format(cal.getTime()));
            } else {
                setup.setText("setup on: <not available>");
            }
            rt = setupCursor.getLong(2);
            if (rt > 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(rt);
                DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
                reply.setText("reply on:" + sdf.format(cal.getTime()));
                ((ImageView) view.findViewById(R.id.status))
                        .setImageResource(android.R.drawable.presence_online);
            } else {
                reply.setText("reply on: <not available>");
            }
        }

        public void setSetupCursor(Cursor setupCursor) {
            this.setupCursor = setupCursor;
        }

    }

}