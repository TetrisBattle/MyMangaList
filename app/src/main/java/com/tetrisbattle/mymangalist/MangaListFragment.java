package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MangaListFragment extends Fragment {

    View view;
    RecyclerView mangaListView;
    ConstraintLayout addNewMangaView;
    ImageButton addNewMangaButton;
    EditText newUrl, newName, newChapter;
    Button addButton, cancelButton;

    InputMethodManager inputMethodManager;
    MyDatabaseHelper myDatabaseHelper;
    FirebaseDatabase db;
    DatabaseReference ref;
    MangaListAdapter mangaListAdapter;

    ConstraintLayout background;
    String table;

    String subscribedListName;
    boolean isPrivate;
    String page;

    public MangaListFragment(ConstraintLayout background, String table) {
        this.background = background;
        this.table = table;
    }

    public MangaListFragment(String subscribedListName, boolean isPrivate, String page) {
        this.subscribedListName = subscribedListName;
        this.isPrivate = isPrivate;
        this.page = page;
        this.table = "subscribedList";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manga_list, container, false);

        mangaListView = view.findViewById(R.id.mangaListView);
        addNewMangaButton = view.findViewById(R.id.addNewMangaButton);

        mangaListView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (!table.equals("subscribedList")) {
            addNewMangaView = view.findViewById(R.id.addNewMangaView);
            newUrl = view.findViewById(R.id.newUrl);
            newName = view.findViewById(R.id.newName);
            newChapter = view.findViewById(R.id.newChapter);
            addButton = view.findViewById(R.id.addButton);
            cancelButton = view.findViewById(R.id.cancelButton);

//            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            setupNewMangaFunction();
            myDatabaseHelper = new MyDatabaseHelper(getContext());
            myDatabaseHelper.setTable(table);

            List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
            mangaListAdapter = new MangaListAdapter(requireContext(), myMangaList, myDatabaseHelper, table, background);
            mangaListView.setAdapter(mangaListAdapter);
        } else {
            addNewMangaButton.setVisibility(View.INVISIBLE);
            db = FirebaseDatabase.getInstance();
            ref = db.getReference();
            getSubscribedList();
        }

        return view;
    }

    public void refresh() {
        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
        mangaListAdapter.setMangaList(myMangaList);
        mangaListAdapter.notifyDataSetChanged();
    }

    public void setupNewMangaFunction() {
        addNewMangaButton.setOnClickListener(v -> {
            addNewMangaButton.setVisibility(View.INVISIBLE);
            addNewMangaView.setVisibility(View.VISIBLE);
            newName.requestFocus();
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0); // show keyboard
        });

        newName.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                newChapter.requestFocus();
                return true;
            }
            return false;
        });

        newChapter.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                inputMethodManager.hideSoftInputFromWindow(newChapter.getWindowToken(), 0); // hide keyboard
                newChapter.clearFocus();
                return true;
            }
            return false;
        });

        newUrl.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                newName.requestFocus();
                return true;
            }
            return false;
        });

        addButton.setOnClickListener(v -> {
            if (newName.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                myDatabaseHelper.insertData(String.valueOf(newName.getText()), String.valueOf(newChapter.getText()), String.valueOf(newUrl.getText()));
                refresh();
                background.callOnClick();

                addNewMangaView.setVisibility(View.INVISIBLE);
                addNewMangaButton.setVisibility(View.VISIBLE);
                newName.setText("");
                newChapter.setText("");
                newUrl.setText("");
            }
        });

        cancelButton.setOnClickListener(v -> {
            background.callOnClick();
            addNewMangaView.setVisibility(View.INVISIBLE);
            addNewMangaButton.setVisibility(View.VISIBLE);
            newName.setText("");
            newChapter.setText("");
            newUrl.setText("");
        });
    }

    public void getSubscribedList() {
        ArrayList<MyManga> subscribedList = new ArrayList<>();
        ArrayList<String> mangaName = new ArrayList<>();
        ArrayList<String> mangaChapter = new ArrayList<>();
        DatabaseReference ref;

        if (isPrivate) {
            ref = db.getReference("publishedMangaLists/privateLists/" + subscribedListName + "/" + page);
        } else {
            ref = db.getReference("publishedMangaLists/publicLists/" + subscribedListName + "/" + page);
        }

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String name = singleSnapshot.getKey();
//                    String chapter = String.valueOf(singleSnapshot.child(name).child("chapter").getValue());

                    if (name != null)
                        mangaName.add(name);

//                    if (chapter != null)
//                        mangaChapter.add(chapter);
//                    else
//                        mangaChapter.add("");
                }

                for(int i=0; i<mangaName.size(); i++) {
                    MyManga myManga = new MyManga(i, mangaName.get(i), "13", "");
                    subscribedList.add(myManga);
                }

                mangaListAdapter = new MangaListAdapter(requireContext(), subscribedList, table);
                mangaListAdapter.setMangaList(subscribedList);
                mangaListView.setAdapter(mangaListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });
    }
}