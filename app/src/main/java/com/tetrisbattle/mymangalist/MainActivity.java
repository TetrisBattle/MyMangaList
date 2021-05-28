package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    int[] secretCode;
    int secretCodeInputPos = 0;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rankButtons = new ArrayList<>(pageIds.length);
        background = findViewById(R.id.background);
        settingsIcon = findViewById(R.id.settingsIcon);

        secretCode = new int[7];
        settingsIconPopupMenu = new PopupMenu(this, settingsIcon);
        settingsIconPopupMenu.getMenuInflater().inflate(R.menu.popup_settings, settingsIconPopupMenu.getMenu());
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        login();
        setupButtons();
        setupSettings();
        setupFromSharedPrefs();

        replaceFragment(new MangaListFragment(background, pageNames[activePage]));
//        replaceFragment(new MangaListFragment(background, "secret"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        background.requestFocus();
    }

    public void backgroundClick(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // hide keyboard
            getCurrentFocus().clearFocus();
        }
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
            int newActivePage = i;
            rankButton.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentPage", newActivePage);
                editor.apply();

                background.callOnClick();
                rankButtons.get(activePage).setBackgroundColor(getResources().getColor(R.color.colorRankButton, null));


                if (secretCodeInputPos == 6) secretCodeInputPos = 0;
                else secretCodeInputPos++;

                secretCode[secretCodeInputPos] = newActivePage;

                if (newActivePage == 5) {
                    int[] currentSecretCode = new int[7];
                    int k = 0;
                    for (int j=secretCodeInputPos+1-secretCode.length; j<secretCodeInputPos+1; j++) {
                        if (j >= 0) currentSecretCode[k] = secretCode[j];
                        else currentSecretCode[k] = secretCode[7+j];
                        k++;
                    }

                    if (currentSecretCode[0] == 0 &&
                        currentSecretCode[1] == 2 &&
                        currentSecretCode[2] == 4 &&
                        currentSecretCode[3] == 6 &&
                        currentSecretCode[4] == 1 &&
                        currentSecretCode[5] == 3 &&
                        currentSecretCode[6] == 5
                    ) {
                        replaceFragment(new MangaListFragment(background, "secret"));
                        secretCodeInputPos = 0;
                    } else {
                        rankButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                        replaceFragment(new MangaListFragment(background, pageNames[newActivePage]));
                    }
                } else {
                    rankButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    replaceFragment(new MangaListFragment(background, pageNames[newActivePage]));
                }

                activePage = newActivePage;
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
                    rankButtons.get(activePage).setBackgroundColor(getResources().getColor(R.color.colorRankButton, null));
                    replaceFragment(new PublishFragment(currentUser));
                    return true;
                } else if (item.getItemId() == R.id.popupSubscribe) {
                    replaceFragment(new SubscribeFragment());
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