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
import com.example.sipsupporterapp.eventbus.RefreshEvent;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class CustomerPaymentByCaseDialogFragment extends DialogFragment {
    private FragmentCustomerPaymentByCaseDialogBinding binding;
    private CaseViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private CaseResult.CaseInfo caseInfo;
    private int caseID;

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
        setupObserver();
        initVariables();
        fetchCaseInfo(caseID);
        fetchCustomerPaymentsByCaseID(caseID);
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

    @Subscribe
    public void getRefreshEvent(RefreshEvent event) {
        fetchCustomerPaymentsByCaseID(caseID);
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

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setupAdapter(CustomerPaymentResult.CustomerPaymentInfo[] customerPaymentInfoArray) {
        if (customerPaymentInfoArray.length == 0) {
            binding.txtEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.txtEmpty.setVisibility(View.GONE);
            CustomerPaymentAdapter adapter = new CustomerPaymentAdapter(Arrays.asList(customerPaymentInfoArray), false);
            binding.recyclerView.setAdapter(adapter);
        }
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
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void fetchCustomerPaymentsByCaseID(int caseID) {
        viewModel.getSipSupporterServiceCustomerResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/ListByCase/";
        viewModel.fetchCustomerPaymentsByCase(path, userLoginKey, caseID);
    }

    private void fetchCaseInfo(int caseID) {
        viewModel.getSipSupporterServiceCaseResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Case/Info/";
        viewModel.fetchCaseInfo(path, userLoginKey, caseID);
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
                AddEditCustomerPaymentDialogFragment fragment = AddEditCustomerPaymentDialogFragment.newInstance(0, caseInfo.getCustomerID(), caseInfo.getCaseID());
                fragment.show(getParentFragmentManager(), AddEditCustomerPaymentDialogFragment.TAG);
            }
        });
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
                    caseInfo = caseResult.getCases()[0];
                } else if (caseResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseResult.getError());
                }
            }
        });
    }
}