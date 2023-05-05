package com.example.sampleassignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StartActivity extends AppCompatActivity {

    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private Button saveButton;
    private RadioGroup userGroup;
    private RadioButton radioText1;
    private RadioButton radioText2;
    private RadioButton radioText3;
    private LinearLayout optionalLayout;
    private String selecteduser;
    private Button startButton;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
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

        dbHelper = new DatabaseHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_NAME,
                DatabaseHelper.projection,
                null,
                null,
                null,
                null,
                null
        );
        ArrayList<String> usernames = new ArrayList<>();
        while (cursor.moveToNext()) {
            usernames.add(cursor.getString(cursor.getColumnIndexOrThrow("username1")));
            usernames.add(cursor.getString(cursor.getColumnIndexOrThrow("username2")));
            usernames.add(cursor.getString(cursor.getColumnIndexOrThrow("username3")));
        }
        cursor.close();
        db.close();

        if (usernames.size() >= 3) {
            editText1.setText(usernames.get(0));
            editText2.setText(usernames.get(1));
            editText3.setText(usernames.get(2));
        }

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

            if (text1.isEmpty() || text2.isEmpty() || text3.isEmpty()) {
                return;
            }

            radioText1.setText(text1);
            radioText2.setText(text2);
            radioText3.setText(text3);

            optionalLayout.setVisibility(View.VISIBLE);

            db = dbHelper.getWritableDatabase();
            dbHelper.rebuild(db);
            ContentValues values = new ContentValues();
            values.put("username1", text1);
            values.put("username2", text2);
            values.put("username3", text3);
            long result = db.insert(DatabaseHelper.TABLE_NAME, null, values);

            if (result == -1) {
                Toast.makeText(StartActivity.this, "ERROR: Failed to save", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StartActivity.this, "Usernames saved!", Toast.LENGTH_SHORT).show();
            }
            db.close();
        });

        startButton.setOnClickListener(v -> {
            String text1 = radioText1.getText().toString();
            String text2 = radioText2.getText().toString();
            String text3 = radioText3.getText().toString();
            String edit1 = editText1.getText().toString();
            String edit2 = editText2.getText().toString();
            String edit3 = editText3.getText().toString();

            if ((text1.isEmpty() || text2.isEmpty() || text3.isEmpty()) || ((edit1.isEmpty() || edit2.isEmpty() || edit3.isEmpty()))) {
                return;
            }

            RadioButton selected = findViewById(userGroup.getCheckedRadioButtonId());
            if (selected != null) {
                String[] arr = {text1, text2, text3};
                Set<String> users = new HashSet<>(Arrays.asList(arr));
                users.remove(selected.getText().toString());
                String[] filtered = users.toArray(new String[0]);
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putExtra("selectedUser", selected.getText().toString());
                intent.putExtra("otherUser1", filtered[0]);
                intent.putExtra("otherUser2", filtered[1]);
                startActivity(intent);
            }
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
