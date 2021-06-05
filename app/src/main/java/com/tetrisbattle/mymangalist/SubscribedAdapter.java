package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubscribedAdapter extends RecyclerView.Adapter<SubscribedAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> subscribedPublicList;
    ArrayList<String> subscribedPrivateList;

    public SubscribedAdapter(Context context, ArrayList<String> subscribedPublicList, ArrayList<String> subscribedPrivateList) {
        this.context = context;
        this.subscribedPublicList = subscribedPublicList;
        this.subscribedPrivateList = subscribedPrivateList;
    }

    @NonNull
    @Override
    public SubscribedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_subscribed, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscribedAdapter.MyViewHolder holder, int position) {
        if (position == subscribedPublicList.size()+subscribedPrivateList.size()-1)
            holder.addNewSubscribeList.setVisibility(View.VISIBLE);

        holder.addNewSubscribeList.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new NewSubscribeFragment());
            fragmentTransaction.commit();
        });

        if (position < subscribedPublicList.size()){
            holder.name.setText(subscribedPublicList.get(position));

            holder.name.setOnClickListener(v -> {
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, new MangaListFragment(false, String.valueOf(holder.name.getText())));
                fragmentTransaction.commit();
            });
        } else {
            holder.name.setText(subscribedPrivateList.get(position - subscribedPublicList.size()));
//            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button2)); // to change background color

            holder.name.setOnClickListener(v -> {
                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, new MangaListFragment(true, String.valueOf(holder.name.getText())));
                fragmentTransaction.commit();
            });
        }
    }

    @Override
    public int getItemCount() {
        return subscribedPublicList.size() + subscribedPrivateList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView addNewSubscribeList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.subscribedName);
            addNewSubscribeList = itemView.findViewById(R.id.addNewSubscribeList);
        }
    }
}