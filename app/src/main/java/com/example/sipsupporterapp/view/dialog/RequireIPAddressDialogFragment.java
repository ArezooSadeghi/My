package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentRequireIpAddressDialogBinding;

public class RequireIPAddressDialogFragment extends DialogFragment {
    private FragmentRequireIpAddressDialogBinding binding;

    public static final String TAG = RequireIPAddressDialogFragment.class.getSimpleName();

    public static RequireIPAddressDialogFragment newInstance() {
        RequireIPAddressDialogFragment fragment = new RequireIPAddressDialogFragment();
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
                R.layout.fragment_require_ip_address_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        return dialog;
    }

    private void handleEvents() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditIPAddressDialogFragment fragment = AddEditIPAddressDialogFragment.newInstance("", "", "");
                fragment.show(getParentFragmentManager(), AddEditIPAddressDialogFragment.TAG);
                dismiss();
            }
        });
    }
}