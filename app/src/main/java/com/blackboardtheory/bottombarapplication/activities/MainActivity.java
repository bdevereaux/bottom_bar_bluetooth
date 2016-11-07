package com.blackboardtheory.bottombarapplication.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.app.FragmentManager;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;

import android.view.View;

import android.view.WindowManager;
import android.widget.Toast;
import android.widget.Toolbar;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackboardtheory.bottombarapplication.MyApplication;
import com.blackboardtheory.bottombarapplication.R;
import com.blackboardtheory.bottombarapplication.fragments.FlightFragment;
import com.blackboardtheory.bottombarapplication.fragments.GamesFragment;
import com.blackboardtheory.bottombarapplication.fragments.MediaFragment;
import com.blackboardtheory.bottombarapplication.fragments.SettingsFragment;
import com.blackboardtheory.bottombarapplication.services.BluetoothService;

import static com.google.android.gms.internal.zzs.TAG;


public class MainActivity extends Activity implements FragmentManager.OnBackStackChangedListener,
        SheetLayout.OnFabAnimationEndListener, View.OnClickListener {


    private static final int REQUEST_CODE = 1;
    public static final int RC_BARCODE_CAPTURE = 9001;

    private FloatingActionButton fab;
    private SheetLayout sheet;

    private String mBlueToothMACAddress;
    private String mUUID;

    private ServiceConnection mServerConn;

    private BluetoothService.LocalBinder mBinder;
    private boolean mBound = false;

    private CoordinatorLayout coordinatorLayout;
    private FloatingActionMenu fam;

    private FragmentManager fm;

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.action_restroom:
                sheet.expandFab();
                break;
            case R.id.action_attendant:
                sendBluetoothCommand("call_attendant", null);
                break;
            case R.id.action_fan:
                sendBluetoothCommand("toggle_fan", null);
                break;
            case R.id.action_light:
                sendBluetoothCommand("toggle_light", null);
                break;
        }
    }

    private enum TabType {
        FLIGHT,
        GAMES,
        MEDIA,
        SETTINGS
    }

    private Fragment flightFragment, gamesFragment, mediaFragment, settingsFragment;

    private TabType activeTab;

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getFragmentManager().getBackStackEntryCount()>0;
        getActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public boolean onNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getFragmentManager().popBackStack();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.actionbar, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white, null), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return true;
        //return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getFragmentManager().addOnBackStackChangedListener(this);

        //Handle when activity is recreated like on orientation Change
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha, null);
        upArrow.setColorFilter(getResources().getColor(R.color.white, null), PorterDuff.Mode.SRC_ATOP);

        setContentView(R.layout.activity_main);

        fam = (FloatingActionMenu) findViewById(R.id.cabin_actions_down);

        fam.getMenuIconView().setRotation(180);

        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                //fam.toggleMenu(true);
                if(!opened) {
                    //fam.setRotation(180);
                    fam.getMenuIconView().setRotation(180);
                }
            }
        });

        fam.setVisibility(MyApplication.getInstance().isConnected() ? View.VISIBLE : View.INVISIBLE);

        fab = (FloatingActionButton) findViewById(R.id.action_restroom);
        FloatingActionButton attendant = (FloatingActionButton) findViewById(R.id.action_attendant);
        FloatingActionButton light = (FloatingActionButton) findViewById(R.id.action_light);
        FloatingActionButton fan = (FloatingActionButton) findViewById(R.id.action_fan);

        sheet = (SheetLayout) findViewById(R.id.bottom_sheet);
        sheet.setFab(fab);
        sheet.setFabAnimationEndListener(this);

        // Set our click listeners
        fab.setOnClickListener(this);
        attendant.setOnClickListener(this);
        light.setOnClickListener(this);
        fan.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        getActionBar().setHomeAsUpIndicator(upArrow);
        shouldDisplayHomeUp();

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.main_activity);

        this.setupBottomBar(savedInstanceState);

        activeTab = TabType.SETTINGS;
        fm = getFragmentManager();

        if (savedInstanceState == null) {
            flightFragment = new FlightFragment();
            gamesFragment = new GamesFragment();
            mediaFragment = new MediaFragment();
            settingsFragment = new SettingsFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.view_container, settingsFragment).commit();
        }
    }

    @Override
    public void onFabAnimationEnd() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE){
            sheet.contractFab();
        }
        else if(requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    if (isBlueToothMACAddressAndUUID(barcode.displayValue)){
                        Log.d(TAG, "Barcode read: " + barcode.displayValue);

                        //Call BluetoothClientActivity
                        Intent connectIntent = new Intent(this,BluetoothService.class);
                        connectIntent.putExtra("MAC",mBlueToothMACAddress);
                        connectIntent.putExtra("UUID",mUUID);
                        startService(connectIntent);

                        mServerConn = new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder binder) {
                                mBinder = (BluetoothService.LocalBinder)binder;
                                Log.d("App", "onServiceConnected");
                                ((SettingsFragment)settingsFragment).setDisconnectText();
                                Toast.makeText(MainActivity.this, "Pairing Completed!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {
                                Log.d("App", "onServiceDisconnected");
                            }
                        };
                        mBound = bindService(connectIntent, mServerConn, Context.BIND_AUTO_CREATE);
                        toggleFam(true);


                    }
                    else{
                        Log.d(TAG, "Error: Bluetooth MAC Address or UUID not found");
                    }
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupBottomBar(Bundle savedInstanceState) {
        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);

        bottomBar.setActiveTabColor(getColor(R.color.white));
        bottomBar.setTextAppearance(R.style.BottomTabTextAppearance);
        bottomBar.setTypeFace("fonts/kohinoor_bangla.ttc");
        bottomBar.setDefaultTabPosition(3);



        bottomBar.setItemsFromMenu(R.menu.bottom_tabs, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {
                FragmentTransaction transaction = fm.beginTransaction();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                transaction.setCustomAnimations(R.animator.fade_out, R.animator.fade_in);
                switch (itemId) {
                    case R.id.flight_info_item:
                        if(activeTab != TabType.FLIGHT) {
                            activeTab = TabType.FLIGHT;
                            transaction.replace(R.id.view_container, flightFragment);
                        }
                        Snackbar.make(coordinatorLayout, "Tab 1 Selected", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.games_item:
                        if(activeTab != TabType.GAMES) {
                            activeTab = TabType.GAMES;
                            transaction.replace(R.id.view_container, gamesFragment);
                        }
                        Snackbar.make(coordinatorLayout, "Tab 2 Selected", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.entertainment_item:
                        if(activeTab != TabType.MEDIA) {
                            activeTab = TabType.MEDIA;
                            transaction.replace(R.id.view_container, mediaFragment);
                        }
                        Snackbar.make(coordinatorLayout, "Tab 3 Selected", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.settings_item:
                        if(activeTab != TabType.SETTINGS) {
                            activeTab = TabType.SETTINGS;
                            transaction.replace(R.id.view_container, settingsFragment);
                        }
                        Snackbar.make(coordinatorLayout, "Tab 4 Selected", Snackbar.LENGTH_LONG).show();
                        break;
                }
                transaction.commit();
            }
        });
    }

    private Boolean isBlueToothMACAddressAndUUID(String inputStr){
        Boolean returnValue = false;

        Pattern pMACAddress = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
        Pattern pUUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

        Matcher mmMacAddress = pMACAddress.matcher(inputStr);
        Matcher mmUUID = pUUID.matcher(inputStr);

        if (mmMacAddress.find() && mmUUID.find()){
            mBlueToothMACAddress = mmMacAddress.group().toString();
            mUUID = mmUUID.group().toString();

            returnValue = true;
        }

        return returnValue;
    }

    public void endBluetoothConnection() {
        if(mBound) {
            unbindService(mServerConn);
            mBinder.getService().onDestroy();
            mBound = false;
            toggleFam(false);
        }
    }

    public void toggleFam(Boolean visible) {
        fam.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void sendBluetoothCommand(String command, String id) {
        Bundle message = new Bundle();
        message.putString("message",command);
        if(id != null) {
            message.putString("movieID", id);
        }
        Message handlerMessage = new Message();
        handlerMessage.setData(message);
        MyApplication.getInstance().getHandler().sendMessage(handlerMessage);
    }

}
