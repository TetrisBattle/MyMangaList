package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    //region variables
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

    private static final int[] pageIds = {
            R.id.rankButtonS,
            R.id.rankButtonA,
            R.id.rankButtonB,
            R.id.rankButtonC,
            R.id.rankButtonD,
            R.id.rankButtonE,
            R.id.rankButtonF,
            R.id.specialButton,
            R.id.planToReadButton
    };

    private List<Button> rankButtons;
    ConstraintLayout background;
    ImageButton settingsIcon;

    PopupMenu settingsIconPopupMenu;
    SharedPreferences sharedPreferences;

    String currentUser;
    int activePage = 0;

//    int testCounter = 0;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rankButtons = new ArrayList<>(pageIds.length);
        background = findViewById(R.id.background);
        settingsIcon = findViewById(R.id.settingsIcon);

        settingsIconPopupMenu = new PopupMenu(this, settingsIcon);
        settingsIconPopupMenu.getMenuInflater().inflate(R.menu.popup_settings, settingsIconPopupMenu.getMenu());
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        login();
        setupButtons();
        setupSettings();
        setupFromSharedPrefs();

//        replaceListFragment(new ListFragment(background, pageNames[activePage]));
        replaceFragment(new ListFragment(background, pageNames[activePage]));

        Log.d("myTest", "test: " + "main");
    }

    @Override
    protected void onStop() {
        super.onStop();
        background.requestFocus();
    }

    public void backgroundClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // hide keyboard
        getCurrentFocus().clearFocus();
        //Log.d("myTest", "test: " + ++testCounter);
    }

    public void login() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentUser = String.valueOf(firebaseAuth.getCurrentUser());
                    Log.d("myTest", "new user: " + currentUser);
                } else {
                    Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            currentUser = firebaseUser.getUid();
            //Log.d("myTest", "already signed in: " + currentUser);
            //firebaseAuth.signOut();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void setupButtons() {
        for (int i = 0; i< pageIds.length; i++) {
            Button rankButton = findViewById(pageIds[i]);
            int finalI = i;
            rankButton.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentPage", finalI);
                editor.apply();

                background.requestFocus();
                rankButtons.get(activePage).setBackgroundColor(getResources().getColor(R.color.colorRankButton, null));
                rankButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));

                activePage = finalI;
                replaceFragment(new ListFragment(background, pageNames[activePage]));
            });
            rankButtons.add(rankButton);
        }
    }

    public void setupSettings() {
        settingsIcon.setOnClickListener(v -> {
            settingsIconPopupMenu.show();

            settingsIconPopupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.popupPlanToRead) {
                    item.setChecked(!item.isChecked());

                    if (item.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("showPlanToReadPage", true);
                        editor.apply();
                        rankButtons.get(rankButtons.size()-1).setVisibility(View.VISIBLE);
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("showPlanToReadPage", false);
                        editor.apply();
                        rankButtons.get(rankButtons.size()-1).setVisibility(View.GONE);

                        if (activePage == rankButtons.size()-1) {
                            rankButtons.get(0).callOnClick();
                        }
                    }

                    // prevent popup menu from closing
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                    item.setActionView(new View(this));
                    return false;
                } else if (item.getItemId() == R.id.popupSpecial) {
                    item.setChecked(!item.isChecked());

                    if (item.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("showSpecialPage", true);
                        editor.apply();
                        rankButtons.get(rankButtons.size()-2).setVisibility(View.VISIBLE);
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("showSpecialPage", false);
                        editor.apply();
                        rankButtons.get(rankButtons.size()-2).setVisibility(View.GONE);

                        if (activePage == rankButtons.size()-2) {
                            rankButtons.get(0).callOnClick();
                        }
                    }

                    // prevent popup menu from closing
                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                    item.setActionView(new View(this));
                    return false;
                } else if (item.getItemId() == R.id.popupPublish) {
//                    Intent intent = new Intent(this, PublishActivity.class);
//                    intent.putExtra("currentUser", currentUser);
//                    startActivity(intent);
                    rankButtons.get(activePage).setBackgroundColor(getResources().getColor(R.color.colorRankButton, null));
                    replaceFragment(new PublishFragment(currentUser));
                    return true;
                } else if (item.getItemId() == R.id.popupImport) {
                    Toast.makeText(this, "import: empty", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            });
        });
    }

    public void setupFromSharedPrefs() {
//        sharedPreferences.getStringSet()

        boolean showPlanToReadPage = sharedPreferences.getBoolean("showPlanToReadPage", false);
        boolean showSpecialPage = sharedPreferences.getBoolean("showSpecialPage", false);
        int currentPage = sharedPreferences.getInt("currentPage", 0);

        if (showPlanToReadPage) {
            rankButtons.get(rankButtons.size()-1).setVisibility(View.VISIBLE);
            settingsIconPopupMenu.getMenu().getItem(0).setChecked(true);
        }

        if (showSpecialPage) {
            rankButtons.get(rankButtons.size()-2).setVisibility(View.VISIBLE);
            settingsIconPopupMenu.getMenu().getItem(1).setChecked(true);
        }

        rankButtons.get(currentPage).callOnClick();
    }
}