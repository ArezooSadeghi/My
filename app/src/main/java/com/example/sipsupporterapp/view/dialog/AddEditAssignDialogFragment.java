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
import com.example.sipsupporterapp.databinding.FragmentAddEditAssignDialogBinding;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.AssignViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddEditAssignDialogFragment extends DialogFragment {
    private FragmentAddEditAssignDialogBinding binding;
    private AssignViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private UserResult.UserInfo[] userInfoArray;
    private AssignResult.AssignInfo assignInfo;
    private int assignUserID, assignID, caseID;

    private static final String ARGS_ASSIGN_ID = "assignID";
    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = AddEditAssignDialogFragment.class.getSimpleName();

    public static AddEditAssignDialogFragment newInstance(int assignID, int caseID) {
        AddEditAssignDialogFragment fragment = new AddEditAssignDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_ASSIGN_ID, assignID);
        args.putInt(ARGS_CASE_ID, caseID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
        fetchUsers();
        setupObserver();

        if (assignID > 0) {
            fetchAssignInfo(assignID);
        }
    }

    private void initVariables() {
        assignID = getArguments().getInt(ARGS_ASSIGN_ID);
        caseID = getArguments().getInt(ARGS_CASE_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_edit_assign_dialog,
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
        viewModel = new ViewModelProvider(requireActivity()).get(AssignViewModel.class);
    }

    private void initViews() {
        binding.edTextDescription.setText(assignInfo.getDescription());
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
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

    private void setupSpinner(UserResult.UserInfo[] userInfoArray) {
        List<String> userFullNameList = new ArrayList<>();
        for (int i = 0; i < userInfoArray.length; i++) {
            userFullNameList.add(i, userInfoArray[i].getUserFullName());
        }
        assignUserID = userInfoArray[0].getUserID();
        binding.spinner.setItems(userFullNameList);
    }

    private void fetchUsers() {
        viewModel.getSipSupporterServiceUserResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/users/List/";
        viewModel.fetchUsers(path, userLoginKey);
    }

    private void addAssign(AssignResult.AssignInfo assignInfo) {
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Add/";
        viewModel.addAssign(path, userLoginKey, assignInfo);
    }

    private void editAssign(AssignResult.AssignInfo assignInfo) {
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Edit/";
        viewModel.editAssign(path, userLoginKey, assignInfo);
    }

    private void fetchAssignInfo(int assignID) {
        viewModel.getSipSupporterServiceAssignResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Info/";
        viewModel.fetchAssignInfo(path, userLoginKey, assignID);
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
                if (assignInfo == null) {
                    assignInfo = new AssignResult.AssignInfo();
                    assignInfo.setCaseID(caseID);
                    assignInfo.setAssignUserID(assignUserID);
                }

                assignInfo.setDescription(binding.edTextDescription.getText().toString());

                if (assignID == 0) {
                    addAssign(assignInfo);
                } else {
                    editAssign(assignInfo);
                }
            }
        });

        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                assignUserID = userInfoArray[position].getUserID();
            }
        });
    }

    private void setupObserver() {
        viewModel.getUsersResultSingleLiveEvent().observe(this, new Observer<UserResult>() {
            @Override
            public void onChanged(UserResult userResult) {
                if (userResult.getErrorCode().equals("0")) {
                    userInfoArray = userResult.getUsers();
                    setupSpinner(userResult.getUsers());
                } else if (userResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(userResult.getError());
                }
            }
        });

        viewModel.getAddAssignResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_assign_message));
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (assignResult.getErrorCode().equals("0")) {
                    ejectUser();
                } else {
                    handleError(assignResult.getError());
                    dismiss();
                }
            }
        });

        viewModel.getEditAssignResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_assign_message));
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else if (assignResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(assignResult.getError());
                    dismiss();
                }
            }
        });

        viewModel.getAssignInfoResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    assignInfo = assignResult.getAssigns()[0];
                    initViews();
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