package com.example.storage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import java.util.List;

public class NoteActions {
    //база данных
    private DBHelper dbHelper;
    private SecondActivity secondActivity;

    List<Note> data;

    public NoteActions(DBHelper _dbHelper, List<Note> _data, SecondActivity _secondActivity) {
        dbHelper = _dbHelper;
        data = _data;
        secondActivity = _secondActivity;
    }

    public void deleteItem(int _id) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String[] selectionArg = {Integer.toString(_id)};
        database.delete(DBHelper.TABLE_DATA, DBHelper.DATA_ID + " LIKE ?", selectionArg);
        secondActivity.deleteItem(_id);
        dbHelper.close();
    }

    public void editItem(int _id, byte[] key) {
        Intent intent = new Intent(secondActivity, EditDataActivity.class);
        intent.putExtra("note_id", _id);
        secondActivity.startActivity(intent);

    }

    public void copyLogin(int _id){
        ClipboardManager clipboard = (ClipboardManager)secondActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clop = ClipData.newPlainText("", "");
        for (Note n:data) if(n.id == _id) {clop = ClipData.newPlainText("", n.login); break;}
        clipboard.setPrimaryClip(clop);
    }

    public void copyPass(int _id){
        ClipboardManager clipboard = (ClipboardManager)secondActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clop = ClipData.newPlainText("", "");
        for (Note n:data) if(n.id == _id){ clop = ClipData.newPlainText("", n.pass); break;}
        clipboard.setPrimaryClip(clop);
    }

    public void copyUrl(int _id){
        ClipboardManager clipboard = (ClipboardManager)secondActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clop = ClipData.newPlainText("", "");
        for (Note n:data) if(n.id == _id) {clop = ClipData.newPlainText("", n.url); break;}
        clipboard.setPrimaryClip(clop);
    }
}
