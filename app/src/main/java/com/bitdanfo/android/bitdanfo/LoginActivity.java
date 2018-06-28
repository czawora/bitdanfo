package com.bitdanfo.android.bitdanfo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int REQUEST_INTERNET = 2;
    private static final int REQUEST_LOCATION = 3;


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String login_url = "http://bitdonfo7463.herokuapp.com";

    private RequestQueue mRequestQueue;

    private boolean tokenResponseReceived = true;
    private byte[] mToken;
    private String mHeader_Info = "";
    private String mHeader_User = "";


    //subclass of Volley Request to get http response header info
    class TokenRequest extends Request<byte[]> {

        private Response.Listener<String> mListener;

        public TokenRequest(String url, Response.Listener<String> listener, Response.ErrorListener elistener) {

            super(Request.Method.GET, url, elistener);
            mListener = listener;
        }

        @Override
        protected Response<byte[]> parseNetworkResponse( NetworkResponse response ){

            tokenResponseReceived = true;

            //check status code
            if (response.statusCode == 200){

                mHeader_User = response.headers.get("User");
                Log.d("mHeader_User " + mHeader_User, TAG);
            }

            mHeader_Info = response.headers.get("Info");
            Log.d("mHeader_Info " + mHeader_Info, TAG);

            mToken = response.data;
            Log.d("token length" + mToken.length, TAG);

            return Response.success(response.data, null);
        }

        @Override
        protected void deliverResponse( byte[] response ){

            //token is many bytes long
            if ( response.length > 10 ) {
                mListener.onResponse("good_response");
            }
        }
    }


    protected void sendTokenRequest( String deviceId ){

        mRequestQueue.cancelAll(TAG);

        //ensures only 1 token request is sent at a time
      //  if ( tokenResponseReceived ) {

            String requestUrl = login_url + "/+" + deviceId;
            Log.d(requestUrl, TAG);

            TokenRequest tokenRequest = new TokenRequest(requestUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            completeLogin();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

            tokenRequest.setTag(TAG);

            Log.d("call sendTokenRequest", TAG);
            mRequestQueue.add(tokenRequest);
            tokenResponseReceived = false;

        //}
    }

    protected void completeLogin(){

        Log.d("in completeLogin", TAG);

        String tokenString = new String(mToken);
        Log.v(TAG, tokenString);
        mAuth.signInWithCustomToken(tokenString).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCustomToken:success");

                    nextActivity();
                } else {

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());

                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_login);

        Log.d(TAG, "get firebase instance");
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "get newRequestQueue");
        mRequestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, "set login button listener");
        Button clickButton = findViewById(R.id.login_button);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    public void login(){

        Log.d(TAG, "login: get deviceID");

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getImei();

        if (deviceId == null){
            Toast.makeText(LoginActivity.this, "Retrieval of device ID failed", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "deviceID retrieved: " + deviceId);
        sendTokenRequest(deviceId);

    }

    public void attemptLogin(){

        boolean has_phoneStatePermission = false;
        boolean has_internetPermission = false;
        boolean has_locationPermisssion = false;

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

            has_phoneStatePermission = true;
            Log.d(TAG, "attemptLogin: READ_PHONE_STATE permissions already granted");

        }

        Log.d(TAG, "attemptLogin: check for INTERNET permissions");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // ask for permission

            Log.d(TAG, "attemptLogin: requesting INTERNET permissions");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_INTERNET);
        } else {

            has_internetPermission = true;
            Log.d(TAG, "attemptLogin: INTERNET permissions already granted");

        }

        Log.d(TAG, "attemptLogin: check for LOCATION permissions");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // ask for permission

            Log.d(TAG, "attemptLogin: requesting LOCATION permissions");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {

            has_locationPermisssion = true;
            Log.d(TAG, "attemptLogin: LOCATION permissions already granted");

        }

        //once both permissions are granted, then login
        if (has_internetPermission && has_phoneStatePermission && has_locationPermisssion) {

            if (mUser == null) {
                login();
            }else{
                nextActivity();
            }
        }

    }

    public void nextActivity(){

        mUser = mAuth.getCurrentUser();

        if (mUser == null){
            Log.d(TAG, "mUser is null");
        }

        Log.d(TAG, "nextActivity");
        if (mHeader_Info.equals("good_token")){

            Log.d(TAG, "nextActivity_good_token");
            Log.d(TAG, "mUser.getDisplayName: " + mUser.getDisplayName());

            if (mUser.getDisplayName().equals("-----")){

                Intent intent = new Intent(this, InfoUpdateActivity.class);
                startActivity(intent);

            }else{

                Intent intent = new Intent(this, EndActivity.class);
                startActivity(intent);

            }


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


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            case REQUEST_INTERNET: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: INTERNET permissions received");


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult: LOCATION permissions received");


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
