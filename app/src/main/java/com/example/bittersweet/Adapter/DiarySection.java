package com.example.bittersweet.Adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.bittersweet.Model.Record;
import com.example.bittersweet.R;

import io.github.luizgrp.sectionedrecyclerviewadapter.*;

public class DiarySection extends Section{

    private String header;
    private List<Record> itemList;

    // call constructor with layout resources for this Section header and items
    public DiarySection(String header, List<Record> itemList) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.activity_diary_item_bglucose)
                .headerResourceId(R.layout.activity_diary_header)
                .build());
        this.header   = header;
        this.itemList = itemList;
    }

    @Override
    public int getContentItemsTotal() {
        return itemList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        itemHolder.time.setText(itemList.get(position).getTime());
        // display exclamation mark if blood glucose is too low or too high
        double bloodGlucose = itemList.get(position).getBloodGlucose();
        if (bloodGlucose <= 4.0 || bloodGlucose >= 13.0) {
            itemHolder.alert.setImageResource(R.drawable.alert);
        } else {itemHolder.alert.setVisibility(View.GONE);}
        itemHolder.bloodGlucose.setText(String.valueOf(bloodGlucose));
        ArrayList<String> labels = itemList.get(position).getLabels();
        // switch image according to saved label, set invisible for null
        if (labels.get(0) == null || labels.get(0).isEmpty()) {itemHolder.meal.setVisibility(View.INVISIBLE);}
        else if (labels.get(0).equals("Breakfast")) {
            itemHolder.meal.setImageResource(R.drawable.breakfast);
            itemHolder.mealText.setText(labels.get(0));}
        else if (labels.get(0).equals("Lunch"))     {
            itemHolder.meal.setImageResource(R.drawable.lunch);
            itemHolder.mealText.setText(labels.get(0));}
        else if (labels.get(0).equals("Dinner"))    {
            itemHolder.meal.setImageResource(R.drawable.dinner);
            itemHolder.mealText.setText(labels.get(0));}

        if (labels.get(1) == null || labels.get(1).isEmpty()) {itemHolder.mealTime.setVisibility(View.INVISIBLE);}
        else if (labels.get(1).equals("Before meal")) {
            itemHolder.mealTime.setImageResource(R.drawable.before_meal);
            itemHolder.mealTimeText.setText(labels.get(1));}
        else if (labels.get(1).equals("After meal"))  {
            itemHolder.mealTime.setImageResource(R.drawable.after_meal);
            itemHolder.mealTimeText.setText(labels.get(1));}

        itemHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.header.setText(header);
    }

    /**
     * ItemViewHolder and HeaderViewHolder
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView time, bloodGlucose, mealText, mealTimeText;
        private final ImageView meal, mealTime, alert;
        private final Button deleteButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.record_time);
            bloodGlucose = (TextView) itemView.findViewById(R.id.record_glucose);
            alert = (ImageView) itemView.findViewById(R.id.record_glucose_alert);
            meal = (ImageView) itemView.findViewById(R.id.record_label_meal);
            mealText = (TextView) itemView.findViewById(R.id.record_label_meal_text);
            mealTime = (ImageView) itemView.findViewById(R.id.record_label_mealtime);
            mealTimeText = (TextView) itemView.findViewById(R.id.record_label_mealtime_text);

            deleteButton = (Button) itemView.findViewById(R.id.record_trash_button);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView header;
        public HeaderViewHolder(View headerView) {
            super(headerView);
            header = (TextView) headerView.findViewById(R.id.date_header);
        }
    }

}
