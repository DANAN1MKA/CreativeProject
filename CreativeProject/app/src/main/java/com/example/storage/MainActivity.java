package com.example.storage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private EditText ed_userpass;
    private Button b_open;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ed_userpass = findViewById(R.id.et_userpass);
        b_open = findViewById(R.id.b_autconfrim);

        if (!isUserExists()) {
            Intent intent = new Intent(this, RegActivity.class);
            startActivity(intent);
        }

        b_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDataBase();
            }
        });


    }

    private boolean isUserExists() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db_user = dbHelper.getReadableDatabase();

        Cursor cursor = db_user.query(DBHelper.TABLE_USER, null, null, null, null, null, null);
        boolean exists = cursor.moveToFirst();

        cursor.close();
        db_user.close();
        dbHelper.close();

        return exists;
    }

    private void openDataBase() {
        if (!isUserExists()) {
            Intent intent = new Intent(this, RegActivity.class);
            startActivity(intent);
        }
        else {

            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db_user = dbHelper.getReadableDatabase();

            Cursor cursor = db_user.query(DBHelper.TABLE_USER, null, null, null, null, null, null);

            if(cursor.moveToFirst()){

                int hashID = cursor.getColumnIndex(DBHelper.USER_HASH);
                int keyID = cursor.getColumnIndex(DBHelper.USER_KEY);

                String userPass = ed_userpass.getText().toString();

                byte[] hashDB = cursor.getBlob(hashID);
                byte[] hashUser = MYCryptography.getHashFromPass(userPass);

                for(int i = 0; i < hashDB.length; i++){
                    if(hashDB[i] != hashUser[i]){
                        Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //получаем ключ и расшифровываем его
                byte[] key = MYCryptography.decryptKey(cursor.getBlob(keyID), userPass);

                //передаем ключ в следующее активити
                Intent intent = new Intent(this, SecondActivity.class);
                Bundle arg = new Bundle();
                arg.putByteArray("key", key);
                intent.putExtras(arg);
                startActivity(intent);

                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            db_user.close();
            dbHelper.close();
        }
    }
}

