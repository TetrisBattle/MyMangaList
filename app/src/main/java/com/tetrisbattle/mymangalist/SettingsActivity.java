package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final String[] pageNames = {
            "rankS",
            "rankA",
            "rankB",
            "rankC",
            "rankD",
            "rankE",
            "rankF",
            "special",
            "planToRead"
    };

    CheckBox publish;
    RadioGroup publishMode;
    RadioButton publicList, privateList;
    ConstraintLayout nameLayout;
    ConstraintLayout passwordLayout;
    EditText name, password;
    Button saveButton;

    SharedPreferences sharedPreferences;
    Animation animationAppear, animationDisappear;

    FirebaseDatabase db;
    DatabaseReference ref;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        publish = findViewById(R.id.publish);
        publishMode = findViewById(R.id.publishMode);
        publicList = findViewById(R.id.publicList);
        privateList = findViewById(R.id.privateList);
        nameLayout = findViewById(R.id.nameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        saveButton = findViewById(R.id.saveButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) currentUser = extras.getString("currentUser");
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        animationAppear = AnimationUtils.loadAnimation(this, R.anim.animation_appear);
        animationDisappear = AnimationUtils.loadAnimation(this, R.anim.animation_disappear);
        //publishMode.setAnimation(animationAppear);
        //passwordLayout.setAnimation(animationAppear);

        setupFromSharedPrefs();

        publish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                publishMode.setVisibility(View.VISIBLE);
                publishMode.startAnimation(animationAppear);

                nameLayout.startAnimation(animationAppear);
                nameLayout.setVisibility(View.VISIBLE);

                if (privateList.isChecked()) {
                    passwordLayout.startAnimation(animationAppear);
                    passwordLayout.setVisibility(View.VISIBLE);
                    password.setText("");
                }
            } else {
                publishMode.startAnimation(animationDisappear);
                publishMode.setVisibility(View.INVISIBLE);

                nameLayout.startAnimation(animationDisappear);
                nameLayout.setVisibility(View.INVISIBLE);

                if (privateList.isChecked()) {
                    passwordLayout.startAnimation(animationDisappear);
                    passwordLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        privateList.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordLayout.setVisibility(View.VISIBLE);
                passwordLayout.startAnimation(animationAppear);
                password.setText("");
            } else {
                passwordLayout.startAnimation(animationDisappear);
                passwordLayout.setVisibility(View.INVISIBLE);
            }
        });

        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("publish", publish.isChecked());
            editor.putBoolean("privateList", privateList.isChecked());
            if (privateList.isChecked()) editor.putString("password", String.valueOf(password.getText()));
            else editor.putString("password", "");

            if (publish.isChecked() && name.getText().toString().equals("")) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                editor.putString("name", String.valueOf(name.getText()));
                if (publicList.isChecked()) {
                    publishPublicList(String.valueOf(name.getText()));
                } else {
                    publishPrivateList(String.valueOf(name.getText()), String.valueOf(password.getText()));
                }
            }

            editor.apply();
        });
    }

    public void setupFromSharedPrefs() {
        String sharedPrefsName = sharedPreferences.getString("name", "");
        name.setText(sharedPrefsName);

        boolean sharedPrefsPublish = sharedPreferences.getBoolean("publish", false);

        if(sharedPrefsPublish) {
            publish.setChecked(true);
            publishMode.setVisibility(View.VISIBLE);
            nameLayout.setVisibility(View.VISIBLE);

            boolean sharedPrefsPrivateList = sharedPreferences.getBoolean("privateList", false);
            if (sharedPrefsPrivateList) {
                privateList.setChecked(true);
                passwordLayout.setVisibility(View.VISIBLE);

                String sharedPrefsPassword = sharedPreferences.getString("password", "");
                password.setText(sharedPrefsPassword);
            }
        }
    }

    public void publishPublicList(String listName) {
        ref = db.getReference("publishedMangaLists/publicLists/" + listName + "/owner");
        ref.setValue(currentUser);

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());

        List<ArrayList<MyManga>> myMangaListDb = myDatabaseHelper.getMyMangaListDb();
        for (int i=0; i<myMangaListDb.size(); i++) {
            List<MyManga> myMangaList = myMangaListDb.get(i);
            for (int j=0; j<myMangaList.size(); j++) {
                MyManga myManga = myMangaList.get(j);

                ref = db.getReference("publishedMangaLists/publicLists/" + listName +
                        "/myMangaList/" + pageNames[i] + "/" + myManga.name);

                ref.child("chapter").setValue(myManga.chapter);
                ref.child("url").setValue(myManga.url);
            }
        }
    }

    public void publishPrivateList(String listName, String password) {
        ref = db.getReference("publishedMangaLists/privateLists/" + listName + "/owner");
        ref.setValue(currentUser);

        ref = db.getReference("publishedMangaLists/privateLists/" + listName + "/password");
        ref.setValue(password);

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());

        List<ArrayList<MyManga>> myMangaListDb = myDatabaseHelper.getMyMangaListDb();
        for (int i=0; i<myMangaListDb.size(); i++) {
            List<MyManga> myMangaList = myMangaListDb.get(i);
            for (int j=0; j<myMangaList.size(); j++) {
                MyManga myManga = myMangaList.get(j);

                ref = db.getReference("publishedMangaLists/privateLists/" + listName +
                        "/myMangaList/" + pageNames[i] + "/" + myManga.name);

                ref.child("chapter").setValue(myManga.chapter);
                ref.child("url").setValue(myManga.url);
            }
        }
    }
}