package net.aohayo.dotdash.inputoutput;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import net.aohayo.dotdash.R;

import java.util.ArrayList;
import java.util.List;

public class OutputSelectionFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String SELECTED_OUTPUTS = "selectedOutputs";

    public interface DialogListener {
        void onOutputDialogPositiveClick(DialogFragment dialog);
        void onOutputDialogCancelClick(DialogFragment dialog);
    }

    private DialogListener dialogListener;
    private List<MorseOutputs> selectedOutputs;
    private boolean vibrationAvailable;

    public static OutputSelectionFragment newInstance(List<MorseOutputs> selectedOutputs) {
        OutputSelectionFragment output = new OutputSelectionFragment();

        Bundle args = new Bundle();
        args.putSerializable(SELECTED_OUTPUTS, new ArrayList<>(selectedOutputs));
        output.setArguments(args);

        return output;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
        vibrationAvailable = VibrationOutput.isAvailable(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<MorseOutputs> selectedOutputsArgs = (List<MorseOutputs>) getArguments().getSerializable(SELECTED_OUTPUTS);
        if (selectedOutputsArgs != null) {
            selectedOutputs = selectedOutputsArgs;
        } else {
            selectedOutputs = new ArrayList<>();
        }
        if (!vibrationAvailable) {
            selectedOutputs.remove(MorseOutputs.VIBRATION);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.output_selection_dialog, null))
                .setPositiveButton(R.string.menu_select, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onOutputDialogPositiveClick(OutputSelectionFragment.this);
                    }
                })
                .setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onOutputDialogCancelClick(OutputSelectionFragment.this);
                    }
                });

        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                AlertDialog alertDialog = (AlertDialog) dialog;

                updateSelectButton();

                CheckBox soundCB = (CheckBox) alertDialog.findViewById(R.id.sound_output_checkbox);
                CheckBox screenCB = (CheckBox) alertDialog.findViewById(R.id.screen_output_checkbox);
                CheckBox diagramCB = (CheckBox) alertDialog.findViewById(R.id.diagram_output_checkbox);
                soundCB.setOnCheckedChangeListener(OutputSelectionFragment.this);
                screenCB.setOnCheckedChangeListener(OutputSelectionFragment.this);
                diagramCB.setOnCheckedChangeListener(OutputSelectionFragment.this);

                if (selectedOutputs.contains(MorseOutputs.AUDIO)) {
                    soundCB.setChecked(true);
                }
                if (selectedOutputs.contains(MorseOutputs.SCREEN)) {
                    screenCB.setChecked(true);
                }
                if (selectedOutputs.contains(MorseOutputs.DIAGRAM)) {
                    diagramCB.setChecked(true);
                }

                if (!vibrationAvailable) {
                    alertDialog.findViewById(R.id.vibration_output_layout).setVisibility(View.GONE);
                } else {
                    CheckBox vibrationCB = (CheckBox) alertDialog.findViewById(R.id.vibration_output_checkbox);
                    vibrationCB.setOnCheckedChangeListener(OutputSelectionFragment.this);
                    if (selectedOutputs.contains(MorseOutputs.VIBRATION)) {
                        vibrationCB.setChecked(true);
                    }
                }

                RelativeLayout soundOutputLayout = (RelativeLayout) alertDialog.findViewById(R.id.sound_output_layout);
                RelativeLayout screenOutputLayout = (RelativeLayout) alertDialog.findViewById(R.id.screen_output_layout);
                RelativeLayout vibrationOutputLayout = (RelativeLayout) alertDialog.findViewById(R.id.vibration_output_layout);
                RelativeLayout diagramOutputLayout = (RelativeLayout) alertDialog.findViewById(R.id.diagram_output_layout);
                soundOutputLayout.setOnClickListener(OutputSelectionFragment.this);
                screenOutputLayout.setOnClickListener(OutputSelectionFragment.this);
                vibrationOutputLayout.setOnClickListener(OutputSelectionFragment.this);
                diagramOutputLayout.setOnClickListener(OutputSelectionFragment.this);

                View soundOutputFAB = alertDialog.findViewById(R.id.sound_output_fab);
                View screenOutputFAB = alertDialog.findViewById(R.id.screen_output_fab);
                View vibrationOutputFAB = alertDialog.findViewById(R.id.vibration_output_fab);
                View diagramOutputFAB = alertDialog.findViewById(R.id.diagram_output_fab);
                soundOutputFAB.setOnClickListener(OutputSelectionFragment.this);
                screenOutputFAB.setOnClickListener(OutputSelectionFragment.this);
                vibrationOutputFAB.setOnClickListener(OutputSelectionFragment.this);
                diagramOutputFAB.setOnClickListener(OutputSelectionFragment.this);
            }
        });
        return dialog;
    }

    public void onCancel(DialogInterface dialog) {
        dialogListener.onOutputDialogCancelClick(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MorseOutputs output = null;
        switch (buttonView.getId()) {
            case R.id.sound_output_checkbox:
                output = MorseOutputs.AUDIO;
                break;
            case R.id.screen_output_checkbox:
                output = MorseOutputs.SCREEN;
                break;
            case R.id.vibration_output_checkbox:
                output = MorseOutputs.VIBRATION;
                break;
            case R.id.diagram_output_checkbox:
                output = MorseOutputs.DIAGRAM;
                break;
            default:
                break;
        }
        if (output != null) {
            boolean selected = selectedOutputs.contains(output);
            if (selected && !isChecked) {
                selectedOutputs.remove(output);
            } else if (!selected && isChecked) {
                selectedOutputs.add(output);
            }
            updateSelectButton();
        }
    }

    private void updateSelectButton() {
        AlertDialog dialog = (AlertDialog) getDialog();
        Button selectButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        selectButton.setEnabled(!selectedOutputs.isEmpty());
    }

    @Override
    public void onClick(View v) {
        CheckBox checkBox = null;
        switch (v.getId()) {
            case R.id.sound_output_layout:
            case R.id.sound_output_fab:
                checkBox = (CheckBox) getDialog().findViewById(R.id.sound_output_checkbox);
                break;
            case R.id.screen_output_layout:
            case R.id.screen_output_fab:
                checkBox = (CheckBox) getDialog().findViewById(R.id.screen_output_checkbox);
                break;
            case R.id.vibration_output_layout:
            case R.id.vibration_output_fab:
                checkBox = (CheckBox) getDialog().findViewById(R.id.vibration_output_checkbox);
                break;
            case R.id.diagram_output_layout:
            case R.id.diagram_output_fab:
                checkBox = (CheckBox) getDialog().findViewById(R.id.diagram_output_checkbox);
                break;
            default:
                break;
        }
        if (checkBox != null) {
            checkBox.toggle();
        }
    }

    public List<MorseOutputs> getSelectedOutputs() {
        return selectedOutputs;
    }

    public List<MorseOutputs> getNotSelectedOutputs() {
        ArrayList<MorseOutputs> outputs = new ArrayList<>();
        for (MorseOutputs output : MorseOutputs.values()) {
            if (!selectedOutputs.contains(output)) {
                outputs.add(output);
            }
        }
        if (!vibrationAvailable) {
            outputs.remove(MorseOutputs.VIBRATION);
        }
        return outputs;
    }
}
