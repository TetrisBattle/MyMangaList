package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubscribeListAdapter extends RecyclerView.Adapter<SubscribeListAdapter.MyViewHolder> {

    Context context;
    String[] data;

    public SubscribeListAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
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
        holder.mangaListName.setText(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox mangaListName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mangaListName = itemView.findViewById(R.id.mangaListName);
        }
    }
}
