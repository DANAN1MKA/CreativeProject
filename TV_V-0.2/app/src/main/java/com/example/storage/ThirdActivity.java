package com.example.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    //база данных
    DBHelper dbHelper;

    private EditText ed_name;
    private EditText ed_login;
    private EditText ed_pass;
    private EditText ed_url;
    private EditText ed_description;

    ///////////////////////////////////////////////////////////////////////CRYPT
    byte[] key = null;
    MYCryptography cryptDealer = null;
    ///////////////////////////////////////////////////////////////////////CRYPT

    private Button b_save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_layout);

        ed_name = findViewById(R.id.ed_addname);
        ed_login = findViewById(R.id.ed_addlogin);
        ed_pass = findViewById(R.id.ed_pass);
        ed_url = findViewById(R.id.ed_url);
        ed_description = findViewById(R.id.ed_description);

        b_save = findViewById(R.id.b_save);
        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        ///////////////////////////////////////////////////////////////////////CRYPT
        Bundle arg = getIntent().getExtras();

        key = arg.getByteArray("key");

        cryptDealer = MYCryptography.getInstance(key);
        cryptDealer.init(MYCryptography.MODE_ENCRYPT); //настраиваем на шифрование
        ///////////////////////////////////////////////////////////////////////CRYPT

        dbHelper = new DBHelper(this);
    }

    void addNote(){

        if(cryptDealer != null) {

            if (ed_name.getText().length() > 0) {
                SQLiteDatabase db_notes = dbHelper.getWritableDatabase();
                ContentValues cv_get_note = new ContentValues();

                String data = packData(ed_name.getText().toString(),
                        ed_login.getText().toString(),
                        ed_pass.getText().toString(),
                        ed_url.getText().toString(),
                        ed_description.getText().toString());

                //киптографируем //киптографируем //киптографируем
                //киптографируем //киптографируем //киптографируем
                byte[] _data = cryptDealer.doFinal(data);
                //киптографируем //киптографируем //киптографируем
                //киптографируем //киптографируем //киптографируем

                cv_get_note.put(DBHelper.DATA_TEXT, _data);

                db_notes.insert(DBHelper.TABLE_DATA, null, cv_get_note);

                dbHelper.close();

            }
            this.finish();

        }
    }

    //получаем ключ из БД
    private byte[] getKeyFromDataBase(){
        dbHelper = new DBHelper(this);
        SQLiteDatabase db_notes = dbHelper.getReadableDatabase();

        byte[] _key = null;

        Cursor cursor = db_notes.query(DBHelper.TABLE_USER, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int keyID = cursor.getColumnIndex(DBHelper.USER_KEY);
            _key = cursor.getBlob(keyID);
        }

        cursor.close();
        db_notes.close();
        dbHelper.close();

        return _key;
    }

    //упаковываем все поля в строку
    private String packData(String _name, String _login, String _pass, String _url, String _description) {
        String data = (char)_name.length() + _name +
                (char)_login.length() + _login +
                (char)_pass.length() + _pass +
                (char)_url.length() + _url +
                (char)_description.length() + _description;
        return data;
    }
}
