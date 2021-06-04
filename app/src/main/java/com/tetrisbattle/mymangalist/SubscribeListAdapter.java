package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
    ArrayList<String> publicListNames;
    ArrayList<String> privateListNames;
    DatabaseReference userRef;
    FirebaseDatabase db;
    SharedPreferences sharedPreferences;

    public SubscribeListAdapter(Context context, ArrayList<String> publicLists, ArrayList<String> privateLists,
                                ArrayList<String> publicListNames, ArrayList<String> privateListNames, DatabaseReference userRef) {
        this.context = context;
        this.publicLists = publicLists;
        this.privateLists = privateLists;
        this.publicListNames = publicListNames;
        this.privateListNames = privateListNames;
        this.userRef = userRef;
        db = FirebaseDatabase.getInstance();
        sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
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
        if (position == publicLists.size()+privateLists.size()-1)
            holder.subscribeFooter.setVisibility(View.VISIBLE);

        if (position < publicLists.size()){
            holder.mangaListName.setText(publicLists.get(position));

            for (int i=0; i<publicListNames.size(); i++) {
                if (publicLists.get(position).equals(publicListNames.get(i))) {
                    holder.mangaListName.setChecked(true);
                }
            }

            holder.mangaListName.setOnClickListener(v -> {
                if (holder.mangaListName.isChecked()) {
                    userRef.child("subscribed/publicLists/" + holder.mangaListName.getText()).setValue("null");
                } else {
                    userRef.child("subscribed/publicLists/" + holder.mangaListName.getText()).removeValue();
                }
            });
        } else {
            holder.mangaListName.setText(privateLists.get(position - publicLists.size()));
            holder.lockIcon.setVisibility(View.VISIBLE);

            for (int i=0; i<privateListNames.size(); i++) {
                if (privateLists.get(position - publicLists.size()).equals(privateListNames.get(i))) {
                    holder.mangaListName.setChecked(true);
                }
            }

            holder.mangaListName.setOnClickListener(v -> {
                if (holder.mangaListName.isChecked()) {
                    if (holder.passwordLayout.getVisibility() == View.GONE) {
                        holder.mangaListName.setChecked(false);
                        holder.passwordLayout.setVisibility(View.VISIBLE);

                        holder.passwordOkButton.setOnClickListener(v2 -> {
                            DatabaseReference passwordRef = db.getReference("publishedMangaLists/privateLists/" + holder.mangaListName.getText() + "/password");

                            passwordRef.child("").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String passwordInput = String.valueOf(holder.passwordEditText.getText());
                                    String password = String.valueOf(snapshot.getValue());
                                    if(passwordInput.equals(password)) {
                                        holder.passwordLayout.setVisibility(View.GONE);
                                        holder.mangaListName.setChecked(true);
                                        userRef.child("subscribed/privateLists/" + holder.mangaListName.getText()).setValue("password");
                                    } else {
                                        Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("myTest", "Failed to read value.", error.toException());
                                }
                            });
                        });
                    } else {
                        holder.mangaListName.setChecked(false);
                        holder.passwordLayout.setVisibility(View.GONE);
                    }
                } else {
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
        EditText passwordEditText;
        ConstraintLayout passwordLayout;
        Button passwordOkButton;
        View subscribeFooter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mangaListName = itemView.findViewById(R.id.mangaListName);
            lockIcon = itemView.findViewById(R.id.lockIcon);
            passwordEditText = itemView.findViewById(R.id.setPassword);
            passwordLayout = itemView.findViewById(R.id.passwordView);
            passwordOkButton = itemView.findViewById(R.id.sendPassword);
            subscribeFooter = itemView.findViewById(R.id.subscribeFooter);
        }
    }
}
