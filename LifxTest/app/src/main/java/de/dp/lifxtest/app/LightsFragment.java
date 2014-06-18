package de.dp.lifxtest.app;

/**
 * Created by dennispriess on 04/06/14.
 */

import com.gcx.lifxtest.app.R;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import lifx.java.android.client.LFXClient;
import lifx.java.android.entities.LFXTypes;
import lifx.java.android.network_context.LFXNetworkContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class LightsFragment extends Fragment implements View.OnClickListener {

    private Button mLightButton, mSetCountdownButton;

    private EditText mCountdownEditText;

    private TextView mTimeLeftText;

    private boolean isTimerFinished = true;

    private LFXNetworkContext mLocalNetworkContext;

    private CountDownTimer mCountDownTimer;

    public LightsFragment() {
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            // turn all lights off
            case R.id.button:
                createNotification();
                if (mLocalNetworkContext.getAllLightsCollection().getFuzzyPowerState()
                        == LFXTypes.LFXFuzzyPowerState.OFF) {
                    mLocalNetworkContext.getAllLightsCollection()
                            .setPowerState(LFXTypes.LFXPowerState.ON);

                } else {
                    mLocalNetworkContext.getAllLightsCollection()
                            .setPowerState(LFXTypes.LFXPowerState.OFF);
                }
                break;

            case R.id.countdown_button:

                int minutes = Integer.parseInt(mCountdownEditText.getEditableText().toString());

                int minutesInMillis = minutes * 1000 * 60;

                if (isTimerFinished) {
                    startCountdown(minutesInMillis);
                } else {
                    mCountDownTimer.cancel();
                    startCountdown(minutesInMillis);
                }

                break;
        }
    }

    private void startCountdown(final int millisMinutes) {
        mCountDownTimer = new CountDownTimer(millisMinutes, 1000) {

            public void onTick(long millisUntilFinished) {
                isTimerFinished = false;
                mTimeLeftText
                        .setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                isTimerFinished = true;
                mTimeLeftText.setText("done!");
                mLocalNetworkContext.getAllLightsCollection()
                        .setPowerState(LFXTypes.LFXPowerState.OFF);
            }
        }.start();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalNetworkContext = LFXClient.getSharedInstance(getActivity())
                .getLocalNetworkContext();
        mLocalNetworkContext.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        mLightButton = (Button) rootView.findViewById(R.id.button);
        mCountdownEditText = (EditText) rootView.findViewById(R.id.countdown_setter);
        mSetCountdownButton = (Button) rootView.findViewById(R.id.countdown_button);
        mTimeLeftText = (TextView) rootView.findViewById(R.id.countdown_left);

        mSetCountdownButton.setOnClickListener(this);
        mLightButton.setOnClickListener(this);
        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification() {

        // prepare intent which is triggered if the
// notification is selected

        Intent intent = new Intent(getActivity(), MasterActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(getActivity())
                .setContentTitle("Control your lifx")
                .setContentText("turn light on / off")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(false)
                .addAction(R.drawable.ic_launcher, "On", pIntent)
                .addAction(R.drawable.ic_launcher, "Off", pIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalNetworkContext.disconnect();
    }
}