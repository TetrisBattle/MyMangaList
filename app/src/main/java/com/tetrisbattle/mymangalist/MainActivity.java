package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final String[] myTables = {
            "myAnimeListTableX",
            "myAnimeListTableS",
            "myAnimeListTableA",
            "myAnimeListTableB",
            "myAnimeListTableC",
            "myAnimeListTableD",
            "myAnimeListTableE",
            "myAnimeListTable"
    };

    private List<Button> rankButtons;
    private static final int[] rankButtonsId = {
            R.id.rankButtonX,
            R.id.rankButtonS,
            R.id.rankButtonA,
            R.id.rankButtonB,
            R.id.rankButtonC,
            R.id.rankButtonD,
            R.id.rankButtonE,
            R.id.planToReadButton
    };

    ConstraintLayout background;
    EditText newName, newChapter;
    Button addButton;
    RecyclerView recyclerView;

    MyRecyclerAdapter myRecyclerAdapter;
    MyDatabaseHelper myDatabaseHelper;

    String myTable = "myAnimeListTable";
    int activeButton = rankButtonsId.length-1;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById (R.id.background);
        newName = findViewById (R.id.newName);
        newChapter = findViewById (R.id.newChapter);
        addButton = findViewById (R.id.addButton);
        recyclerView = findViewById (R.id.recyclerView);
        rankButtons = new ArrayList<>(rankButtonsId.length);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myDatabaseHelper = new MyDatabaseHelper(this, myTable);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setupButtons();
        setupEditTexts();

        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
        myRecyclerAdapter = new MyRecyclerAdapter(this, myMangaList, myDatabaseHelper, myTable, background);
        recyclerView.setAdapter(myRecyclerAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        background.requestFocus();
    }

    public void backgroundClick(View v) {
        background.requestFocus();
    }

    public void setupButtons() {
        for (int i=0; i<rankButtonsId.length; i++) {
            Button rankButton = findViewById(rankButtonsId[i]);
            int finalI = i;
            rankButton.setOnClickListener(v -> {
                background.requestFocus();
                rankButtons.get(activeButton).setBackgroundColor(getResources().getColor(R.color.colorRankButton, null));
                rankButton.setBackgroundColor(getResources().getColor(R.color.colorRankButtonSelected, null));

                activeButton = finalI;
                myTable = myTables[finalI];
                myDatabaseHelper.setTable(myTable);
                refresh();
            });
            rankButtons.add(rankButton);
        }

        addButton.setOnClickListener(v -> {
            if (newName.getText().toString().equals("")) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            } else if (newChapter.getText().toString().equals("")) {
                //Toast.makeText(MainActivity.this, "Chapter can't be empty", Toast.LENGTH_SHORT).show();
                myDatabaseHelper.insertData(String.valueOf(newName.getText()), "");
                refresh();
                newName.setText("");
                newChapter.setText("");
                newName.clearFocus();
                newChapter.clearFocus();
            } else {
                myDatabaseHelper.insertData(String.valueOf(newName.getText()), String.valueOf(newChapter.getText()));
                refresh();
                newName.setText("");
                newChapter.setText("");
                newName.clearFocus();
                newChapter.clearFocus();
            }
        });
    }

    public void setupEditTexts() {
        newName.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                newName.clearFocus();
                return true;
            }
            return false;
        });

        newName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                inputMethodManager.hideSoftInputFromWindow(newName.getWindowToken(), 0); // hide keyboard
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
            }
        });
    }

    public void refresh() {
        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
        myRecyclerAdapter.setMangaList(myMangaList);
        myRecyclerAdapter.notifyDataSetChanged();
    }
}