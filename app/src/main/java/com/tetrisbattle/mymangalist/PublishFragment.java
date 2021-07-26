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

    CheckBox publishCheckBox;
    RadioGroup publishGroup;
    RadioButton publicList, privateList;
    ConstraintLayout nameLayout;
    ConstraintLayout passwordLayout;
    EditText listNameEditText, passwordEditText;
    Button saveButton;

    SharedPreferences sharedPreferences;
    boolean sharedPrefsPublished;
    boolean sharedPrefsPrivateList;
    String sharedPrefsListName;
    String sharedPrefsPassword;

    Animation animationAppear, animationDisappear;

    FirebaseDatabase db;
    DatabaseReference ref;
    String currentUser;
    String newListName;
    String newPassword;

    public PublishFragment(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_publish, container, false);

//        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        publishCheckBox = view.findViewById(R.id.publish);
        publishGroup = view.findViewById(R.id.publishGroup);
        publicList = view.findViewById(R.id.publicList);
        privateList = view.findViewById(R.id.privateList);
        nameLayout = view.findViewById(R.id.nameLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        listNameEditText = view.findViewById(R.id.listName);
        passwordEditText = view.findViewById(R.id.password);
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

//        db.getReference("publishedMangaLists/publicLists/" + "testPublic" + "/owner").setValue("testPublic");
//        db.getReference("publishedMangaLists/privateLists/" + "testPrivate" + "/owner").setValue("testPrivate");

        return view;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setupFromSharedPrefs() {
        sharedPrefsListName = sharedPreferences.getString("listName", "");
        listNameEditText.setText(sharedPrefsListName);

        sharedPrefsPublished = sharedPreferences.getBoolean("published", false);

        if(sharedPrefsPublished) {
            publishCheckBox.setChecked(true);
            publishGroup.setVisibility(View.VISIBLE);
            nameLayout.setVisibility(View.VISIBLE);

            sharedPrefsPrivateList = sharedPreferences.getBoolean("privateList", false);
            if (sharedPrefsPrivateList) {
                privateList.setChecked(true);
                passwordLayout.setVisibility(View.VISIBLE);

                sharedPrefsPassword = sharedPreferences.getString("password", "");
                passwordEditText.setText(sharedPrefsPassword);
            }
        }
    }

    private void setupFunctions() {
        publishCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                publishGroup.setVisibility(View.VISIBLE);
                publishGroup.startAnimation(animationAppear);

                nameLayout.startAnimation(animationAppear);
                nameLayout.setVisibility(View.VISIBLE);

                if (privateList.isChecked()) {
                    passwordLayout.startAnimation(animationAppear);
                    passwordLayout.setVisibility(View.VISIBLE);
                    passwordEditText.setText("");
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
                passwordEditText.setText("");
            } else {
                passwordLayout.startAnimation(animationDisappear);
                passwordLayout.setVisibility(View.INVISIBLE);
            }
        });

        saveButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                if (publishCheckBox.isChecked()) {
                    newListName = String.valueOf(listNameEditText.getText());
                    newPassword = String.valueOf(passwordEditText.getText());

                    if (newListName.equals("")) {
                        Toast toast = Toast.makeText(getContext(), "List name can't be empty", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 400);
                        toast.show();
                    } else {
                        // if list was published before
                        if(sharedPrefsPublished == publishCheckBox.isChecked()) {
                            // if name is same
                            if (sharedPrefsListName.equals(newListName)) {
                                // if list was private and its still private
                                if (sharedPrefsPrivateList && privateList.isChecked()) {
                                    // if password is same
                                    if (sharedPrefsPassword.equals(newPassword)) {
                                        Toast.makeText(getContext(), "List is already published", Toast.LENGTH_SHORT).show();
                                    } else updatePassword();
                                } else publish();
                            } else publish();
                        } else { // the list has not been published before
                            ArrayList<String> listNames = new ArrayList<>();

                            DatabaseReference publicRef = db.getReference("publishedMangaLists/publicLists");
                            publicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                                        String value = singleSnapshot.getKey();
                                        if (value != null && !value.equals(sharedPrefsListName))
                                            listNames.add(value);
                                    }

                                    DatabaseReference privateRef = db.getReference("publishedMangaLists/privateLists");
                                    privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                                                String value = singleSnapshot.getKey();
                                                if (value != null && !value.equals(sharedPrefsListName))
                                                    listNames.add(value);
                                            }

                                            boolean nameIsTaken = false;
                                            for (int i=0; i<listNames.size(); i++) {
                                                if (!newListName.equals(listNames.get(i))) {
                                                    Toast toast = Toast.makeText(getContext(), "List name is already taken", Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.BOTTOM, 0, 400);
                                                    toast.show();

                                                    nameIsTaken = true;
                                                    break;
                                                }
                                            }

                                            if (!nameIsTaken) publish();
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
                    }
                } else {
                    // if the list was published before
                    if(sharedPrefsPublished != publishCheckBox.isChecked()) {
                        deleteList();
                        updateSharedPrefs();
                    }
                }
            } else {
                Toast toast = Toast.makeText(getContext(), "Internet is not available", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 400);
                toast.show();
            }
        });
    }

    public void publish() {
        deleteList();

        if (privateList.isChecked()) {
            db.getReference("publishedMangaLists/privateLists/" + newListName + "/owner").setValue(currentUser);
            db.getReference("publishedMangaLists/privateLists/" + newListName + "/password").setValue(newPassword);
            ref = db.getReference("publishedMangaLists/privateLists/" + newListName);
        }
        else {
            db.getReference("publishedMangaLists/publicLists/" + newListName + "/owner").setValue(currentUser);
            ref = db.getReference("publishedMangaLists/publicLists/" + newListName);
        }

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getContext());
        List<ArrayList<MyManga>> myMangaListDb = myDatabaseHelper.getMyMangaListDb();

        for (int i=0; i<myMangaListDb.size(); i++) {
            List<MyManga> myMangaList = myMangaListDb.get(i);
            for (int j=0; j<myMangaList.size(); j++) {
                MyManga myManga = myMangaList.get(j);

                ref.child(pageNames[i] + "/" + myManga.name + "/chapter").setValue(myManga.chapter);
                ref.child(pageNames[i] + "/" + myManga.name + "/url").setValue(myManga.url);
            }
        }

        updateSharedPrefs();
    }

    public void deleteList() {
        sharedPrefsPublished = sharedPreferences.getBoolean("published", false);
        if (sharedPrefsPublished) { // if the list was published
            if (sharedPrefsPrivateList) {
                db.getReference("publishedMangaLists/privateLists/" + sharedPrefsListName).removeValue();
            } else {
                db.getReference("publishedMangaLists/publicLists/" + sharedPrefsListName).removeValue();
            }
        }
    }

    public void updateSharedPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("published", publishCheckBox.isChecked());
        editor.putBoolean("privateList", privateList.isChecked());
        editor.putString("listName", newListName);

        if (!publishCheckBox.isChecked()) editor.putString("password", "");
        else if (!privateList.isChecked()) editor.putString("password", "");
        else editor.putString("password", newPassword);
        editor.apply();

        sharedPrefsPublished = publishCheckBox.isChecked();
        sharedPrefsPrivateList = privateList.isChecked();
        sharedPrefsListName = newListName;
        sharedPrefsPassword = newPassword;
    }

    public void updatePassword() {
        if(privateList.isChecked()) {
            db.getReference("publishedMangaLists/privateLists/" +
                    newListName + "/password").setValue(newPassword);
        } else {
            db.getReference("publishedMangaLists/publicLists/" +
                    newListName + "/password").setValue(newPassword);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", newPassword);
        editor.apply();

        sharedPrefsPassword = newPassword;
    }
}