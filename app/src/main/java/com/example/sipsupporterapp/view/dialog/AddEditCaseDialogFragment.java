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
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.TaskViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddEditCaseDialogFragment extends DialogFragment {
    public static final String ARGS_CASE_ID = "caseID";
    private FragmentAddEditCaseDialogBinding binding;
    private TaskViewModel viewModel;
    private ServerData serverData;
    private int customerID, caseID;
    private String centerName, userLoginKey, textPriority;

    private static final String ARGS_CUSTOMER_ID = "CustomerID";
    private static final String ARGS_CUSTOMER_NAME = "CustomerName";
    private static final String ARGS_CASE_TYPE_ID = "caseTypeID";
    private static final String ARGS_PRIORITY = "priority";
    private static final String ARGS_SHARE = "share";
    private static final String ARGS_DESCRIPTION = "description";

    public static final String TAG = AddEditCaseDialogFragment.class.getSimpleName();

    public static AddEditCaseDialogFragment newInstance(int caseID, int caseTypeID, int customerID, String customerName, int priority, boolean share, String description) {
        AddEditCaseDialogFragment fragment = new AddEditCaseDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ARGS_CASE_ID, caseID);
        args.putInt(ARGS_CASE_TYPE_ID, caseTypeID);
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        args.putString(ARGS_CUSTOMER_NAME, customerName);
        args.putInt(ARGS_PRIORITY, priority);
        args.putBoolean(ARGS_SHARE, share);
        args.putString(ARGS_DESCRIPTION, description);

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

        initViews();
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

        viewModel.getEditCaseResultSingleLiveEvent().observe(this, new Observer<CaseResult>() {
            @Override
            public void onChanged(CaseResult caseResult) {
                if (caseResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("کار با موفقیت افزوده شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(caseResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    dismiss();
                }
            }
        });
    }

    private void initViews() {
        String customerName = Converter.letterConverter(getArguments().getString(ARGS_CUSTOMER_NAME));
        binding.edTextCustomerName.setText(customerName);

        String description = getArguments().getString(ARGS_DESCRIPTION);
        binding.edTextDescription.setText(description);

        boolean share = getArguments().getBoolean(ARGS_SHARE);
        binding.checkBoxShare.setChecked(share);

        int priority = getArguments().getInt(ARGS_PRIORITY);
        switch (priority) {
            case 0:
                List<String> prioritiesZero = new ArrayList<String>() {{
                    add(0, "کم");
                    add(1, "متوسط");
                    add(2, "مهم");
                    add(3, "خیلی مهم");
                }};
                textPriority = "کم";
                binding.spinnerPriority.setItems(prioritiesZero);
                break;
            case 1:
                List<String> prioritiesOne = new ArrayList<String>() {{
                    add(0, "متوسط");
                    add(1, "کم");
                    add(2, "مهم");
                    add(3, "خیلی مهم");
                }};
                textPriority = "متوسط";
                binding.spinnerPriority.setItems(prioritiesOne);
                break;
            case 2:
                List<String> prioritiesTwo = new ArrayList<String>() {{
                    add(0, "مهم");
                    add(1, "کم");
                    add(2, "متوسط");
                    add(3, "خیلی مهم");
                }};
                textPriority = "مهم";
                binding.spinnerPriority.setItems(prioritiesTwo);
                break;
            case 3:
                List<String> prioritiesThree = new ArrayList<String>() {{
                    add(0, "خیلی مهم");
                    add(1, "کم");
                    add(2, "متوسط");
                    add(3, "مهم");
                }};
                textPriority = "خیلی مهم";
                binding.spinnerPriority.setItems(prioritiesThree);
                break;
        }
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
                int caseID = getArguments().getInt(ARGS_CASE_ID);
                int caseTypeID = getArguments().getInt(ARGS_CASE_TYPE_ID);
                int customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
                caseInfo.setCaseID(caseID);
                caseInfo.setCaseTypeID(caseTypeID);
                caseInfo.setCustomerID(customerID);
                caseInfo.setCustomerName(binding.edTextCustomerName.getText().toString());
                caseInfo.setDescription(binding.edTextDescription.getText().toString());
                caseInfo.setShare(binding.checkBoxShare.isChecked());

                switch (textPriority) {
                    case "کم":
                        caseInfo.setPriority(0);
                        break;
                    case "متوسط":
                        caseInfo.setPriority(1);
                        break;
                    case "مهم":
                        caseInfo.setPriority(2);
                        break;
                    case "خیلی مهم":
                        caseInfo.setPriority(3);
                        break;
                }

                if (caseID == 0) {
                    viewModel.getSipSupporterServiceAddCase(serverData.getIpAddress() + ":" + serverData.getPort());
                    String path = "/api/v1/case/Add/";
                    viewModel.addCase(path, userLoginKey, caseInfo);
                } else {
                    viewModel.getSipSupporterServiceEditCase(serverData.getIpAddress() + ":" + serverData.getPort());
                    String path = "/api/v1/Case/Edit/";
                    viewModel.editCase(path, userLoginKey, caseInfo);
                }
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddEditCaseDialogFragment.this).navigate(R.id.menu_search);
                dismiss();
            }
        });

        binding.spinnerPriority.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                textPriority = (String) item;
            }
        });
    }
}