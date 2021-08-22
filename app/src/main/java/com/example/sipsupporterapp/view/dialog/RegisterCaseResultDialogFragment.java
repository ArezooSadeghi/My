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
import com.example.sipsupporterapp.databinding.FragmentRegisterCaseResultDialogBinding;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class RegisterCaseResultDialogFragment extends DialogFragment {
    private FragmentRegisterCaseResultDialogBinding binding;
    private CaseViewModel viewModel;
    private ServerData serverData;
    private String textResultOk, centerName, userLoginKey;

    private static final String ARGS_CASE_ID = "caseID";
    private static final String ARGS_RESULT_OK = "resultOk";
    private static final String ARGS_RESULT_DESCRIPTION = "resultDescription";
    public static final String TAG = RegisterCaseResultDialogFragment.class.getSimpleName();

    public static RegisterCaseResultDialogFragment newInstance(int caseID, boolean resultOk, String resultDescription) {
        RegisterCaseResultDialogFragment fragment = new RegisterCaseResultDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ARGS_CASE_ID, caseID);
        args.putBoolean(ARGS_RESULT_OK, resultOk);
        args.putString(ARGS_RESULT_DESCRIPTION, resultDescription);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_register_case_result_dialog,
                null,
                false);

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
        viewModel = new ViewModelProvider(requireActivity()).get(CaseViewModel.class);
    }

    private void setupObserver() {
        viewModel.getCloseCaseResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("نتیجه کار با موفقیت ثبت شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefreshCaseFinishClicked().setValue(true);
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

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
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

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
    }

    private void initViews() {
        String resultDescription = getArguments().getString(ARGS_RESULT_DESCRIPTION);
        binding.edTextResultDescription.setText(resultDescription);

        List<String> caseResultListZero = new ArrayList<String>() {{
            add(0, "نتیجه مورد تایید نیست");
            add(1, "نتیجه قابل قبول است");
        }};
        textResultOk = caseResultListZero.get(0);
        binding.spinner.setItems(caseResultListZero);

        boolean resultOk = getArguments().getBoolean(ARGS_RESULT_OK);
        if (resultOk) {
            List<String> caseResultListOne = new ArrayList<String>() {{
                add(0, "نتیجه قابل قبول است");
                add(1, "نتیجه مورد تایید نیست");
            }};
            textResultOk = caseResultListOne.get(0);
            binding.spinner.setItems(caseResultListOne);
        }
    }

    private void handleEvents() {
        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                textResultOk = (String) item;
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaseResult.CaseInfo caseInfo = new CaseResult.CaseInfo();

                int caseID = getArguments().getInt(ARGS_CASE_ID);
                caseInfo.setCaseID(caseID);

                if (textResultOk.equals("نتیجه قابل قبول است")) {
                    caseInfo.setResultOk(true);
                } else {
                    caseInfo.setResultOk(false);
                }

                caseInfo.setResultDescription(binding.edTextResultDescription.getText().toString());

                closeCase(caseInfo);
            }
        });
    }

    private void closeCase(CaseResult.CaseInfo caseInfo) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Close/";
        viewModel.closeCase(path, userLoginKey, caseInfo);
    }
}