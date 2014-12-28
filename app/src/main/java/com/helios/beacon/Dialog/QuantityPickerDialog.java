package com.helios.beacon.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.example.nhantran.beaconexample.R;
import com.helios.beacon.model.OrderedItem;

/**
 * Created by nhantran on 11/13/14.
 */
public class QuantityPickerDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {

    private static QuantityPickerDialog instance;

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        orderedItem.setQuantity(newVal);
    }

    public interface NoticeDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private NoticeDialogListener listener;
    private OrderedItem orderedItem;

    public QuantityPickerDialog(){};

    public void setListener(NoticeDialogListener listener){
        this.listener = listener;
    }

    public void setOrderedItem(OrderedItem orderedItem){
        this.orderedItem = orderedItem;
    }

    public OrderedItem getOrderedItem(){
        return orderedItem;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        instance = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.diaglog_quantity_picker, null);
        NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(30);
        numberPicker.setMinValue(1);
        numberPicker.setValue(1);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        builder.setView(dialogView)
                .setTitle("Pick number you want order!")
                .setPositiveButton(R.string.btn_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(instance);
                    }
                })
                .setNegativeButton(R.string.btn_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(instance);
                    }
                });

        return builder.create();
    }
}
