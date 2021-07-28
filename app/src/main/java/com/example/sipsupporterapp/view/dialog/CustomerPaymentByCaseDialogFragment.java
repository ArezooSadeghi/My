package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CustomerPaymentAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerPaymentByCaseDialogBinding;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;

import java.util.Arrays;

public class CustomerPaymentByCaseDialogFragment extends DialogFragment {
    private FragmentCustomerPaymentByCaseDialogBinding binding;
    private CaseViewModel viewModel;
    private ServerData serverData;
    private int caseID, customerID;
    private String centerName, userLoginKey, customerName;

    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = CustomerPaymentByCaseDialogFragment.class.getSimpleName();

    public static CustomerPaymentByCaseDialogFragment newInstance(int caseID) {
        CustomerPaymentByCaseDialogFragment fragment = new CustomerPaymentByCaseDialogFragment();
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
        fetchCustomerPaymentsByCaseID(caseID);
        fetchCaseInfo(caseID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_customer_payment_by_case_dialog,
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

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        caseID = getArguments().getInt(ARGS_CASE_ID);
    }

    private void setupObserver() {
        viewModel.getCustomerPaymentsByCaseResultSingleLiveEvent().observe(this, new Observer<CustomerPaymentResult>() {
            @Override
            public void onChanged(CustomerPaymentResult customerPaymentResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    setupAdapter(customerPaymentResult.getCustomerPayments());
                } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });

        viewModel.getCaseInfoResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    customerID = caseResult.getCases()[0].getCustomerID();
                    customerName = caseResult.getCases()[0].getCustomerName();
                    SipSupportSharedPreferences.setNewCustomerName(getContext(), customerName);
                }
            }
        });
    }

    private void fetchCustomerPaymentsByCaseID(int caseID) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/ListByCase/";
        viewModel.fetchCustomerPaymentsByCase(path, userLoginKey, caseID);
    }

    private void fetchCaseInfo(int caseID) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Info/";
        viewModel.fetchCaseInfo(path, userLoginKey, caseID);
    }

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance(0, customerID, 0, 0, 0, "", caseID);
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
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

    private void setupAdapter(CustomerPaymentResult.CustomerPaymentInfo[] customerPaymentInfoArray) {
        if (customerPaymentInfoArray.length == 0) {
            binding.txtEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.txtEmpty.setVisibility(View.GONE);
            CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(getContext(), Arrays.asList(customerPaymentInfoArray));
            binding.recyclerView.setAdapter(adapter);
        }
    }
}