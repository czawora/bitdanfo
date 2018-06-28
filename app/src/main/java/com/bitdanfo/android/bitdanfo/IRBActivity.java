package com.bitdanfo.android.bitdanfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;

public class IRBActivity extends AppCompatActivity {

    String mDisplayName;
    Boolean mDriver;

    CheckBox mIRBChk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irb);

        FileInputStream serviceAccount = new FileInputStream("app/bitdanfo-firebase-adminsdk-2pw68-50f9fcee1b.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://bitdanfo.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        Intent intent = getIntent();
        mDisplayName = intent.getStringExtra("displayName");
        mDriver = intent.getBooleanExtra("boolDriver", true);

        mIRBChk = findViewById(R.id.chkIRB);
        mIRBChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){

                    //edit display name in firebase

                    //add driver boolean to firebase

                    //call final activity
                    Intent intent = new Intent( IRBActivity.this, EndActivity.class);
                    startActivity(intent);

                }
            }
        });

    }
}
