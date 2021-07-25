package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PublishFragment extends Fragment {

    View view;

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
    RadioGroup publishGroup;
    RadioButton publicList, privateList;
    ConstraintLayout nameLayout;
    ConstraintLayout passwordLayout;
    EditText listName, password;
    Button saveButton;

    SharedPreferences sharedPreferences;
    Animation animationAppear, animationDisappear;

    FirebaseDatabase db;
    DatabaseReference ref;
    String currentUser;
    String currentListName;

    public PublishFragment(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_publish, container, false);

//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        publish = view.findViewById(R.id.publish);
        publishGroup = view.findViewById(R.id.publishGroup);
        publicList = view.findViewById(R.id.publicList);
        privateList = view.findViewById(R.id.privateList);
        nameLayout = view.findViewById(R.id.nameLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        listName = view.findViewById(R.id.listName);
        password = view.findViewById(R.id.password);
        saveButton = view.findViewById(R.id.saveButton);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        animationAppear = AnimationUtils.loadAnimation(getContext(), R.anim.animation_appear);
        animationDisappear = AnimationUtils.loadAnimation(getContext(), R.anim.animation_disappear);
        //publishGroup.setAnimation(animationAppear);
        //passwordLayout.setAnimation(animationAppear);

        setupFromSharedPrefs();
        setupFunctions();

//        publishPublicList("testPublic");
//        publishPublicList("testPublic2");
//        publishPrivateList("testPrivate", "password");
//        publishPrivateList("testPrivate2", "password2");

        return view;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setupFromSharedPrefs() {
        currentListName = sharedPreferences.getString("listName", "");
        listName.setText(currentListName);

        boolean sharedPrefsPublished = sharedPreferences.getBoolean("published", false);

        if(sharedPrefsPublished) {
            publish.setChecked(true);
            publishGroup.setVisibility(View.VISIBLE);
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

    private void setupFunctions() {
        publish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                publishGroup.setVisibility(View.VISIBLE);
                publishGroup.startAnimation(animationAppear);

                nameLayout.startAnimation(animationAppear);
                nameLayout.setVisibility(View.VISIBLE);

                if (privateList.isChecked()) {
                    passwordLayout.startAnimation(animationAppear);
                    passwordLayout.setVisibility(View.VISIBLE);
                    password.setText("");
                }
            } else {
                publishGroup.startAnimation(animationDisappear);
                publishGroup.setVisibility(View.INVISIBLE);

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
            if (isNetworkAvailable()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (publish.isChecked()) {
                    String newListName = String.valueOf(listName.getText());
                    if (newListName.equals("")) {
                        Toast toast = Toast.makeText(getContext(), "List name can't be empty", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 400);
                        toast.show();
                    } else {
                        ArrayList<String> listNames = new ArrayList<>();

                        DatabaseReference publicRef = db.getReference("publishedMangaLists/publicLists");
                        publicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                                    String value = singleSnapshot.getKey();
                                    if (value != null && !value.equals(currentListName))
                                        listNames.add(value);
                                }

                                DatabaseReference privateRef = db.getReference("publishedMangaLists/privateLists");
                                privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                                            String value = singleSnapshot.getKey();
                                            if (value != null && !value.equals(currentListName))
                                                listNames.add(value);
                                        }

                                        boolean previousPrivateList = sharedPreferences.getBoolean("privateList", false);
                                        if (currentListName.equals(newListName) &&
                                                previousPrivateList == privateList.isChecked()) {
                                            Toast.makeText(getContext(), "List is already published", Toast.LENGTH_SHORT).show();
                                        } else {
                                            boolean nameIsReserved = false;
                                            for (int i=0; i<listNames.size(); i++) {
                                                if (!listNames.get(i).equals(currentListName) &&
                                                        String.valueOf(listNames.get(i)).equals(newListName)) {
                                                    nameIsReserved = true;
                                                    break;
                                                }
                                            }

                                            if (!nameIsReserved) {
                                                deleteList();
                                                if (publicList.isChecked()) publishPublicList(newListName);
                                                else publishPrivateList(newListName, String.valueOf(password.getText()));
                                            } else {
                                                Toast toast = Toast.makeText(getContext(), "List name is already taken", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.BOTTOM, 0, 400);
                                                toast.show();
                                            }
                                        }

                                        editor.putString("listName", "");
                                        editor.putBoolean("published", publish.isChecked());
                                        editor.putBoolean("privateList", privateList.isChecked());
                                        if (privateList.isChecked()) editor.putString("password", String.valueOf(password.getText()));
                                        else editor.putString("password", "");
                                        editor.apply();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("myTest", "Failed to read value.", error.toException());
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("myTest", "Failed to read value.", error.toException());
                            }
                        });
                    }
                } else {
                    deleteList();
                    editor.putString("listName", "");
                    editor.putBoolean("published", publish.isChecked());
                    editor.putBoolean("privateList", privateList.isChecked());
                    if (privateList.isChecked()) editor.putString("password", String.valueOf(password.getText()));
                    else editor.putString("password", "");
                    editor.apply();
                }
            } else {
                Toast toast = Toast.makeText(getContext(), "Internet is not available", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 400);
                toast.show();
            }
        });
    }

    public void publishPublicList(String listName) {
        db.getReference("publishedMangaLists/publicLists/" + listName + "/owner").setValue(currentUser);

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getContext());
        List<ArrayList<MyManga>> myMangaListDb = myDatabaseHelper.getMyMangaListDb();
        for (int i=0; i<myMangaListDb.size(); i++) {
            List<MyManga> myMangaList = myMangaListDb.get(i);
            for (int j=0; j<myMangaList.size(); j++) {
                MyManga myManga = myMangaList.get(j);

                ref = db.getReference("publishedMangaLists/publicLists/" + listName +
                        "/" + pageNames[i] + "/" + myManga.name);

                ref.child("chapter").setValue(myManga.chapter);
                ref.child("url").setValue(myManga.url);
            }
        }
    }

    public void publishPrivateList(String listName, String password) {
        db.getReference("publishedMangaLists/privateLists/" + listName + "/owner").setValue(currentUser);
        db.getReference("publishedMangaLists/privateLists/" + listName + "/password").setValue(password);

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getContext());

        List<ArrayList<MyManga>> myMangaListDb = myDatabaseHelper.getMyMangaListDb();
        for (int i=0; i<myMangaListDb.size(); i++) {
            List<MyManga> myMangaList = myMangaListDb.get(i);
            for (int j=0; j<myMangaList.size(); j++) {
                MyManga myManga = myMangaList.get(j);

                ref = db.getReference("publishedMangaLists/privateLists/" + listName +
                        "/" + pageNames[i] + "/" + myManga.name);

                ref.child("chapter").setValue(myManga.chapter);
                ref.child("url").setValue(myManga.url);
            }
        }
    }

    public void deleteList() {
        String listName = sharedPreferences.getString("listName", "");
        boolean sharedPrefsPrivateList = sharedPreferences.getBoolean("privateList", false);

        if (sharedPrefsPrivateList) {
            db.getReference("publishedMangaLists/privateLists/" + listName).removeValue();
        } else {
            db.getReference("publishedMangaLists/publicLists/" + listName).removeValue();
        }
    }
}