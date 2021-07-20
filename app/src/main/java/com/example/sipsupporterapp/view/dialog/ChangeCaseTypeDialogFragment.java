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
import com.example.sipsupporterapp.databinding.FragmentChangeCaseTypeDialogBinding;
import com.example.sipsupporterapp.eventbus.CaseTypesEvent;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ChangeCaseTypeDialogFragment extends DialogFragment {
    private FragmentChangeCaseTypeDialogBinding binding;
    private CaseViewModel viewModel;
    private static final String ARGS_CASE_INFO = "caseInfo";

    private int caseTypeID;
    private List<String> caseTypes = new ArrayList<>();
    private List<Integer> caseTypeIDs = new ArrayList<>();
    private String centerName, userLoginKey;
    private ServerData serverData;

    public static final String TAG = ChangeCaseTypeDialogFragment.class.getSimpleName();

    public static ChangeCaseTypeDialogFragment newInstance(CaseResult.CaseInfo caseTypeInfo) {
        ChangeCaseTypeDialogFragment fragment = new ChangeCaseTypeDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_CASE_INFO, caseTypeInfo);
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

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void getCaseTypesEvent(CaseTypesEvent event) {
        setupSpinner(event.getCaseTypeResult().getCaseTypes());
        EventBus.getDefault().removeStickyEvent(event);
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
                editCase();
                viewModel.getRefreshSingleLiveEvent().setValue(caseTypeID);
                dismiss();
            }
        });

        binding.spinnerCaseTypes.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String caseType = (String) item;
                for (int i = 0; i < caseTypes.size(); i++) {
                    if (caseType.equals(caseTypes.get(i))) {
                        caseTypeID = caseTypeIDs.get(i);
                        break;
                    }
                }
            }
        });
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CaseViewModel.class);
    }

    private void fetchCaseTypes() {
        viewModel.getSipSupporterServiceCaseTypeResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/CaseType/List/";
        viewModel.fetchCaseTypes(path, userLoginKey);
    }

    private void editCase() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Edit/";
        CaseResult.CaseInfo caseInfo = (CaseResult.CaseInfo) getArguments().getSerializable(ARGS_CASE_INFO);
        caseInfo.setCaseTypeID(caseTypeID);
        viewModel.editCase(path, userLoginKey, caseInfo);
    }

    private void setupObserver() {
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

        viewModel.getEditCaseResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("تغییر گروه با موفقیت انجام شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
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

    private void setupSpinner(CaseTypeResult.CaseTypeInfo[] caseTypeInfoArray) {
        for (int i = 0; i < caseTypeInfoArray.length; i++) {
            caseTypes.add(i, caseTypeInfoArray[i].getCaseType());
            caseTypeIDs.add(i, caseTypeInfoArray[i].getCaseTypeID());
        }
        caseTypeID = caseTypeIDs.get(0);
        binding.spinnerCaseTypes.setItems(caseTypes);
    }
}