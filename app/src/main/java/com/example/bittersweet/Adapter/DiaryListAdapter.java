package com.example.bittersweet.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bittersweet.Model.BloodGlucose;
import com.example.bittersweet.R;

import java.util.ArrayList;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {

    private ArrayList<BloodGlucose> mrecords;
    private LayoutInflater minflater;
    private ItemClickListener mclickListener;

    // data is passed into the constructor
    DiaryListAdapter(Context context, ArrayList<BloodGlucose> records){
        this.minflater = LayoutInflater.from(context);
        this.mrecords = records;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = minflater.inflate(R.layout.activity_diary_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull DiaryListAdapter.ViewHolder holder, int position) {
        String inputDate = mrecords.get(position).getInputDate();
        holder.myTextView.setText(inputDate);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mrecords.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.reocrd_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mclickListener != null) mclickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    BloodGlucose getItem(int position) {
        return mrecords.get(position);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mclickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}


