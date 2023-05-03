package com.example.sampleassignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private Button saveButton;
    private RadioGroup userGroup;
    private RadioButton radioText1;
    private RadioButton radioText2;
    private RadioButton radioText3;
    private LinearLayout optionalLayout;
    private Button startButton;
    private static final String EDIT_TEXT_1_KEY = "editText1";
    private static final String EDIT_TEXT_2_KEY = "editText2";
    private static final String EDIT_TEXT_3_KEY = "editText3";
    private static final String SELECTED_USER = "selectedUser";
    private static final String OPTIONAL_VISIBILITY = "optionalVisibility";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = findViewById(R.id.username1);
        editText2 = findViewById(R.id.username2);
        editText3 = findViewById(R.id.username3);
        saveButton = findViewById(R.id.save_button);
        userGroup = findViewById(R.id.userGroup);
        radioText1 = findViewById(R.id.radioButton);
        radioText2 = findViewById(R.id.radioButton2);
        radioText3 = findViewById(R.id.radioButton3);
        optionalLayout = findViewById(R.id.optionalUI);
        startButton = findViewById(R.id.start_button);

        if (savedInstanceState != null) {
            editText1.setText(savedInstanceState.getString(EDIT_TEXT_1_KEY));
            editText2.setText(savedInstanceState.getString(EDIT_TEXT_2_KEY));
            editText3.setText(savedInstanceState.getString(EDIT_TEXT_3_KEY));
            optionalLayout.setVisibility(savedInstanceState.getInt(OPTIONAL_VISIBILITY));
        }

        saveButton.setOnClickListener(v -> {
            String text1 = editText1.getText().toString();
            String text2 = editText2.getText().toString();
            String text3 = editText3.getText().toString();

            radioText1.setText(text1);
            radioText2.setText(text2);
            radioText3.setText(text3);

            optionalLayout.setVisibility(View.VISIBLE);

            // save text1, text2, and text3 to a database or file here

            Toast.makeText(MainActivity.this, "Usernames saved!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EDIT_TEXT_1_KEY, editText1.getText().toString());
        outState.putString(EDIT_TEXT_2_KEY, editText2.getText().toString());
        outState.putString(EDIT_TEXT_3_KEY, editText3.getText().toString());
        outState.putInt(SELECTED_USER, userGroup.getCheckedRadioButtonId());
        outState.putInt(OPTIONAL_VISIBILITY, optionalLayout.getVisibility());
    }
}
