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
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentSuccessDialogBinding;
import com.example.sipsupporterapp.viewmodel.UserViewModel;

public class SuccessAddEditCustomerSupportDialogFragment extends DialogFragment {
    private FragmentSuccessDialogBinding binding;
    private UserViewModel viewModel;

    private static final String ARGS_MESSAGE = "message";
    public static final String TAG = AddEditCustomerSupportDialogFragment.class.getSimpleName();

    public static SuccessAddEditCustomerSupportDialogFragment newInstance(String message) {
        SuccessAddEditCustomerSupportDialogFragment fragment = new SuccessAddEditCustomerSupportDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_success_dialog,
                null,
                false);

        initViews();
        handleEvent();

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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    private void initViews() {
        String message = getArguments().getString(ARGS_MESSAGE);
        binding.txtSuccessMessage.setText(message);
    }

    private void handleEvent() {
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getRefresh().setValue(true);
                dismiss();
            }
        });
    }
}