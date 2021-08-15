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
import com.example.sipsupporterapp.databinding.FragmentAddCustomerSupportDialogBinding;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerSupportViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddEditCustomerSupportDialogFragment extends DialogFragment {
    private FragmentAddCustomerSupportDialogBinding binding;
    private CustomerSupportViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private SupportEventResult.SupportEventInfo[] supportEventArray;
    private int customerID, supportEventID;

    private static final String ARGS_CUSTOMER_ID = "customerID";
    public static final String TAG = AddEditCustomerSupportDialogFragment.class.getSimpleName();

    public static AddEditCustomerSupportDialogFragment newInstance(int customerID) {
        AddEditCustomerSupportDialogFragment fragment = new AddEditCustomerSupportDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
        fetchSupportEvents();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_customer_support_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(CustomerSupportViewModel.class);
    }

    private void initVariables() {
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void setupSpinner(SupportEventResult.SupportEventInfo[] supportEventInfoArray) {
        List<String> supportEventList = new ArrayList<>();
        for (int i = 0; i < supportEventInfoArray.length; i++) {
            supportEventList.add(i, supportEventInfoArray[i].getSupportEvent());
        }

        supportEventID = supportEventInfoArray[0].getSupportEventID();
        binding.spinner.setItems(supportEventList);
    }

    private void showSuccessDialog(String message) {
        SuccessAddEditCustomerSupportDialogFragment fragment = SuccessAddEditCustomerSupportDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessAddEditCustomerSupportDialogFragment.TAG);
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
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void fetchSupportEvents() {
        viewModel.getSipSupporterServiceSupportEventResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/supportEvents/List/";
        viewModel.fetchSupportEventsResult(path, userLoginKey);
    }

    private void addCustomerSupport(CustomerSupportResult.CustomerSupportInfo customerSupportInfo) {
        viewModel.getSipSupporterServiceCustomerSupportResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerSupports/AddWithAnswer/";
        viewModel.addCustomerSupport(path, userLoginKey, customerSupportInfo);
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
                if (binding.edTextAnswer.getText().toString().isEmpty() || binding.edTextQuestion.getText().toString().isEmpty()) {
                    handleError(getString(R.string.fill_required_fields));
                } else {
                    int customerUserID = SipSupportSharedPreferences.getCustomerUserId(getContext());
                    String question = binding.edTextQuestion.getText().toString();
                    String answer = binding.edTextAnswer.getText().toString();
                    CustomerSupportResult.CustomerSupportInfo customerSupportInfo = new CustomerSupportResult.CustomerSupportInfo();
                    customerSupportInfo.setSupportEventID(supportEventID);
                    customerSupportInfo.setCustomerID(customerID);
                    customerSupportInfo.setCustomerUserID(customerUserID);
                    customerSupportInfo.setQuestion(question);
                    customerSupportInfo.setAnswer(answer);
                    addCustomerSupport(customerSupportInfo);
                }
            }
        });

        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                supportEventID = supportEventArray[position].getSupportEventID();
            }
        });
    }

    private void setupObserver() {
        viewModel.getSupportEventsResultSingleLiveEvent().observe(this, new Observer<SupportEventResult>() {
            @Override
            public void onChanged(SupportEventResult supportEventResult) {
                if (supportEventResult.getErrorCode().equals("0")) {
                    supportEventArray = supportEventResult.getSupportEvents();
                    setupSpinner(supportEventResult.getSupportEvents());
                } else if (supportEventResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(supportEventResult.getError());
                }
            }
        });

        viewModel.getAddCustomerSupportResultSingleLiveEvent().observe(this, new Observer<CustomerSupportResult>() {
            @Override
            public void onChanged(CustomerSupportResult customerSupportResult) {
                if (customerSupportResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_customer_support_message));
                    dismiss();
                } else if (customerSupportResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerSupportResult.getError());
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