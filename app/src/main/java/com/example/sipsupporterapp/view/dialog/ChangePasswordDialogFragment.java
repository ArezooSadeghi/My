package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentChangePasswordDialogBinding;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.ChangePasswordViewModel;

public class ChangePasswordDialogFragment extends DialogFragment {
    private FragmentChangePasswordDialogBinding binding;
    private ChangePasswordViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;

    public static final String TAG = ChangePasswordDialogFragment.class.getSimpleName();

    public static ChangePasswordDialogFragment newInstance() {
        ChangePasswordDialogFragment fragment = new ChangePasswordDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        setupObserver();
        initVariables();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_change_password_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
    }

    private void setupObserver() {
        viewModel.getChangedPasswordResultSingleLiveEvent().observe(this, new Observer<UserResult>() {
            @Override
            public void onChanged(UserResult userResult) {
                if (userResult.getErrorCode().equals("0")) {
                    SipSupportSharedPreferences.setUserLoginKey(getContext(), userResult.getUsers()[0].getUserLoginKey());
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_change_password));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    dismiss();
                } else if (userResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(userResult.getError());
                }
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
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
                if (binding.edTextNewPassword.getText().toString().length() > 12 || binding.edTextRepeatNewPassword.getText().toString().length() > 12) {
                    handleError("حداکثر 12 کاراکتر مجاز می باشد");
                } else {
                    if (!binding.edTextNewPassword.getText().toString().equals(binding.edTextRepeatNewPassword.getText().toString())) {
                        handleError("عدم تطابق رمز ها");
                    } else {
                        changePassword(binding.edTextNewPassword.getText().toString());
                    }
                }
            }
        });
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
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
        SipSupportSharedPreferences.setCaseID(getContext(), 0);
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void changePassword(String newPassword) {
        viewModel.getSipSupporterServiceCustomerUserResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/users/changePassword/";
        viewModel.changePassword(path, userLoginKey, newPassword);
    }
}