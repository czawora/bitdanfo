package com.bitdanfo.android.bitdanfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class InfoUpdateActivity extends AppCompatActivity {

    String mDisplayName = "";
    Button mSubmitButton;
    EditText mDisplayNameText;
    RadioGroup mRadioGroup;
    RadioButton mRadioButton;
    Boolean mdriver = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infoupdate);

        mDisplayNameText = findViewById(R.id.input_display_name);
        mSubmitButton = findViewById(R.id.infoUpdateButton);
        mRadioGroup = findViewById(R.id.radioGroup);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String displayNameText = mDisplayNameText.getText().toString();

                if (displayNameText.isEmpty()){

                    Toast toast = Toast.makeText(getApplicationContext(), "Enter a display name", Toast.LENGTH_LONG);
                    toast.show();

                } else{

                    mDisplayName = displayNameText;

                    int selectedId = mRadioGroup.getCheckedRadioButtonId();
                    mRadioButton = findViewById(selectedId);

                    String radioString = mRadioButton.getText().toString();

                    if (radioString.equals(getString(R.string.radioDriver))){

                        mdriver = true;

                    }else{

                        mdriver = false;

                    }

                    Intent intent = new Intent(InfoUpdateActivity.this, IRBActivity.class);
                    intent.putExtra("displayName", mDisplayName);
                    intent.putExtra("boolDriver", mdriver);
                    startActivity(intent);

                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (!mDisplayName.isEmpty()){

            mDisplayNameText.setText(mDisplayName);
        }else{

            mDisplayNameText.getText().clear();
        }
    }
}
