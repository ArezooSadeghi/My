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
import com.example.sipsupporterapp.databinding.FragmentAddEditCustomerSupportDialogBinding;
import com.example.sipsupporterapp.model.CustomerSupportInfo;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventInfo;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.AddEditCustomerSupportViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class AddEditCustomerSupportDialogFragment extends DialogFragment {
    private FragmentAddEditCustomerSupportDialogBinding binding;
    private AddEditCustomerSupportViewModel viewModel;

    private int customerID, supportEventID;
    private String lastValueSpinner;
    private SupportEventInfo[] supportEventArray;

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

        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);

        createViewModel();
        fetchSupportEvents();
        setupObservable();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_edit_customer_support_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(AddEditCustomerSupportViewModel.class);
    }

    private void fetchSupportEvents() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceSupportEventsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/supportEvents/List/";
        viewModel.fetchSupportEventsResult(path, userLoginKey);
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edTextAnswer.getText().toString().isEmpty() || binding.edTextQuestion.getText().toString().isEmpty()) {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.fill_required_fields));
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else {
                    int customerUserID = SipSupportSharedPreferences.getCustomerUserId(getContext());
                    String question = binding.edTextQuestion.getText().toString();
                    String answer = binding.edTextAnswer.getText().toString();
                    CustomerSupportInfo customerSupportInfo = new CustomerSupportInfo(supportEventID, customerID, customerUserID, question, answer);
                    addSupport(customerSupportInfo);
                }
            }
        });

        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                lastValueSpinner = (String) item;
                supportEventID = supportEventArray[position].getSupportEventID();
            }
        });
    }

    private void setupObservable() {
        viewModel.getSupportEventsResultSingleLiveEvent().observe(this, new Observer<SupportEventResult>() {
            @Override
            public void onChanged(SupportEventResult supportEventResult) {
                if (supportEventResult.getErrorCode().equals("0")) {
                    supportEventArray = supportEventResult.getSupportEvents();
                    setupSpinner(supportEventResult.getSupportEvents());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(supportEventResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getAddCustomerSupportResultSingleLiveEvent().observe(this, new Observer<CustomerSupportResult>() {
            @Override
            public void onChanged(CustomerSupportResult customerSupportResult) {
                if (customerSupportResult.getErrorCode().equals("0")) {
                    SuccessAddEditCustomerSupportDialogFragment fragment = SuccessAddEditCustomerSupportDialogFragment.newInstance("ثبت پشتیبانی موفقیت آمیز بود");
                    fragment.show(getParentFragmentManager(), "ok");
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerSupportResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getDangerousUserSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
                SipSupportSharedPreferences.setUserFullName(getContext(), null);
                SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
                SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
                SipSupportSharedPreferences.setCustomerName(getContext(), null);
                SipSupportSharedPreferences.setCustomerTel(getContext(), null);
                Intent intent = LoginContainerActivity.start(getContext());
                startActivity(intent);
                getActivity().finish();
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });
    }

    private void setupSpinner(SupportEventInfo[] supportEvents) {
        String[] supportEventArray = new String[supportEvents.length];
        for (int i = 0; i < supportEventArray.length; i++) {
            supportEventArray[i] = supportEvents[i].getSupportEvent();
        }
        if (supportEventArray.length != 0) {
            if (supportEventID == 0) {
                lastValueSpinner = supportEventArray[0];
                supportEventID = supportEvents[0].getSupportEventID();
                binding.spinner.setItems(supportEventArray);
            } else {
                for (int i = 0; i < supportEvents.length; i++) {
                    if (supportEvents[i].getSupportEventID() == supportEventID) {
                        lastValueSpinner = supportEvents[i].getSupportEvent();
                        SupportEventInfo supportEvent = supportEvents[i];
                        supportEventID = supportEvent.getSupportEventID();
                        supportEventArray[i] = supportEventArray[0];
                        supportEventArray[0] = lastValueSpinner;
                        supportEvents[i] = supportEvents[0];
                        supportEvents[0] = supportEvent;
                    }
                }
                binding.spinner.setItems(supportEventArray);
            }
        }
    }

    private void addSupport(CustomerSupportInfo customerSupportInfo) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceAddCustomerSupportResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerSupports/AddWithAnswer/";
        viewModel.addCustomerSupport(path, userLoginKey, customerSupportInfo);
    }
}