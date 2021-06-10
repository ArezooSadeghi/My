package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentQuestionDialogBinding;
import com.example.sipsupporterapp.viewmodel.NewPaymentViewModel;
import com.example.sipsupporterapp.viewmodel.PaymentViewModel;

public class QuestionDeleteNewPaymentFragment extends DialogFragment {
    private FragmentQuestionDialogBinding binding;
    private NewPaymentViewModel viewModel;

    private static final String ARGS_MESSAGE = "message";

    public static final String TAG = QuestionDeleteNewPaymentFragment.class.getSimpleName();

    public static QuestionDeleteNewPaymentFragment newInstance(String message) {
        QuestionDeleteNewPaymentFragment fragment = new QuestionDeleteNewPaymentFragment();
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
                R.layout.fragment_question_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(NewPaymentViewModel.class);
    }

    private void initViews() {
        String message = getArguments().getString(ARGS_MESSAGE);
        binding.txtMessage.setText(message);
    }

    private void handleEvents() {
        binding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getYesDelete().setValue(true);
                dismiss();
            }
        });
    }
}