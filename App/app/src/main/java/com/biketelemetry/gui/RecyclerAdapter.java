package com.biketelemetry.gui;

import org.apache.commons.io.FileUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.biketelemetry.R;
import com.biketelemetry.data.TelemetryFileListEntry;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<TelemetryFileListEntry> values;
    private int selectedIndex;

    public RecyclerAdapter(List<TelemetryFileListEntry> values) {
        this.values = values;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TelemetryFileListEntry entry = values.get(position);
        holder.getFilenameView().setText(entry.getFilename());
        holder.getSizeView().setText(FileUtils.byteCountToDisplaySize(entry.getSize()));

        holder.itemView.setOnLongClickListener(v -> {
            setSelectedIndex(holder.getLayoutPosition());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
