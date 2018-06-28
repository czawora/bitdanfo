package com.bitdanfo.android.bitdanfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EndActivity extends AppCompatActivity {

    private static final String TAG = "EndActivity";


    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        Log.d(TAG, "here");

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){

            mUser = mAuth.getCurrentUser();

            Log.d(TAG, "uid: " + mUser.getUid());
            Log.d(TAG, "display_name: " + mUser.getDisplayName());

        }

    }
}
