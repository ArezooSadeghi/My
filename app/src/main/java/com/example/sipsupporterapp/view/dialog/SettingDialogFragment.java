package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentSettingDialogBinding;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;

public class SettingDialogFragment extends DialogFragment {
    private FragmentSettingDialogBinding binding;

    public static final String TAG = SettingDialogFragment.class.getSimpleName();

    public static SettingDialogFragment newInstance() {
        SettingDialogFragment fragment = new SettingDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_setting_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.radioBtnOne.isChecked()) {
                    SipSupportSharedPreferences.setFactor(getContext(), binding.radioBtnOne.getText().toString());
                } else if (binding.radioBtnTwo.isChecked()) {
                    SipSupportSharedPreferences.setFactor(getContext(), binding.radioBtnTwo.getText().toString());
                } else if (binding.radioBtnThree.isChecked()) {
                    SipSupportSharedPreferences.setFactor(getContext(), binding.radioBtnThree.getText().toString());
                } else if (binding.radioBtnFour.isChecked()) {
                    SipSupportSharedPreferences.setFactor(getContext(), binding.radioBtnFour.getText().toString());
                }
                dismiss();
            }
        });
    }
}