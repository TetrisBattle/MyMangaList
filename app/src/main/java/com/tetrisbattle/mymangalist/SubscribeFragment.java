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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubscribeFragment extends Fragment {

    View view;
    RecyclerView subscribeList;
    SubscribeListAdapter subscribeListAdapter;

    FirebaseDatabase db;
    DatabaseReference publicRef;
    DatabaseReference privateRef;

    ArrayList<String> publicLists;
    ArrayList<String> privateLists;

    public SubscribeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        subscribeList = view.findViewById(R.id.subscribeList);
        subscribeList.setLayoutManager(new LinearLayoutManager(getContext()));

        publicLists = new ArrayList<>();
        privateLists = new ArrayList<>();

        db = FirebaseDatabase.getInstance();

        publicRef = db.getReference("publishedMangaListNames/publicLists");
        ValueEventListener publicEventListener = publicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> data = new ArrayList<>();
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    data.add(value);
                }
                publicLists = data;
                subscribeListAdapter = new SubscribeListAdapter(getContext(), publicLists, privateLists);
                subscribeList.setAdapter(subscribeListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });

        privateRef = db.getReference("publishedMangaListNames/privateLists");
        ValueEventListener privateEventListener = privateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> data = new ArrayList<>();
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    String value = singleSnapshot.getKey();
                    data.add(value);
                }
                privateLists = data;
                subscribeListAdapter = new SubscribeListAdapter(getContext(), publicLists, privateLists);
                subscribeList.setAdapter(subscribeListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("myTest", "Failed to read value.", error.toException());
            }
        });

//        privateRef.removeEventListener(publicEventListener);
//        privateRef.removeEventListener(privateEventListener);

        return view;
    }
}