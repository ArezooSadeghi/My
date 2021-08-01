package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentQuestionDialogBinding;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

public class AttachAgainDialogFragment extends DialogFragment {
    private FragmentQuestionDialogBinding binding;
    private AttachmentViewModel viewModel;

    private static final String ARGS_MESSAGE = "message";

    public static final String TAG = AttachAgainDialogFragment.class.getSimpleName();

    public static AttachAgainDialogFragment newInstance(String message) {
        AttachAgainDialogFragment fragment = new AttachAgainDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        args.putString(ARGS_MESSAGE, message);

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
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_question_dialog, null, false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initViews() {
        String message = getArguments().getString(ARGS_MESSAGE);
        binding.txtMessage.setText(message);
    }

    private void handleEvents() {
        binding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getNoAttachAgain().setValue(true);
                dismiss();
            }
        });

        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getYesAttachAgain().setValue(true);
                dismiss();
            }
        });
    }
}