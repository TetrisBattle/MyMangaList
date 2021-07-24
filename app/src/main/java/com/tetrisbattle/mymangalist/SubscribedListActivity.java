package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubscribedListActivity extends AppCompatActivity {

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
    TextView title;

    PopupMenu settingsIconPopupMenu;
    SharedPreferences sharedPreferences;

    String currentUser;
    int activePage = 0;
    String subscribedListName;
    boolean isPrivate;

    int[] secretCode;
    int secretCodeInputPos = 0;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_list);

        rankButtons = new ArrayList<>(pageIds.length);
        settingsIcon = findViewById(R.id.settingsIcon);
        title = findViewById(R.id.title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            subscribedListName = extras.getString("listName");
            isPrivate = extras.getBoolean("isPrivate");
            title.setText(subscribedListName);
        }

        settingsIconPopupMenu = new PopupMenu(this, settingsIcon);
        settingsIconPopupMenu.getMenuInflater().inflate(R.menu.popup_settings, settingsIconPopupMenu.getMenu());

        currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        setupExtraPages();
        setupButtons();
        setupSettings();

        //replaceFragment(new MangaListFragment(background, pageNames[activePage]));
        rankButtons.get(activePage).setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
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
                rankButtons.get(activePage).setBackgroundColor(getResources().getColor(R.color.colorRankButton, null));

                rankButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                replaceFragment(new MangaListFragment(subscribedListName, isPrivate, pageNames[newActivePage]));

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
                    rankButtons.get(activePage).setBackgroundColor(getResources().getColor(R.color.colorRankButton, null));
                    replaceFragment(new SubscribeFragment());
                    return true;
                } else {
                    return false;
                }
            });
        });
    }

    public void setupExtraPages() {
        if (false) {
            rankButtons.get(rankButtons.size()-1).setVisibility(View.VISIBLE);
            settingsIconPopupMenu.getMenu().getItem(0).setChecked(true);
        }

        if (false) {
            rankButtons.get(rankButtons.size()-2).setVisibility(View.VISIBLE);
            settingsIconPopupMenu.getMenu().getItem(1).setChecked(true);
        }
    }
}