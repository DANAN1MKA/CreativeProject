package com.example.storage;

import android.content.ContentValues;
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

public class EditDataActivity extends AppCompatActivity {

    //база данных
    private DBHelper dbHelper;
    int id;

    private EditText ed_name;
    private EditText ed_login;
    private EditText ed_pass;
    private EditText ed_url;
    private EditText ed_description;

    private Button b_save;


    ///////////////////////////////////////////////////////////////////////CRYPT
    byte[] key = null;
    MYCryptography cryptDealer = null;
    ///////////////////////////////////////////////////////////////////////CRYPT

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
        id = arg.getInt("note_id");

        key = arg.getByteArray("key");

        cryptDealer = MYCryptography.getInstance(key);
        cryptDealer.init(MYCryptography.MODE_DECRYPT); //настраиваем на расшифровку
        ///////////////////////////////////////////////////////////////////////CRYPT

        dbHelper = new DBHelper(this);
        SQLiteDatabase db_note = dbHelper.getWritableDatabase();

        Cursor cursor = db_note.query(DBHelper.TABLE_DATA, null, DBHelper.DATA_ID + " = ?", new String[]{Integer.toString(id)}, null, null, null);
        int dataIndex = cursor.getColumnIndex(DBHelper.DATA_TEXT);
        cursor.moveToFirst();

        byte[] _data = cursor.getBlob(dataIndex);

        //декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные
        //декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные
        String data = cryptDealer.doFinal(_data); //получаем расшифрованную строку
        //декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные
        //декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные//декриптуем данные

        Note note = unpackData(id, data); //парсим полученное

        //выводим
        ed_name.setText(note.name);
        ed_login.setText(note.login);
        ed_pass.setText(note.pass);
        ed_url.setText(note.url);
        ed_description.setText(note.description);

        cursor.close();
        dbHelper.close();
    }

    void addNote(){
        if(ed_name.getText().length() > 0) {
            SQLiteDatabase db_notes = dbHelper.getWritableDatabase();
            ContentValues cv_update = new ContentValues();

            String data = packData(ed_name.getText().toString(),
                    ed_login.getText().toString(),
                    ed_pass.getText().toString(),
                    ed_url.getText().toString(),
                    ed_description.getText().toString());

            //киптографируем //киптографируем //киптографируем
            //киптографируем //киптографируем //киптографируем
            cryptDealer.init(MYCryptography.MODE_ENCRYPT);
            byte[] _data = cryptDealer.doFinal(data); //зашифровОЧКА
            //киптографируем //киптографируем //киптографируем
            //киптографируем //киптографируем //киптографируем

            cv_update.put(DBHelper.DATA_TEXT, _data);

            String where = DBHelper.DATA_ID + " = " + id;

            db_notes.update(DBHelper.TABLE_DATA, cv_update, where, null);

            dbHelper.close();

            Toast.makeText(this, "edited", Toast.LENGTH_SHORT).show();
        }
        this.finish();
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

    //распаковываем строку (полностью)
    private Note unpackData(int _id, String _data) {
        char[] splitdata = _data.toCharArray();

        Note unpData = new Note();

        unpData.id = _id;
        unpData.name = "";
        unpData.login = "";
        unpData.pass = "";
        unpData.url = "";
        unpData.description = "";

        //костыляем блин
        int crutch = 0; //необходим для понимания какую строку извлекаем
        int cur = 1; //cur указывает на начало подстроки
        int count = splitdata[0] ; //в count находится количество символов подстроки
        int i = cur; // i - позиция в главной строке
        while (i < splitdata.length) {
            char[] substring = new char[count]; //суда записываем подстроку

            for (i = cur; i < cur + count+1; i++) {
                if (i - cur < count) substring[i - cur] = splitdata[i];
                else {

                    switch (crutch){
                        case 0:
                            unpData.name += new String(substring);
                            break;
                        case 1:
                            unpData.login += new String(substring);
                            break;
                        case 2:
                            unpData.pass += new String(substring);
                            break;
                        case 3:
                            unpData.url += new String(substring);
                            break;
                        case 4:
                            unpData.description += new String(substring);
                            break;
                    }
                    crutch++;

                    if(i < splitdata.length) {
                        count = splitdata[i]; //подстрока полностью считана получаем длину следующей подстроки
                        cur = i + 1; //получаем позицию начала подстроки
                        substring = new char[count];
                    }
                }

            }
        }

        return unpData;
    } //похорошму данный метод будет находиться в шифровальной библиотеке а пока так
}
