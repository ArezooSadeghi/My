package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentSeeDialogBinding;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;

public class SeeDialogFragment extends DialogFragment {
    private FragmentSeeDialogBinding binding;
    private CaseViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int caseID;

    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = SeeDialogFragment.class.getSimpleName();

    public static SeeDialogFragment newInstance(int caseID) {
        SeeDialogFragment fragment = new SeeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CASE_ID, caseID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
        setupObserver();
        fetchCaseInfo(caseID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_see_dialog,
                null,
                false);

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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CaseViewModel.class);
    }

    private void initVariables() {
        caseID = getArguments().getInt(ARGS_CASE_ID);

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void fetchCaseInfo(int caseID) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Info/";
        viewModel.fetchCaseInfo(path, userLoginKey, caseID);
    }

    private void setupObserver() {
        viewModel.getCaseInfoResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    binding.edTextDescription.setText(caseResult.getCases()[0].getDescription());
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
                }
            }
        });
    }

    private void handleError(String msg) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(msg);
        dialog.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void ejectUser() {
        SipSupportSharedPreferences.setUserFullName(getContext(), null);
        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
        SipSupportSharedPreferences.setCenterName(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}