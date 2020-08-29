package com.example.bittersweet.Helper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.example.bittersweet.R;

import java.util.Calendar;

public class YearPickerDialog extends AppCompatDialogFragment {

    private AlertDialog.Builder builder;
    private LayoutInflater layoutInflater;
    private View view;
    private NumberPicker yearPicker;
    private YearPickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        layoutInflater = getActivity().getLayoutInflater();
        view = layoutInflater.inflate(R.layout.year_picker_dialog, null);

        yearPicker = (NumberPicker) view.findViewById(R.id.year_of_diagnose_picker);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearPicker.setMinValue(1600);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(currentYear);
        yearPicker.setWrapSelectorWheel(false);

        builder.setView(view)
        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int year = yearPicker.getValue();
                listener.applyText(year);
            }
        });
        return builder.create();
    }

    public interface YearPickerListener {
        void applyText(int year);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (YearPickerListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() +
                    "please implement YearPickerListener");
        }
    }
}
