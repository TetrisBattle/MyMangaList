package com.tetrisbattle.mymangalist;

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

public class SubscribedFragment extends Fragment {

    View view;
    RecyclerView subscribedListView;
    SubscribedAdapter subscribedAdapter;

    String currentUser;
    ArrayList<String> subscribedList;

    public SubscribedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscribed, container, false);

        subscribedListView = view.findViewById(R.id.subscribedListView);
        subscribedListView.setLayoutManager(new LinearLayoutManager(getContext()));

        subscribedList = new ArrayList<>();

        login();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference subscribedPublicRef = db.getReference("users/" + currentUser + "/subscribed/publicLists");
        DatabaseReference subscribedPrivateRef = db.getReference("users/" + currentUser + "/subscribed/privateLists");

        subscribedPublicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null)
                        subscribedList.add(value);
                }
                subscribedAdapter = new SubscribedAdapter(getContext(), subscribedList);
                subscribedListView.setAdapter(subscribedAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });

        subscribedPrivateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    if (value != null)
                        subscribedList.add(value);
                }
                subscribedAdapter = new SubscribedAdapter(getContext(), subscribedList);
                subscribedListView.setAdapter(subscribedAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });

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
}