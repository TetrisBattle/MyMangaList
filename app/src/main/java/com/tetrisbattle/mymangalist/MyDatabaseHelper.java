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

    public MyDatabaseHelper(Context context, String myTable) {
        super(context, "myAnimeListDatabase", null, 1);
        this.myTable = myTable;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "myAnimeListTable" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableX" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableS" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableA" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableB" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableC" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableD" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
        db.execSQL("CREATE TABLE " + "myAnimeListTableE" + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + name + " TEXT, " + chapter + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + myTable);
    }

    public void setTable(String myTable) {
        this.myTable = myTable;
    }

    public void insertData(String newMangaName, String newMangaChapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(name, newMangaName);
        contentValues.put(chapter, newMangaChapter);
        db.insert(myTable, null, contentValues);
    }

    public void updateData(String newMangaName, String newMangaChapter, int selectedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(name, newMangaName);
        contentValues.put(chapter, newMangaChapter);
        db.update(myTable, contentValues, "id = ?", new String[] {String.valueOf(selectedId)});
    }

    public void deleteData(int selectedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(myTable, "id = ?", new String[] { String.valueOf(selectedId) });
    }

//    public void deleteData(MyMangaListModel pos) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String queryString = "DELETE FROM myAnimeListTable WHERE id = " + pos;
//        db.execSQL(queryString);
//    }

    // create list of objects from database
    public List<MyManga> getMyMangaList() {
        ArrayList<MyManga> myMangaList = new ArrayList<>();
        String queryString = "SELECT * FROM " + myTable + " ORDER BY " + name;
        SQLiteDatabase db = this.getReadableDatabase();
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
