package com.biketelemetry.gui;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.biketelemetry.R;

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    private TextView filename;
    private TextView size;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        filename = itemView.findViewById(R.id.text_filename);
        size = itemView.findViewById(R.id.text_size);
        itemView.setOnCreateContextMenuListener(this);
    }

    public TextView getFilenameView() {
        return filename;
    }

    public TextView getSizeView() {
        return size;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, R.id.ctx_copy,
                Menu.NONE, R.string.ctx_copy);
        menu.add(Menu.NONE, R.id.ctx_delete,
                Menu.NONE, R.string.ctx_delete);
    }
}
