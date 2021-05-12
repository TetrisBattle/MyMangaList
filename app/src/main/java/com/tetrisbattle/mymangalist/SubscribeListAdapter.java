package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubscribeListAdapter extends RecyclerView.Adapter<SubscribeListAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> publicLists;
    ArrayList<String> privateLists;

    public SubscribeListAdapter(Context context, ArrayList<String> publicLists, ArrayList<String> privateLists) {
        this.context = context;
        this.publicLists = publicLists;
        this.privateLists = privateLists;
    }

    @NonNull
    @Override
    public SubscribeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_subscribe, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position < publicLists.size())
        holder.mangaListName.setText(publicLists.get(position));
        else {
            holder.mangaListName.setText(privateLists.get(position - publicLists.size()));
            holder.lockIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return publicLists.size() + privateLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox mangaListName;
        ImageView lockIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mangaListName = itemView.findViewById(R.id.mangaListName);
            lockIcon = itemView.findViewById(R.id.lockIcon);
        }
    }
}
