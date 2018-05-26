package com.bitdanfo.android.bitdanfo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_READ_PHONE_STATE = 1;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "get firebase instance");
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "set login button listener");
        Button clickButton = findViewById(R.id.login_button);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                attemptLogin();
            }
        });
    }


    public void login(){

        Log.d(TAG, "login: get deviceID");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();

        if (deviceId == null){
            Toast.makeText(LoginActivity.this, "Retrieval of device ID failed", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "deviceID retrieved: " + deviceId);


    }

    public void attemptLogin(){

        Log.d(TAG, "attemptLogin: check for READ_PHONE_STATE permissions");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // ask for permission

            Log.d(TAG, "attemptLogin: requesting READ_PHONE_STATE permissions");

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        } else {

            login();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: READ_PHONE_STATE permissions received");

                    login();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
