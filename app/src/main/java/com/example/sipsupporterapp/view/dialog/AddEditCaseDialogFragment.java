package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.sipsupporterapp.databinding.FragmentAddEditCaseDialogBinding;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;

public class AddEditCaseDialogFragment extends DialogFragment {
    private FragmentAddEditCaseDialogBinding binding;
    private CaseViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private CaseResult.CaseInfo caseInfo;
    private CustomerResult.CustomerInfo customerInfo;
    private int caseID, caseTypeID, customerID;

    private static final String ARGS_CASE_ID = "caseID";
    private static final String ARGS_CASE_TYPE_ID = "caseTypeID";
    private static final String ARGS_CUSTOMER_ID = "CustomerID";
    public static final String TAG = AddEditCaseDialogFragment.class.getSimpleName();

    public static AddEditCaseDialogFragment newInstance(int caseID, int caseTypeID, int customerID) {
        AddEditCaseDialogFragment fragment = new AddEditCaseDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CASE_ID, caseID);
        args.putInt(ARGS_CASE_TYPE_ID, caseTypeID);
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        setupObserver();
        initVariables();

        if (caseID > 0) {
            fetchCaseInfo(caseID);
        }
        if (customerID != 0) {
            fetchCustomerInfo(customerID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_edit_case_dialog,
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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CaseViewModel.class);
    }

    private void initVariables() {
        caseID = getArguments().getInt(ARGS_CASE_ID);
        caseTypeID = getArguments().getInt(ARGS_CASE_TYPE_ID);
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        binding.edTextDescription.setText(caseInfo.getDescription());

        binding.checkBoxShare.setChecked(caseInfo.isShare());

        switch (caseInfo.getPriority()) {
            case 0:
                binding.radioBtnLow.setChecked(true);
                break;
            case 1:
                binding.radioBtnMedium.setChecked(true);
                break;
            case 2:
                binding.radioBtnImportant.setChecked(true);
                break;
            case 3:
                binding.radioBtnVeryImportant.setChecked(true);
                break;
        }
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
    }

    private void ejectUser() {
        SipSupportSharedPreferences.setUserFullName(getContext(), null);
        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
        SipSupportSharedPreferences.setCenterName(getContext(), null);
        SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
    }

    private void addCase(CaseResult.CaseInfo caseInfo) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/case/Add/";
        viewModel.addCase(path, userLoginKey, caseInfo);
    }

    private void editCase(CaseResult.CaseInfo caseInfo) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Edit/";
        viewModel.editCase(path, userLoginKey, caseInfo);
    }

    private void fetchCaseInfo(int caseID) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/case/Info/";
        viewModel.fetchCaseInfo(path, userLoginKey, caseID);
    }

    private void fetchCustomerInfo(int customerID) {
        viewModel.getSipSupporterServiceCustomerResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customers/Info/";
        viewModel.fetchCustomerInfo(path, userLoginKey, customerID);
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
                CaseResult.CaseInfo caseInfo = new CaseResult().new CaseInfo();
                caseInfo.setCaseID(caseID);
                caseInfo.setCaseTypeID(caseTypeID);
                caseInfo.setCustomerID(customerID);
                caseInfo.setCustomerName(binding.btnCustomerName.getText().toString());
                caseInfo.setDescription(binding.edTextDescription.getText().toString());
                caseInfo.setShare(binding.checkBoxShare.isChecked());
                if (binding.radioBtnLow.isChecked()) {
                    caseInfo.setPriority(0);
                } else if (binding.radioBtnMedium.isChecked()) {
                    caseInfo.setPriority(1);
                } else if (binding.radioBtnImportant.isChecked()) {
                    caseInfo.setPriority(2);
                } else if (binding.radioBtnVeryImportant.isChecked()) {
                    caseInfo.setPriority(3);
                }

                if (caseID == 0) {
                    addCase(caseInfo);
                } else {
                    editCase(caseInfo);
                }
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getMoreClicked().setValue(true);
                dismiss();
            }
        });
    }

    private void setupObserver() {
        viewModel.getCustomerInfoResultSingleLiveEvent().observe(this, new Observer<CustomerResult>() {
            @Override
            public void onChanged(CustomerResult customerResult) {
                if (customerResult.getErrorCode().equals("0")) {
                    customerInfo = customerResult.getCustomers()[0];
                    binding.btnCustomerName.setText(Converter.letterConverter(customerInfo.getCustomerName()));
                }
            }
        });

        viewModel.getCaseInfoResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    caseInfo = caseResult.getCases()[0];
                    initViews();
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
                }
            }
        });

        viewModel.getAddCaseResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_case_message));
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
                    dismiss();
                }
            }
        });

        viewModel.getEditCaseResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_case_message));
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
                    dismiss();
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });
    }
}