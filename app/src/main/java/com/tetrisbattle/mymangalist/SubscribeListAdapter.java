package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SubscribeListAdapter extends RecyclerView.Adapter<SubscribeListAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> publicLists;
    ArrayList<String> privateLists;

    FirebaseDatabase db;
    DatabaseReference userRef;

    public SubscribeListAdapter(Context context, ArrayList<String> publicLists, ArrayList<String> privateLists, DatabaseReference userRef) {
        this.context = context;
        this.publicLists = publicLists;
        this.privateLists = privateLists;
        this.userRef = userRef;

        db = FirebaseDatabase.getInstance();
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
        if (position < publicLists.size()){
            holder.mangaListName.setText(publicLists.get(position));

//            Log.d("myTest", "size: " + publicListNames.size());
//            for (int i=0; i<publicLists.size(); i++) {
//                Log.d("myTest", "test: " + "?");
//                if (publicListNames.get(i).equals(publicLists.get(i))) {
//                    Log.d("myTest", "test: " + "yes");
//                    holder.mangaListName.setChecked(true);
//                }
//            }

            holder.mangaListName.setOnClickListener(v -> {
                if (holder.mangaListName.isChecked()) {
                    Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();
                    userRef.child("subscribed/publicLists/" + holder.mangaListName.getText()).setValue("null");
                } else {
                    Toast.makeText(context, "no", Toast.LENGTH_SHORT).show();
                    userRef.child("subscribed/publicLists/" + holder.mangaListName.getText()).removeValue();
                }
            });
        } else {
            holder.mangaListName.setText(privateLists.get(position - publicLists.size()));
            holder.lockIcon.setVisibility(View.VISIBLE);

//            for (int i=0; i<privateListNames.size(); i++) {
//                if (privateListNames.get(i-publicLists.size()).equals(privateLists.get(i-publicLists.size()))) {
//                    holder.mangaListName.setChecked(true);
//                }
//            }

            holder.mangaListName.setOnClickListener(v -> {
                if (holder.mangaListName.isChecked()) {
                    Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();
                    userRef.child("subscribed/privateLists/" + holder.mangaListName.getText()).setValue("password");
                } else {
                    Toast.makeText(context, "no", Toast.LENGTH_SHORT).show();
                    userRef.child("subscribed/privateLists/" + holder.mangaListName.getText()).removeValue();
                }
            });
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
