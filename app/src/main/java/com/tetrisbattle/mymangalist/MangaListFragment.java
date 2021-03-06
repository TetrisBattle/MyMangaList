package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public MangaListFragment(ConstraintLayout background, String table) {
        this.background = background;
        this.table = table;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manga_list, container, false);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mangaListView = view.findViewById(R.id.mangaListView);
        addNewMangaView = view.findViewById(R.id.addNewMangaView);
        addNewMangaButton = view.findViewById(R.id.addNewMangaButton);
        newUrl = view.findViewById(R.id.newUrl);
        newName = view.findViewById(R.id.newName);
        newChapter = view.findViewById(R.id.newChapter);
        addButton = view.findViewById(R.id.addButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        myDatabaseHelper = new MyDatabaseHelper(getContext());
        myDatabaseHelper.setTable(table);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        List<MyManga> myMangaList = myDatabaseHelper.getMyMangaList();
        mangaListAdapter = new MangaListAdapter(requireContext(), myMangaList, myDatabaseHelper, table, background);
        mangaListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mangaListView.setAdapter(mangaListAdapter);

        setupNewMangaFunction();

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
}