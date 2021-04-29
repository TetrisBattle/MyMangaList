package com.tetrisbattle.mymangalist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
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

    private static final String[] rankButtonsName = {
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
    private static final int[] rankButtonsId = {
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
    Button addButton, cancelButton;
    RecyclerView recyclerView;
    ImageButton settings;

    MyRecyclerAdapter myRecyclerAdapter;
//    MyDatabaseHelper myDatabaseHelper;

//    String myTable = "rankS";
    int activeButton = 0;
    InputMethodManager inputMethodManager;

    FirebaseDatabase db;
    DatabaseReference ref;

    PopupMenu popupSettings;
    MenuInflater inflater;

    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        background = findViewById(R.id.background);
        addNewMangaView = findViewById(R.id.addNewMangaView);
        newUrl = findViewById(R.id.newUrl);
        newName = findViewById(R.id.newName);
        newChapter = findViewById(R.id.newChapter);
        addButton = findViewById(R.id.addButton);
        cancelButton = findViewById(R.id.cancelButton);
        recyclerView = findViewById(R.id.recyclerView);
        rankButtons = new ArrayList<>(rankButtonsId.length);
        settings = findViewById(R.id.settings);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        popupSettings = new PopupMenu(this, settings);
        inflater = popupSettings.getMenuInflater();
        inflater.inflate(R.menu.settings_popup, popupSettings.getMenu());

        currentUser = getCurrentUser();
//        myDatabaseHelper = new MyDatabaseHelper(this, myTable);

        setupButtons();
        setupEditTexts();

        rankButtons.get(activeButton).callOnClick();

//        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
//        myRecyclerAdapter = new MyRecyclerAdapter(this, myMangaList, myDatabaseHelper, myTable, background);
//        recyclerView.setAdapter(myRecyclerAdapter);



//        ref = db.getReference("users/" + currentUser + "/myMangaList/" + myTables[activeButton]);
//        ref.setValue("test");

//        ref = db.getReference("thien/myMangaList/S");
////        Log.d("myTest", "test: " + ref);
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                MyManga asd = snapshot.getValue(MyManga.class);
//                Log.d("myTest", "test: " + asd.chapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        ref = db.getReference("publicMangaLists/kupla/publisher");
//        ref = db.getReference().child("test/name2/imaginary/something");
//        Log.d("myTest", "test: " + ref);
//        ref.child("test/name2/something2").setValue("new test");
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = snapshot.getValue(String.class);
//                Log.d("myTest", "test: " + value);
//
//                if (value == null) {
//                    Log.d("myTest", "test: " + "null");
//                } else {
//                    Log.d("myTest", "test: " + "not null");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("myTest", "Firebase error");
//            }
//        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        background.requestFocus();
    }

    public void backgroundClick(View v) {
        background.requestFocus();
    }

    public String getCurrentUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            firebaseAuth.signInAnonymously();
        }

        return firebaseUser.getUid();
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
//                myTable = myTables[finalI];
//                myDatabaseHelper.setTable(myTable);
//                refresh();
            });
            rankButtons.add(rankButton);
        }

        settings.setOnClickListener(v -> {
            popupSettings.show();

            popupSettings.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.popupAddNewManga) {
                    addNewMangaView.setVisibility(View.VISIBLE);
                    newName.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); // show keyboard
                    return true;
                } else if (item.getItemId() == R.id.popupAddList) {
                    Toast.makeText(this, "AddList: empty", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.popupCopyList) {
                    Toast.makeText(this, "CopyList: empty", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            });
        });

        addButton.setOnClickListener(v -> {
            if (newName.getText().toString().equals("")) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            } else {
//                myDatabaseHelper.insertData(String.valueOf(newName.getText()), String.valueOf(newChapter.getText()), String.valueOf(newUrl.getText()));
                refresh();
                background.requestFocus();
                addNewMangaView.setVisibility(View.GONE);
                newName.setText("");
                newChapter.setText("");
                newUrl.setText("");
            }
        });

        cancelButton.setOnClickListener(v -> {
            background.requestFocus();
            addNewMangaView.setVisibility(View.GONE);
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

        newUrl.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                newName.requestFocus();
                return true;
            }
            return false;
        });

        newUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                inputMethodManager.hideSoftInputFromWindow(newUrl.getWindowToken(), 0); // hide keyboard
            }
        });
    }

    public void refresh() {
//        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
//        myRecyclerAdapter.setMangaList(myMangaList);
//        myRecyclerAdapter.notifyDataSetChanged();
    }
}