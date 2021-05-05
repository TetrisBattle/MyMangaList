package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private List<Button> rankButtons;
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

    ConstraintLayout background;
    ConstraintLayout addNewMangaView;
    EditText newUrl, newName, newChapter;
    ImageButton addNewMangaButton;
    Button addButton, cancelButton;
    RecyclerView recyclerView;
    ImageButton settings;

    PopupMenu popupSettings;
    MenuInflater inflater;
    InputMethodManager inputMethodManager;
    SharedPreferences sharedPreferences;

    MyRecyclerAdapter myRecyclerAdapter;
    MyDatabaseHelper myDatabaseHelper;
    FirebaseDatabase db;
    DatabaseReference ref;

    String currentUser;
    int activePage = 0;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        background = findViewById(R.id.background);
        addNewMangaView = findViewById(R.id.addNewMangaView);
        newUrl = findViewById(R.id.newUrl);
        newName = findViewById(R.id.newName);
        newChapter = findViewById(R.id.newChapter);
        addNewMangaButton = findViewById(R.id.addNewMangaButton);
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);
        recyclerView = findViewById(R.id.recyclerView);
        rankButtons = new ArrayList<>(pageIds.length);
        settings = findViewById(R.id.settings);

        popupSettings = new PopupMenu(this, settings);
        inflater = popupSettings.getMenuInflater();
        inflater.inflate(R.menu.settings_popup, popupSettings.getMenu());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        myDatabaseHelper = new MyDatabaseHelper(this, pageNames[activePage]);

        login();
        setupButtons();
        setupEditTexts();
        setupSettings();

        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
        myRecyclerAdapter = new MyRecyclerAdapter(this, myMangaList, myDatabaseHelper, pageNames[activePage], background);
        recyclerView.setAdapter(myRecyclerAdapter);

        setupFromSharedPrefs();
    }

    @Override
    protected void onStop() {
        super.onStop();
        background.requestFocus();
    }

    public void backgroundClick(View v) {
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // hide keyboard
        getCurrentFocus().clearFocus();
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
                rankButton.setBackgroundColor(getResources().getColor(R.color.colorRankButtonSelected, null));

                activePage = finalI;
                myDatabaseHelper.setTable(pageNames[activePage]);
                refresh();
            });
            rankButtons.add(rankButton);
        }

        addNewMangaButton.setOnClickListener(v -> {
            addNewMangaButton.setVisibility(View.INVISIBLE);
            addNewMangaView.setVisibility(View.VISIBLE);
            newName.requestFocus();
        });

        addButton.setOnClickListener(v -> {
            if (newName.getText().toString().equals("")) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                myDatabaseHelper.insertData(String.valueOf(newName.getText()), String.valueOf(newChapter.getText()), String.valueOf(newUrl.getText()));
                refresh();
                background.requestFocus();
                addNewMangaView.setVisibility(View.INVISIBLE);
                addNewMangaButton.setVisibility(View.VISIBLE);
                newName.setText("");
                newChapter.setText("");
                newUrl.setText("");
            }
        });

        cancelButton.setOnClickListener(v -> {
            background.requestFocus();
            addNewMangaView.setVisibility(View.INVISIBLE);
            addNewMangaButton.setVisibility(View.VISIBLE);
            newName.setText("");
            newChapter.setText("");
            newUrl.setText("");
        });
    }

    public void setupEditTexts() {
        newName.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                newChapter.requestFocus();
                return true;
            }
            return false;
        });

        newName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                newName.clearFocus();
            }
        });

        newChapter.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                newChapter.clearFocus();
                return true;
            }
            return false;
        });

        newChapter.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                inputMethodManager.hideSoftInputFromWindow(newChapter.getWindowToken(), 0); // hide keyboard
                newChapter.clearFocus();
            }
        });

        newUrl.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                newName.requestFocus();
                return true;
            }
            return false;
        });

        newUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                newUrl.clearFocus();
            }
        });
    }

    public void setupSettings() {
        settings.setOnClickListener(v -> {
            popupSettings.show();

            popupSettings.setOnMenuItemClickListener(item -> {
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
                } else if (item.getItemId() == R.id.popupExport) {
                    publishPublic("Test public");

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
            popupSettings.getMenu().getItem(0).setChecked(true);
        }
        if (showSpecialPage) {
            rankButtons.get(rankButtons.size()-2).setVisibility(View.VISIBLE);
            popupSettings.getMenu().getItem(1).setChecked(true);
        }

        rankButtons.get(currentPage).callOnClick();
    }

    public void publishPublic(String publicListName) {
        ref = db.getReference("publishedMangaLists/" + publicListName + "/" + "owner");
        ref.setValue(currentUser);

        ref = db.getReference("publishedMangaLists/" + publicListName + "/owner");
        ref.setValue(currentUser);

        List<ArrayList<MyManga>> myMangaListDb = myDatabaseHelper.getMyMangaListDb();
        for (int i=0; i<myMangaListDb.size(); i++) {
            List<MyManga> myMangaList = myMangaListDb.get(i);
            for (int j=0; j<myMangaList.size(); j++) {
                MyManga myManga = myMangaList.get(j);

                ref = db.getReference("publishedMangaLists/" + publicListName +
                        "/myMangaList/" + pageNames[i] + "/" + myManga.name);

                ref.child("chapter").setValue(myManga.chapter);
                ref.child("url").setValue(myManga.url);
            }
        }
    }

    public void refresh() {
        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
        myRecyclerAdapter.setMangaList(myMangaList);
        myRecyclerAdapter.notifyDataSetChanged();
    }
}