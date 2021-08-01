package com.example.recordy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private File[] allFiles;
    private TimeFormatter timeFormatter;

    private OnClickListener onClickListener;

    public RecyclerViewAdapter(File[] allFiles , OnClickListener onClickListener) {
        this.allFiles = allFiles;
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_item ,parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( RecyclerViewAdapter.ViewHolder holder, int position) {
        timeFormatter = new TimeFormatter();

        holder.itemTitle.setText(allFiles[position].getName());
        holder.itemDate.setText(timeFormatter.getTime(allFiles[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemImageView;
        TextView itemTitle;
        TextView itemDate;
        public ViewHolder( View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.file_item_img);
            itemTitle = itemView.findViewById(R.id.file_item_name);
            itemDate = itemView.findViewById(R.id.file_itme_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickListener.onItemClickListener( allFiles[getAdapterPosition()]  , getAdapterPosition() );
        }
    }
    public interface OnClickListener{
        void onItemClickListener(File itemFile , int itemPosition);
    };
}

