package com.blackboardtheory.bottombarapplication.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackboardtheory.bottombarapplication.activities.BarcodeCaptureActivity;
import com.blackboardtheory.bottombarapplication.activities.MainActivity;
import com.blackboardtheory.bottombarapplication.MyApplication;
import com.blackboardtheory.bottombarapplication.R;
import com.blackboardtheory.bottombarapplication.activities.SignInActivity;


public class SettingsFragment extends Fragment implements View.OnClickListener{

    private View view;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle("Settings");
        View view = inflater.inflate(R.layout.view_settings, container, false);
        LinearLayout connect = (LinearLayout)view.findViewById(R.id.connect_button);
        LinearLayout account = (LinearLayout)view.findViewById(R.id.skymiles_login_button);
        connect.setOnClickListener(this);
        account.setOnClickListener(this);

        if(!"Guest".equals(MyApplication.getInstance().getUserID())) {
            TextView top = (TextView)account.findViewById(R.id.account_top);
            TextView bottom = (TextView)account.findViewById(R.id.account_bottom);
            top.setText("SIGN OUT");
            bottom.setText("of your account");
        }

        if(MyApplication.getInstance().isConnected()) {
            TextView top = (TextView)connect.findViewById(R.id.connect_top);
            TextView bottom = (TextView)connect.findViewById(R.id.connect_bottom);
            top.setText("DISCONNECT YOUR DEVICE");
            bottom.setText("from active connection");
        }
        else {
            TextView top = (TextView)connect.findViewById(R.id.connect_top);
            TextView bottom = (TextView)connect.findViewById(R.id.connect_bottom);
            top.setText("CONNECT YOUR DEVICE");
            bottom.setText("via bluetooth");
        }
        this.view = view;

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.connect_button:
                if(!MyApplication.getInstance().isConnected()) {//not connected
                    Intent intent = new Intent(SettingsFragment.this.getActivity(), BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                    getActivity().startActivityForResult(intent, MainActivity.RC_BARCODE_CAPTURE);
                }
                else {//connected
                    MyApplication.getInstance().setConnected(false);
                    TextView top = (TextView)view.findViewById(R.id.connect_top);
                    TextView bottom = (TextView)view.findViewById(R.id.connect_bottom);
                    top.setText("CONNECT YOUR DEVICE");
                    bottom.setText("via bluetooth");
                    ((MainActivity)getActivity()).endBluetoothConnection();
                }
                break;
            case R.id.skymiles_login_button:
                ((MainActivity)getActivity()).endBluetoothConnection();
                Intent intent = new Intent(SettingsFragment.this.getActivity(), SignInActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    public void setDisconnectText() {
        TextView top = (TextView)view.findViewById(R.id.connect_top);
        TextView bottom = (TextView)view.findViewById(R.id.connect_bottom);
        top.setText("DISCONNECT YOUR DEVICE");
        bottom.setText("from active connection");
    }

}
