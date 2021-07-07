package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentChangeCaseTypeDialogBinding;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChangeCaseTypeDialogFragment extends DialogFragment {
    private FragmentChangeCaseTypeDialogBinding binding;
    private TaskViewModel viewModel;

    public static final String TAG = ChangeCaseTypeDialogFragment.class.getSimpleName();

    public static ChangeCaseTypeDialogFragment newInstance() {
        ChangeCaseTypeDialogFragment fragment = new ChangeCaseTypeDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        fetchCaseTypes();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_change_case_type_dialog,
                null,
                false);

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
    }

    private void fetchCaseTypes() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCaseTypes(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/CaseType/List/";
        viewModel.fetchCaseTypes(path, userLoginKey);
    }

    private void setupObserver() {
        viewModel.getCaseTypesResultSingleLiveEvent().observe(this, new Observer<CaseTypeResult>() {
            @Override
            public void onChanged(CaseTypeResult caseTypeResult) {
                if (caseTypeResult.getErrorCode().equals("0")) {
                    setupSpinner(caseTypeResult.getCaseTypes());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(caseTypeResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });
    }

    private void setupSpinner(CaseTypeResult.CaseTypeInfo[] caseTypeInfoArray) {
        List<String> caseTypes = new ArrayList<>();
        for (int i = 0; i < caseTypeInfoArray.length; i++) {
            caseTypes.add(caseTypeInfoArray[i].getCaseType());
        }
        binding.spinnerCaseTypes.setItems(caseTypes);
    }
}