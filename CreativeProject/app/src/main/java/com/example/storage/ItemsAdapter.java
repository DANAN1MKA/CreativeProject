package com.example.storage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    //***************тут можно менять***************//
    private List<Note> data;
    private NoteActions noteActions;
    private byte[] key;
    //***************тут можно менять***************//

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //***************тут можно менять***************//
        public TextView tv_itemname;
        public Spinner sp_action;

        public int note_id;
        public byte[] key;

        public NoteActions noteActions;
        //***************тут можно менять***************//

        public ViewHolder(View view)
        {
            super(view);

            //***************тут можно менять***************//
            tv_itemname = view.findViewById(R.id.tv_itemname);
            sp_action = view.findViewById(R.id.sp_action);
            //***************тут можно менять***************//
            sp_action.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0: break;
                        case 1:
                            noteActions.copyLogin(note_id);
                            Toast.makeText(view.getContext(), "username copied", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            noteActions.copyPass(note_id);
                            Toast.makeText(view.getContext(), "pass copied", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            noteActions.copyUrl(note_id);
                            Toast.makeText(view.getContext(), "URL copied", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            noteActions.editItem(note_id, key);
                            //Toast.makeText(view.getContext(), "edited", Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            noteActions.deleteItem(note_id);
                            Toast.makeText(view.getContext(), "deleted", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            /*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //***************тут можно менять***************
                    //Toast.makeText(this, tv_itemname.getText().toString(), Toast.LENGTH_SHORT).show();
                    //***************тут можно менять***************
                }
            });*/
        }

    }
    public ItemsAdapter(List<Note> _data, DBHelper _dbHelper, SecondActivity _secondActivity, byte[] _key)
    {
        //***************тут можно менять***************//
        key = _key;
        data = _data;
        noteActions = new NoteActions(_dbHelper, _data, _secondActivity);
        //secondActivity = _secondActivity;
        // ***************тут можно менять***************//
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cl_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //***************тут можно менять***************//
        //выводим название каждой записи
        holder.tv_itemname.setText(data.get(position).name);
        holder.note_id = data.get(position).id;
        holder.noteActions = noteActions;
        holder.key = key;
        //***************тут можно менять***************//
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

}
