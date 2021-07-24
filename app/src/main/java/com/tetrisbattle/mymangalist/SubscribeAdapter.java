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
            holder.name.setText(subscribedPublicList.get(position));

            holder.name.setOnClickListener(v -> {
//                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frameLayout, new MangaListFragment(false, String.valueOf(holder.name.getText())));
//                fragmentTransaction.commit();

                openActivity(String.valueOf(holder.name.getText()), false);
            });
        } else {
            holder.name.setText(subscribedPrivateList.get(position - subscribedPublicList.size()));
//            holder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button2)); // to change background color

            holder.name.setOnClickListener(v -> {
//                FragmentTransaction fragmentTransaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frameLayout, new MangaListFragment(true, String.valueOf(holder.name.getText())));
//                fragmentTransaction.commit();

                openActivity(String.valueOf(holder.name.getText()), true);
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