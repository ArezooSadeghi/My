package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentRegisterCaseResultDialogBinding;

import java.util.ArrayList;
import java.util.List;

public class RegisterCaseResultDialogFragment extends DialogFragment {
    private FragmentRegisterCaseResultDialogBinding binding;

    public static final String TAG = RegisterCaseResultDialogFragment.class.getSimpleName();

    public static RegisterCaseResultDialogFragment newInstance() {
        RegisterCaseResultDialogFragment fragment = new RegisterCaseResultDialogFragment();
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
                R.layout.fragment_register_case_result_dialog,
                null,
                false);

        setupSpinner();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void setupSpinner() {
        List<String> caseResultList = new ArrayList<String>() {{
            add("نتیجه مورد تایید نیست");
            add("نتیجه قابل قبول است");
        }};

        binding.spinnerCaseResult.setItems(caseResultList);
    }
}