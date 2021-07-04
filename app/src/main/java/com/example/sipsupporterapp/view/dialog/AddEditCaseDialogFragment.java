package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
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
import androidx.navigation.fragment.NavHostFragment;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditCaseDialogBinding;
import com.example.sipsupporterapp.model.CaseInfo;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.fragment.TaskFragment;
import com.example.sipsupporterapp.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddEditCaseDialogFragment extends DialogFragment {
    private FragmentAddEditCaseDialogBinding binding;
    private TaskViewModel viewModel;
    private ServerData serverData;
    private int customerID;
    private String centerName, userLoginKey;

    private static final String ARGS_CUSTOMER_ID = "CustomerID";
    private static final String ARGS_CUSTOMER_NAME = "CustomerName";
    private static final String ARGS_CASE_TYPE_ID = "caseTypeID";

    public static final String TAG = AddEditCaseDialogFragment.class.getSimpleName();

    public static AddEditCaseDialogFragment newInstance(int caseTypeID, int customerID, String customerName) {
        AddEditCaseDialogFragment fragment = new AddEditCaseDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CASE_TYPE_ID, caseTypeID);
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        args.putString(ARGS_CUSTOMER_NAME, customerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        setupObserver();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_edit_case_dialog,
                null,
                false);

        String customerName = getArguments().getString(ARGS_CUSTOMER_NAME);
        binding.edTextCustomerName.setText(customerName);

        setupSpinner();
        handleEvents();

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

    private void setupObserver() {
        viewModel.getAddCaseResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("کار با موفقیت افزوده شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(caseResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });
    }

    private void setupSpinner() {
        List<String> priorities = new ArrayList<String>() {{
            add("کم");
            add("متوسط");
            add("مهم");
            add("خیلی مهم");
        }};

        binding.spinnerPriority.setItems(priorities);
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
                CaseInfo caseInfo = new CaseInfo();
                int caseTypeID = getArguments().getInt(ARGS_CASE_TYPE_ID);
                int customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
                caseInfo.setCaseTypeID(caseTypeID);
                caseInfo.setCustomerID(customerID);
                caseInfo.setCustomerName(binding.edTextCustomerName.getText().toString());
                caseInfo.setDescription(binding.edTextDescription.getText().toString());

                viewModel.getSipSupporterServiceAddCase(serverData.getIpAddress() + ":" + serverData.getPort());
                String path = "/api/v1/case/Add/";
                viewModel.addCase(path, userLoginKey, caseInfo);
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddEditCaseDialogFragment.this).navigate(R.id.menu_search);
                dismiss();
            }
        });
    }
}