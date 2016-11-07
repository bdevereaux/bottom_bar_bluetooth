package com.blackboardtheory.bottombarapplication.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.blackboardtheory.bottombarapplication.MyApplication;
import com.blackboardtheory.bottombarapplication.R;

public class SignInActivity extends Activity implements View.OnClickListener, View.OnTouchListener{

    private MyApplication app;
    private LinearLayout loginButton, guestButton;
    private String id;
    private Intent mainIntent;
    private ImageView logo;
    private EditText login_id;

    private boolean mBooleanIsPressed;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        public void run() {
            checkGlobalVariable();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // set an enter transition
        getWindow().setEnterTransition(new DetailsTransition());
        setContentView(R.layout.activity_sign_in);

        app = MyApplication.getInstance();

        mainIntent = new Intent(this, SplashActivity.class);

        loginButton = (LinearLayout)findViewById(R.id.login_button);
        guestButton = (LinearLayout)findViewById(R.id.guest_button);
        logo = (ImageView)findViewById(R.id.my_logo);
        logo.setOnTouchListener(this);

        loginButton.setOnClickListener(this);
        guestButton.setOnClickListener(this);

        login_id = (EditText)findViewById(R.id.login_text);
        login_id.requestFocus();

        login_id.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.login_button:
                EditText loginText = (EditText)findViewById(R.id.login_text);
                id = loginText.getText().toString();
                if(id.equals(null) || id.length() < 1) {
                    Toast.makeText(this, "Please enter a valid login ID", Toast.LENGTH_SHORT).show();
                }
                else {
                    app.setUserID(id);

                    ActivityOptions options = ActivityOptions.
                            makeSceneTransitionAnimation(this, logo, "my_logo");
                    startActivity(mainIntent, options.toBundle());
                }
                break;
            case R.id.guest_button:
                app.setUserID("Guest");
                ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(this, logo, "my_logo");
                startActivity(mainIntent, options.toBundle());break;
        }
    }

    public class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).
                    addTransition(new ChangeTransform()).
                    addTransition(new ChangeImageTransform());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(v.getId()) {
            case R.id.my_logo:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Execute your Runnable after 3000 milliseconds = 3 seconds.
                    handler.postDelayed(runnable, 3000);
                    mBooleanIsPressed = true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mBooleanIsPressed) {
                        mBooleanIsPressed = false;
                        handler.removeCallbacks(runnable);
                    }
                }
                break;
        }
        return false;
    }

    private void checkGlobalVariable() {
        if(mBooleanIsPressed) {
            //Toast.makeText(this, "Pressed for 5 seconds", Toast.LENGTH_SHORT).show();
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.settings_dialog);
            dialog.setTitle("Developer Options");

            final EditText ip_text = (EditText)dialog.findViewById(R.id.ip_edit);
            ip_text.setText(MyApplication.myAddress);
            final EditText port_text = (EditText)dialog.findViewById(R.id.port_edit);
            port_text.setText(MyApplication.myPort);
            Button dialogButton = (Button)dialog.findViewById(R.id.dialogButtonOK);
            final RadioButton gogo = (RadioButton)dialog.findViewById(R.id.radioGogo);

            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //set our global variables here
                    MyApplication.myPort = port_text.getText().toString();
                    MyApplication.myAddress = ip_text.getText().toString();

                    MyApplication.jsonSource = (gogo.isChecked() ? MyApplication.externalSource : MyApplication.externalSource);
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }
}
