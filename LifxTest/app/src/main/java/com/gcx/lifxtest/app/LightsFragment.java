package com.gcx.lifxtest.app;

/**
 * Created by dennispriess on 04/06/14.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lifx.java.android.client.LFXClient;
import lifx.java.android.entities.LFXTypes;
import lifx.java.android.network_context.LFXNetworkContext;

/**
 * A placeholder fragment containing a simple view.
 */
public class LightsFragment extends Fragment implements View.OnClickListener {

    private Button mLightButton;

    private LFXNetworkContext mLocalNetworkContext;

    private boolean turnOn = true;

    public LightsFragment() {
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            // turn all lights off
            case R.id.button:

                if (turnOn) {
                    mLocalNetworkContext.getAllLightsCollection()
                            .setPowerState(LFXTypes.LFXPowerState.ON);
                    turnOn = false;
                } else {
                    mLocalNetworkContext.getAllLightsCollection()
                            .setPowerState(LFXTypes.LFXPowerState.OFF);
                    turnOn = true;
                }
                break;
        }
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
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocalNetworkContext.disconnect();
    }
}