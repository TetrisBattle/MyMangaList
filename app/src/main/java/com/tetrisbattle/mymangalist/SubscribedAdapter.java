package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubscribedAdapter extends RecyclerView.Adapter<SubscribedAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> subscribed;

    public SubscribedAdapter(Context context, ArrayList<String> subscribed) {
        this.context = context;
        this.subscribed = subscribed;
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
        if (position == subscribed.size()-1)
            holder.addNewSubscribeButton.setVisibility(View.VISIBLE);

        holder.name.setText(subscribed.get(position));

        holder.addNewSubscribeButton.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new NewSubscribeFragment());
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return subscribed.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        Button addNewSubscribeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.subscribedName);
            addNewSubscribeButton = itemView.findViewById(R.id.addNewSubscribeButton);
        }
    }
}