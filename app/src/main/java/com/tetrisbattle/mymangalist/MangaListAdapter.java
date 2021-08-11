package com.tetrisbattle.mymangalist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MangaListAdapter extends RecyclerView.Adapter<MangaListAdapter.MyViewHolder> {

    Context context;
    List<MyManga> data;
    MyDatabaseHelper myDatabaseHelper;
    String myTable;
    ConstraintLayout background;

    InputMethodManager inputMethodManager;
    AlertDialog.Builder deleteDialog;

    int selectedId;
    String selectedName;
    String selectedChapter;
    String selectedUrl;

    public MangaListAdapter(Context context, List<MyManga> myManga, MyDatabaseHelper myDatabaseHelper, String myTable, ConstraintLayout background) {
        this.context = context;
        this.data = myManga;
        this.myDatabaseHelper = myDatabaseHelper;
        this.myTable = myTable;
        this.background = background;
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        deleteDialog = new AlertDialog.Builder(context);
    }

    public MangaListAdapter(Context context, List<MyManga> myManga, String myTable) {
        this.context = context;
        this.data = myManga;
        this.myTable = myTable;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.cardview_manga, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (!myTable.equals("subscribedList")) {
            if (position != data.size()-1) holder.footer.setVisibility(View.GONE);
            else holder.footer.setVisibility(View.VISIBLE);

            holder.id.setText(String.valueOf(position+1));
            holder.name.setText(data.get(position).name);
            holder.chapter.setText(data.get(position).chapter);

            holder.id.setOnClickListener(v -> {
                background.callOnClick();
//            ClipboardManager clipboardManager;
//            clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
//
//            ClipData clipData;
//            clipData = ClipData.newPlainText("Name", selectedName);
//            clipboardManager.setPrimaryClip(clipData);
            });

            holder.name.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    selectedId = data.get(position).id;
                    selectedName = String.valueOf(holder.name.getText());
                } else {
                    if (!String.valueOf(holder.name.getText()).equals(selectedName)){
                        myDatabaseHelper.updateName(String.valueOf(holder.name.getText()), selectedId);

//                data.get(position).name = String.valueOf(holder.name.getText());

                        data = myDatabaseHelper.getMyMangaList();
//                    notifyDataSetChanged();
//                    notifyItemChanged(position); // remake item at pos
                    }
                }
            });

            holder.name.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    holder.name.clearFocus();
                    return true;
                }
                return false;
            });

            holder.name.setOnLongClickListener(v -> {
                background.callOnClick();
                selectedId = data.get(position).id;
                selectedName = String.valueOf(holder.name.getText());
                showPopupMenu(v, holder, myDatabaseHelper.getUrl(selectedId));
                return true;
            });

            holder.chapter.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    selectedId = data.get(position).id;
                    selectedChapter = String.valueOf(holder.chapter.getText());
                } else {
                    if (!String.valueOf(holder.chapter.getText()).equals(selectedChapter)){
                        myDatabaseHelper.updateChapter(String.valueOf(holder.chapter.getText()), selectedId);
                    }
                }
            });

            holder.chapter.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    holder.chapter.clearFocus();
                    return true;
                }
                return false;
            });

            holder.changeUrl.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    if (!String.valueOf(holder.changeUrl.getText()).equals(selectedUrl)){
                        myDatabaseHelper.updateUrl(String.valueOf(holder.changeUrl.getText()), selectedId);
                    }
                    holder.changeUrl.setVisibility(View.GONE);
                }
            });

            holder.changeUrl.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    holder.changeUrl.clearFocus();
                    return true;
                }
                return false;
            });
        } else {
            if (position != data.size()-1) holder.footer.setVisibility(View.GONE);
            else holder.footer.setVisibility(View.VISIBLE);

            holder.id.setText(String.valueOf(position+1));
            holder.name.setText(data.get(position).name);
            holder.chapter.setText(data.get(position).chapter);

            holder.name.setInputType(InputType.TYPE_NULL);
            holder.chapter.setEnabled(false);

            holder.name.setOnLongClickListener(v -> {
                selectedId = data.get(position).id;
                selectedName = String.valueOf(holder.name.getText());
                showPopupMenuSubscribedList(v);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        EditText name;
        EditText chapter;
        EditText changeUrl;
        View footer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.counter);
            name = itemView.findViewById(R.id.listName);
            chapter = itemView.findViewById(R.id.chapter);
            changeUrl = itemView.findViewById(R.id.changeUrl);
            footer = itemView.findViewById(R.id.footer);
        }
    }

    public void setMangaList(List<MyManga> data) {
        this.data = data;
    }

    public void showPopupMenu(View v, MyViewHolder holder, String url) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.popupCopy) {
                ClipboardManager clipboardManager;
                clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

                ClipData clipData;
                clipData = ClipData.newPlainText("Name", selectedName);
                clipboardManager.setPrimaryClip(clipData);
//                Toast.makeText(context, "copy: " + selectedName, Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.popupGoToLink) {
                if (url.equals("")) {
                    Toast.makeText(context, "Link to website is not provided", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Intent browse = new Intent( Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity( browse );
                    } catch (Exception exception) {
                        Toast.makeText(context, "Website is not available", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            } else if (item.getItemId() == R.id.popupChangeLink) {
                holder.changeUrl.setVisibility(View.VISIBLE);
                selectedUrl = myDatabaseHelper.getUrl(selectedId);
                holder.changeUrl.setText(selectedUrl);
                holder.changeUrl.requestFocus();
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0); // show keyboard
                return true;
            } else if (item.getItemId() == R.id.popupDelete) {
                deleteDialog.setTitle("DELETE")
                        .setMessage("Are you sure you want to delete " + selectedName + " from the list?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            myDatabaseHelper.deleteData(selectedId);
                            data = myDatabaseHelper.getMyMangaList();
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("No", (dialog, which) -> {});
                deleteDialog.show();
                return true;
            } else {
                return false;
            }
        });
    }

    public void showPopupMenuSubscribedList(View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_subscribe, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.popupCopy) {
                ClipboardManager clipboardManager;
                clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

                ClipData clipData;
                clipData = ClipData.newPlainText("Name", selectedName);
                clipboardManager.setPrimaryClip(clipData);
//                Toast.makeText(context, "copy: " + selectedName, Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.popupAddToPlanToRead) {
                Log.d("myTest", "test: " + "popupAddToPlanToRead");
                return true;
            } else {
                return false;
            }
        });
    }
}
