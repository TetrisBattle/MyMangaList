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

public class SubscribeFragment extends Fragment {

    View view;
    RecyclerView subscribeList;
    SubscribeListAdapter subscribeListAdapter;

    FirebaseDatabase db;
    DatabaseReference publicRef;
    DatabaseReference privateRef;

    ArrayList<String> publicLists;
    ArrayList<String> privateLists;

    SharedPreferences sharedPreferences;
    String sharedPrefsListName;
    String currentUser;

    DatabaseReference userRef;
    DatabaseReference subscribedPublicRef;
    DatabaseReference subscribedPrivateRef;

    ArrayList<String> publicListNames;
    ArrayList<String> privateListNames;

    public SubscribeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        subscribeList = view.findViewById(R.id.subscribeList);
        subscribeList.setLayoutManager(new LinearLayoutManager(getContext()));

        publicLists = new ArrayList<>();
        privateLists = new ArrayList<>();

        db = FirebaseDatabase.getInstance();

        sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        sharedPrefsListName = sharedPreferences.getString("listName", "");

        setupCards();

//        privateRef.removeEventListener(publicEventListener);
//        privateRef.removeEventListener(privateEventListener);

        return view;
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
                    Toast.makeText(requireContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            currentUser = firebaseUser.getUid();
            //Log.d("myTest", "already signed in: " + currentUser);
            //firebaseAuth.signOut();
        }
    }

    public void setupCards() {
        login();

        if (currentUser != null) {
            userRef = db.getReference("users/" + currentUser);
            subscribedPublicRef = db.getReference("users/" + currentUser + "/subscribed/publicLists");
            subscribedPrivateRef = db.getReference("users/" + currentUser + "/subscribed/privateLists");

            //ValueEventListener subscribedPublicEventListener =
                subscribedPublicRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    subscribedPublicRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    publicListNames = new ArrayList<>();
                    for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                        String value = singleSnapshot.getKey();
                        if (value != null)
                            publicListNames.add(value);
                    }
                    setupPublicCards();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("myTest", "Failed to read value.", error.toException());
                }
            });

            //ValueEventListener subscribedPrivateEventListener =
                    subscribedPrivateRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    subscribedPrivateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    privateListNames = new ArrayList<>();
                    for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                        String value = singleSnapshot.getKey();
                        if (value != null)
                            privateListNames.add(value);
                    }
                    setupPrivateCards();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("myTest", "Failed to read value.", error.toException());
                }
            });
        }

        //db.getReference("publishedMangaListNames/privateLists/" + listName + "/owner").setValue(currentUser);
        //db.getReference("publishedMangaLists/privateLists/" + listName).removeValue();
    }

    public void setupPublicCards() {
        publicRef = db.getReference("publishedMangaListNames/publicLists");
        //ValueEventListener publicEventListener =
//                publicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                publicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("myTest", "public lists changed");

                ArrayList<String> data = new ArrayList<>();
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null && !value.equals(sharedPrefsListName))
                        data.add(value);
                }
                publicLists = data;
                subscribeListAdapter = new SubscribeListAdapter(requireContext(), publicLists, privateLists, publicListNames, privateListNames, userRef);
                subscribeList.setAdapter(subscribeListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });
    }

    public void setupPrivateCards() {
        privateRef = db.getReference("publishedMangaListNames/privateLists");
        //ValueEventListener privateEventListener =
//                privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                privateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> data = new ArrayList<>();
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null && !value.equals(sharedPrefsListName))
                        data.add(value);
                }
                privateLists = data;
                subscribeListAdapter = new SubscribeListAdapter(requireContext(), publicLists, privateLists, publicListNames, privateListNames, userRef);
                subscribeList.setAdapter(subscribeListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });
    }
}