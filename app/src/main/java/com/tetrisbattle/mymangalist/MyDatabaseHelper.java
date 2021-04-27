package com.tetrisbattle.mymangalist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    String myTable;
    String name = "name";
    String chapter = "chapter";
    String url = "url";

    public MyDatabaseHelper(Context context, String myTable) {
        super(context, "myAnimeListDatabase", null, 1);
        this.myTable = myTable;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "myAnimeListTable" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableX" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableS" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableA" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableB" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableC" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableD" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableE" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT, " + url + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + myTable);
    }

    public void setTable(String myTable) {
        this.myTable = myTable;
    }

    public void insertData(String newName, String newChapter, String newUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(name, newName);
        contentValues.put(chapter, newChapter);
        contentValues.put(url, newUrl);
        db.insert(myTable, null, contentValues);
    }

    public void updateName(String newName, int selectedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(name, newName);
        db.update(myTable, contentValues, "id = ?", new String[] {String.valueOf(selectedId)});
    }

    public void updateChapter(String newChapter, int selectedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(chapter, newChapter);
        db.update(myTable, contentValues, "id = ?", new String[] {String.valueOf(selectedId)});
    }

    public void updateUrl(String newUrl, int selectedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(url, newUrl);
        db.update(myTable, contentValues, "id = ?", new String[] {String.valueOf(selectedId)});
    }

    public void deleteData(int selectedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(myTable, "id = ?", new String[] { String.valueOf(selectedId) });
    }

    public String getUrl(int selectedId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT url FROM " + myTable + " WHERE id = " + selectedId;

        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

//    public void deleteData(MyMangaListModel pos) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String queryString = "DELETE FROM myAnimeListTable WHERE id = " + pos;
//        db.execSQL(queryString);
//    }

    // create list of objects from database
    public List<MyManga> getMyMangaList() {
        ArrayList<MyManga> myMangaList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + myTable + " ORDER BY " + name;
        Cursor cursor = db.rawQuery(queryString, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String mangaName = cursor.getString(1);
            String mangaChapter = cursor.getString(2);

            MyManga myManga = new MyManga(id, mangaName, mangaChapter);
            myMangaList.add(myManga);
        }

        cursor.close();
        db.close();
        return myMangaList;
    }
}
