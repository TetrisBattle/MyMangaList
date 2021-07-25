package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> subscribedPublicList;
    ArrayList<String> subscribedPrivateList;

    public SubscribeAdapter(Context context, ArrayList<String> subscribedPublicList, ArrayList<String> subscribedPrivateList) {
        this.context = context;
        this.subscribedPublicList = subscribedPublicList;
        this.subscribedPrivateList = subscribedPrivateList;
    }

    @NonNull
    @Override
    public SubscribeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_subscribed, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscribeAdapter.MyViewHolder holder, int position) {
        if (position < subscribedPublicList.size()){
            String selectedName = subscribedPublicList.get(position);
            holder.name.setText(selectedName);
            holder.name.setOnClickListener(v -> {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("publishedMangaLists/publicLists/" + selectedName);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getKey();
                        if (value != null && value.equals(selectedName)){
                            openActivity(String.valueOf(holder.name.getText()), false);
                        } else {
                            Log.d("myTest", "test: " + value + " doesnt exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("myTest", "Failed to read value.", error.toException());
                    }
                });
            });
        } else {
            String selectedName = subscribedPrivateList.get(position - subscribedPublicList.size());
            holder.name.setText(selectedName);
            holder.name.setOnClickListener(v -> {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("publishedMangaLists/privateLists/" + selectedName);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getKey();
                        if (value != null && value.equals(selectedName)){
                            openActivity(String.valueOf(holder.name.getText()), false);
                        } else {
                            Log.d("myTest", "test: " + value + " doesnt exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("myTest", "Failed to read value.", error.toException());
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return subscribedPublicList.size() + subscribedPrivateList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.subscribedName);
        }
    }

    public void openActivity(String listName, boolean isPrivate) {
        Intent intent = new Intent(context, SubscribedListActivity.class);
        intent.putExtra("listName", listName);
        intent.putExtra("isPrivate", isPrivate);
        context.startActivity(intent);
    }
}