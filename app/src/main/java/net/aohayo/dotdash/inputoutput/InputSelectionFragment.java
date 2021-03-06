package net.aohayo.dotdash.inputoutput;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import net.aohayo.dotdash.R;

public class InputSelectionFragment extends DialogFragment implements View.OnClickListener {
    public interface DialogListener {
        void onInputDialogCancelClick(DialogFragment dialog);
        void onInputDialogPositiveClick(DialogFragment dialog);
    }

    private DialogListener dialogListener;
    private MorseInput selectedInput;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedInput = MorseInput.FAB_BUTTON;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.input_selection_dialog, null))
                .setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onInputDialogCancelClick(InputSelectionFragment.this);
                    }
                });

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                AlertDialog alertDialog = (AlertDialog) dialog;

                RelativeLayout fabInputLayout = (RelativeLayout) alertDialog.findViewById(R.id.fab_input_layout);
                RelativeLayout lbInputLayout = (RelativeLayout) alertDialog.findViewById(R.id.large_button_input_layout);
                RelativeLayout textInputLayout = (RelativeLayout) alertDialog.findViewById(R.id.text_input_layout);
                fabInputLayout.setOnClickListener(InputSelectionFragment.this);
                lbInputLayout.setOnClickListener(InputSelectionFragment.this);
                textInputLayout.setOnClickListener(InputSelectionFragment.this);

                View fabInputFAB = alertDialog.findViewById(R.id.fab_input_fab);
                View lbInputFAB = alertDialog.findViewById(R.id.large_button_input_fab);
                View textInputFAB = alertDialog.findViewById(R.id.text_input_fab);
                fabInputFAB.setOnClickListener(InputSelectionFragment.this);
                lbInputFAB.setOnClickListener(InputSelectionFragment.this);
                textInputFAB.setOnClickListener(InputSelectionFragment.this);
            }
        });
        return dialog;
    }

    public void onCancel(DialogInterface dialog) {
        dialogListener.onInputDialogCancelClick(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_input_layout:
            case R.id.fab_input_fab:
                selectedInput = MorseInput.FAB_BUTTON;
                dialogListener.onInputDialogPositiveClick(this);
                getDialog().dismiss();
                break;
            case R.id.large_button_input_layout:
            case R.id.large_button_input_fab:
                selectedInput = MorseInput.LARGE_BUTTON;
                dialogListener.onInputDialogPositiveClick(this);
                getDialog().dismiss();
                break;
            case R.id.text_input_layout:
            case R.id.text_input_fab:
                selectedInput = MorseInput.TEXT;
                dialogListener.onInputDialogPositiveClick(this);
                getDialog().dismiss();
                break;
        }
    }

    public MorseInput getSelectedInput() {
        return selectedInput;
    }
}
