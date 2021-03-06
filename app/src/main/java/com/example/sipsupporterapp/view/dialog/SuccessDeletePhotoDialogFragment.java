package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentSuccessDialogBinding;

public class SuccessDeletePhotoDialogFragment extends DialogFragment {
    private FragmentSuccessDialogBinding binding;

    private static final String ARGS_MESSAGE = "message";

    public static final String TAG = SuccessDeletePhotoDialogFragment.class.getSimpleName();

    public static SuccessDeletePhotoDialogFragment newInstance(String message) {
        SuccessDeletePhotoDialogFragment fragment = new SuccessDeletePhotoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE, message);
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
                R.layout.fragment_success_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void initViews() {
        String message = getArguments().getString(ARGS_MESSAGE);
        binding.txtSuccessMessage.setText(message);
    }

    private void handleEvents() {
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                dismiss();
            }
        });
    }
}