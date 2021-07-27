package com.tetrisbattle.mymangalist;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class NewSubscribeFragment extends Fragment {

    View view;
    RecyclerView newSubscribeListView;
    NewSubscribeAdapter newSubscribeAdapter;

    FirebaseDatabase db;
    DatabaseReference publicRef;
    DatabaseReference privateRef;
    ValueEventListener publicEventListener;
    ValueEventListener privateEventListener;

    ArrayList<String> publicLists;
    ArrayList<String> privateLists;

    SharedPreferences sharedPreferences;
    String sharedPrefsListName;
    String currentUser;

    DatabaseReference userRef;

    ArrayList<String> subscribedPublicListNames;
    ArrayList<String> subscribedPrivateListNames;

    public NewSubscribeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscribe_new, container, false);

        newSubscribeListView = view.findViewById(R.id.newSubscribeListView);
        newSubscribeListView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseDatabase.getInstance();

        sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        sharedPrefsListName = sharedPreferences.getString("listName", "");

        publicLists = new ArrayList<>();
        privateLists = new ArrayList<>();

        setupCards();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        publicRef.removeEventListener(publicEventListener);
        privateRef.removeEventListener(privateEventListener);
    }

    public void login() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentUser = firebaseAuth.getCurrentUser().getUid();
                    Log.d("myTest", "new user: " + currentUser);
                } else {
                    Toast.makeText(requireContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            currentUser = firebaseUser.getUid();
        }
    }

    public void setupCards() {
        login();

        if (currentUser != null) {
            userRef = db.getReference("users/" + currentUser);

            DatabaseReference subscribedPublicRef = db.getReference("users/" + currentUser + "/subscribed/publicLists");
            DatabaseReference subscribedPrivateRef = db.getReference("users/" + currentUser + "/subscribed/privateLists");

            subscribedPublicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    subscribedPublicListNames = new ArrayList<>();
                    for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                        String value = singleSnapshot.getKey();
                        if (value != null)
                            subscribedPublicListNames.add(value);
                    }
                    setupPublicCards();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("myTest", "Failed to read value.", error.toException());
                }
            });

            subscribedPrivateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    subscribedPrivateListNames = new ArrayList<>();
                    for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                        String value = singleSnapshot.getKey();
                        if (value != null)
                            subscribedPrivateListNames.add(value);
                    }
                    setupPrivateCards();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("myTest", "Failed to read value.", error.toException());
                }
            });
        }
    }

    public void setupPublicCards() {
        publicRef = db.getReference("publishedMangaLists/publicLists");
        publicEventListener = publicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> data = new ArrayList<>();
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null && !value.equals(sharedPrefsListName))
                        data.add(value);
                }
                publicLists = data;
                newSubscribeAdapter = new NewSubscribeAdapter(requireContext(), publicLists, privateLists, subscribedPublicListNames, subscribedPrivateListNames, userRef);
                newSubscribeListView.setAdapter(newSubscribeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });
    }

    public void setupPrivateCards() {
        privateRef = db.getReference("publishedMangaLists/privateLists");
        privateEventListener = privateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> data = new ArrayList<>();
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null && !value.equals(sharedPrefsListName))
                        data.add(value);
                }
                privateLists = data;
                newSubscribeAdapter = new NewSubscribeAdapter(requireContext(), publicLists, privateLists, subscribedPublicListNames, subscribedPrivateListNames, userRef);
                newSubscribeListView.setAdapter(newSubscribeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });
    }
}