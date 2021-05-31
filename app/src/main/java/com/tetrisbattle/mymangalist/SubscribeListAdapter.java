package com.tetrisbattle.mymangalist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    ArrayList<String> publicListNames;
    ArrayList<String> privateListNames;
    DatabaseReference userRef;

    public SubscribeListAdapter(Context context, ArrayList<String> publicLists, ArrayList<String> privateLists,
                                ArrayList<String> publicListNames, ArrayList<String> privateListNames, DatabaseReference userRef) {
        this.context = context;
        this.publicLists = publicLists;
        this.privateLists = privateLists;
        this.publicListNames = publicListNames;
        this.privateListNames = privateListNames;
        this.userRef = userRef;
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

            holder.mangaListName.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (holder.mangaListName.isChecked()) {
                    holder.mangaListName.setChecked(false);
                    holder.passwordView.setVisibility(View.VISIBLE);

                    holder.sendPassword.setOnClickListener(v -> {
                        Toast.makeText(context, "check the password", Toast.LENGTH_SHORT).show();
                    });
                    
                    userRef.child("subscribed/privateLists/" + holder.mangaListName.getText()).setValue("password");
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
        EditText setPassword;
        ConstraintLayout passwordView;
        Button sendPassword;
        View subscribeFooter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mangaListName = itemView.findViewById(R.id.mangaListName);
            lockIcon = itemView.findViewById(R.id.lockIcon);
            setPassword = itemView.findViewById(R.id.setPassword);
            passwordView = itemView.findViewById(R.id.passwordView);
            sendPassword = itemView.findViewById(R.id.sendPassword);
            subscribeFooter = itemView.findViewById(R.id.subscribeFooter);
        }
    }
}
