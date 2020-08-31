package com.example.bittersweet.Adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.bittersweet.Model.Record;
import com.example.bittersweet.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DiaryAdapter extends FirestoreRecyclerAdapter<Record, RecyclerView.ViewHolder> {

    private static final String TAG = "DiaryAdapterDebug";
    private static final int TYPE_GLUCOSE = 1;
    private static final int TYPE_MEDICATION = 2;

    private onRecordClickListener listener;
    private onEditClickListener editClickListener;
    private String format;
    private Resources resources;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DiaryAdapter(@NonNull FirestoreRecyclerOptions<Record> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Record model) {

        DateFormat dateFormat = new SimpleDateFormat(format, Locale.UK);
        String dateTimeString = dateFormat.format(model.getDateTime());
        ((ItemViewHolder) holder).dateTime.setText(dateTimeString);
        if (model.getBloodGlucose() != 0){
            ((ItemViewHolder) holder).glucose.setText(String.valueOf(model.getBloodGlucose()));
            if (model.getBloodGlucose() >= 13) {
                ((ItemViewHolder) holder).cardTitle.setBackgroundColor(resources.getColor(R.color.red));
                ((ItemViewHolder) holder).ketoneLayout.setVisibility(View.VISIBLE);
                boolean state = model.getKetone().getKetoneState();
                double number = model.getKetone().getKetoneNumber();
                ((ItemViewHolder) holder).ketoneState.setText(String.valueOf(state));
                ((ItemViewHolder) holder).ketoneNumber.setText(String.valueOf(number));
            } else {
                ((ItemViewHolder) holder).cardTitle.setBackgroundColor(resources.getColor(R.color.light_yellow));
                ((ItemViewHolder) holder).ketoneLayout.setVisibility(View.GONE);
            }
        }
        String inputNotes = model.getNotes();
        if (inputNotes != null && !inputNotes.isEmpty()) {
            ((ItemViewHolder) holder).note.setVisibility(View.VISIBLE);
            ((ItemViewHolder) holder).note.setText(model.getNotes());
        } else {
            ((ItemViewHolder) holder).note.setVisibility(View.GONE);
        }

    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        format = parent.getContext().getResources().getString(R.string.date_to_format);
        resources = parent.getContext().getResources();
        ViewHolder vh;

        if (viewType == TYPE_GLUCOSE){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_diary_card_bglucose,parent,false);
            vh = new ItemViewHolder(v);
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_diary_card_medication,parent,false);
            vh = new ItemViewHolderMed(v);
            return vh;
        }
    }

    class ItemViewHolder extends ViewHolder {
        TextView dateTime, glucose;
        TextView ketoneState, ketoneNumber, note;
        LinearLayout ketoneLayout, cardTitle;
        ExpandableLayout glucoseExpan;
        Button editButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            dateTime = (TextView)itemView.findViewById(R.id.item_date);
            glucose = (TextView) itemView.findViewById(R.id.item_glucose);
            ketoneLayout = (LinearLayout) itemView.findViewById(R.id.item_ketone_layout);
            ketoneState = (TextView) itemView.findViewById(R.id.item_ketone_state);
            ketoneNumber = (TextView) itemView.findViewById(R.id.item_ketone_number);
            note = (TextView) itemView.findViewById(R.id.item_glucose_note);
            glucoseExpan = (ExpandableLayout) itemView.findViewById(R.id.item_glucose_expandable);
            cardTitle = (LinearLayout) itemView.findViewById(R.id.card_title);
            editButton = (Button) itemView.findViewById(R.id.diary_edit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, glucoseExpan);
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        editClickListener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    class ItemViewHolderMed extends ViewHolder {
        public ItemViewHolderMed(View itemView){
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Record record = getSnapshots().get(position);
        if (record.getBloodGlucose() != 0) {
            return TYPE_GLUCOSE;
        } else if (!record.getMedication().isEmpty()){
            return TYPE_MEDICATION;
        }
        return super.getItemViewType(position);
    }

    public interface onRecordClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, ExpandableLayout expan);
    }

    public void setOnRecordClickListener(onRecordClickListener listener) {
        this.listener = listener;
    }

    public interface onEditClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnEditClickListener(onEditClickListener listener) {
        this.editClickListener = listener;
    }
}
