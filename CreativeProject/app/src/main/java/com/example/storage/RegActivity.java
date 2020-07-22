package com.example.storage;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class RegActivity extends AppCompatActivity {

    private EditText tv_pass;
    private EditText tv_repeat;

    private Button b_create;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        tv_pass = findViewById(R.id.ed_userpass);
        tv_repeat = findViewById(R.id.ed_userpass_repeat);
        b_create = findViewById(R.id.b_create);

        b_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createButtonClick();
            }
        });
    }

    private void createButtonClick() {
        String pass = tv_pass.getText().toString();
        String repeat = tv_repeat.getText().toString();
        if(pass.equals(repeat)){

            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db_user = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            //генерируем ключ и сразу шифруем
            byte[] encryptedKey = MYCryptography.encryptKey(MYCryptography.generateKey(), pass);
            contentValues.put(DBHelper.USER_KEY, encryptedKey);

            //получем хеш пароля
            contentValues.put(DBHelper.USER_HASH, MYCryptography.getHashFromPass(pass));


            db_user.insert(DBHelper.TABLE_USER, null, contentValues);
            db_user.close();
            dbHelper.close();
        } else Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_LONG).show();

        this.finish();
    }

}
