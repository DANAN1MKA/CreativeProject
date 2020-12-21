package com.example.storage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    //Следующие записи отвечают за вывод названий в ресайклвью
    private RecyclerView rv_notes;
    private RecyclerView.LayoutManager rv_lm_notes;
    private RecyclerView.Adapter vh_notes;

    private List<Note> notes; //здесь будут храниться тольько наименования записей (почему? причину придумай сам,  вникатель блин)
    //^^^^^^^^^^^храним и выводм^^^^^^^^^^^//


    ///////////////////////////////////////////////////////////////////////CRYPT
    byte[] key = null;
    MYCryptography cryptDealer = null;
    ///////////////////////////////////////////////////////////////////////CRYPT

    //база данных
    private DBHelper dbHelper;

    //интерфейс
    private Button b_add_note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        b_add_note = findViewById(R.id.b_additem);
        b_add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemClick();
            }
        });

        ///////////////////////////////////////////////////////////////////////CRYPT
        Bundle arg = getIntent().getExtras();

        key = arg.getByteArray("key");

        cryptDealer = MYCryptography.getInstance(key);
        cryptDealer.init(MYCryptography.MODE_DECRYPT); //настраиваем на расшифровку
        ///////////////////////////////////////////////////////////////////////CRYPT

        ///////////////////////////////////////Осторожно происходит работа с базой данных///////////////////////////////////////
        readFromDataBase();
        ///////////////////////////////////////Осторожно происходит работа с базой данных///////////////////////////////////////


        //**************************************Осторожно происходит работа с ресайклвью**************************************//
        rv_notes = findViewById(R.id.rv_records);
        rv_notes.setHasFixedSize(true);
        rv_lm_notes = new LinearLayoutManager(this);
        rv_notes.setLayoutManager(rv_lm_notes);

        vh_notes = new ItemsAdapter(notes, dbHelper, this, key);
        rv_notes.setAdapter(vh_notes);
        //**************************************Осторожно происходит работа с ресайклвью**************************************//
    }

    private void addItemClick() {
        //передаем ключ в следующее активити
        Intent intent = new Intent(this, ThirdActivity.class);
        Bundle arg = new Bundle();
        arg.putByteArray("key", key);
        intent.putExtras(arg);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

        updateNotesList();
    }

    private void readFromDataBase() {

        //Если шиифровалка настроена то делаем дела иначе нет
        if (cryptDealer != null) {

            ///////////////////////////////////////Осторожно происходит работа с базой данных///////////////////////////////////////
            dbHelper = new DBHelper(this);
            SQLiteDatabase db_notes = dbHelper.getReadableDatabase();

            notes = new ArrayList<>();

            Cursor cursor = db_notes.query(DBHelper.TABLE_DATA, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DBHelper.DATA_ID);
                int nameIndex = cursor.getColumnIndex(DBHelper.DATA_TEXT);
                //тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать
                //тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать
                do {

                    int id = cursor.getInt(idIndex);
                    byte _data[] = cursor.getBlob(nameIndex); //полуфабрикат не расшифрованный

                    String data = cryptDealer.doFinal(_data); //полуфабрикат расшифрованный

                    cryptDealer.init(MYCryptography.MODE_DECRYPT);//   :( только никому не рассказывайте

                    //получаем имя, распаковываем и запоминаем
                    notes.add(unpackData(id, data));

                } while (cursor.moveToNext());
                //тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать
                //тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать//тут надо дешифровать

            } else Toast.makeText(this, "database is empty", Toast.LENGTH_LONG).show();

            //закрываем подключение к бд
            cursor.close();
            dbHelper.close();
            ///////////////////////////////////////Осторожно происходит работа с базой данных///////////////////////////////////////
        }
    }

    public void updateNotesList(){
        if(cryptDealer != null) {
            if(cryptDealer.get_mode() == MYCryptography.MODE_ENCRYPT) cryptDealer.init(MYCryptography.MODE_DECRYPT);

            readFromDataBase();

            // отображаем считанное
            vh_notes = new ItemsAdapter(notes, dbHelper, this, key);
            rv_notes.setAdapter(vh_notes);
        } else Toast.makeText(this, "something went wrong (", Toast.LENGTH_SHORT).show();
    }

    //удаляем запись и обновляем список на экране
    public void deleteItem(int _id){
        for(Note n:notes) if(n.id == _id) {notes.remove(n); break;}
        // отображаем считанное
        vh_notes = new ItemsAdapter(notes, dbHelper, this, key);
        rv_notes.setAdapter(vh_notes);
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
    //получаем из строки только имя записи
    private Note getNameFromData(int _id, String _data){
        char[] data = _data.toCharArray();
        int count = data[0];
        char[] name = new char[count];
        for (int i = 1; i < count + 1; i++)
            name[i - 1] = data[i];

        return new Note(_id, new String(name), null, null, null, null);
    } //этот тоже
}
