package com.tetrisbattle.mymangalist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubscribeFragment extends Fragment {

    View view;
    RecyclerView subscribeListView;
    SubscribeAdapter subscribeAdapter;
    TextView addNewSubscribeList;

    String currentUser;
    FirebaseDatabase db;

    public SubscribeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscribed, container, false);

        subscribeListView = view.findViewById(R.id.subscribedListView);
        subscribeListView.setLayoutManager(new LinearLayoutManager(getContext()));
        addNewSubscribeList = view.findViewById(R.id.addNewSubscribeList);

        addNewSubscribeList.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new NewSubscribeFragment());
            fragmentTransaction.commit();
        });

        login();
        db = FirebaseDatabase.getInstance();

        getSubscribedList();

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

    public void getSubscribedList() {
        ArrayList<String> publicList = new ArrayList<>();
        ArrayList<String> privateList = new ArrayList<>();

        DatabaseReference publicRef = db.getReference("users/" + currentUser + "/subscribed/publicLists");
        DatabaseReference privateRef = db.getReference("users/" + currentUser + "/subscribed/privateLists");

        publicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null)
                        publicList.add(value);
                }
                subscribeAdapter = new SubscribeAdapter(getContext(), publicList, privateList);
                subscribeListView.setAdapter(subscribeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });

        privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null)
                        privateList.add(value);
                }
                subscribeAdapter = new SubscribeAdapter(getContext(), publicList, privateList);
                subscribeListView.setAdapter(subscribeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });
    }

    public void getSubscribeLists() {
        ArrayList<String> publicList = new ArrayList<>();
        ArrayList<String> privateList = new ArrayList<>();

        DatabaseReference publicRef = db.getReference("publishedMangaListNames/publicLists");
        DatabaseReference privateRef = db.getReference("publishedMangaListNames/privateLists");

        publicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null)
                        publicList.add(value);
                }
                subscribeAdapter = new SubscribeAdapter(getContext(), publicList, privateList);
                subscribeListView.setAdapter(subscribeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });

        privateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null)
                        privateList.add(value);
                }
                subscribeAdapter = new SubscribeAdapter(getContext(), publicList, privateList);
                subscribeListView.setAdapter(subscribeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });
    }
}