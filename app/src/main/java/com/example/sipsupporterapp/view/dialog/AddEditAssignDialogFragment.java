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

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditAssignDialogBinding;
import com.example.sipsupporterapp.model.AssignInfo;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserInfo;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.AssignViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class AddEditAssignDialogFragment extends DialogFragment {
    private FragmentAddEditAssignDialogBinding binding;
    private AssignViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int assignUserID;
    private List<String> userFullNames = new ArrayList<>();
    private List<Integer> userIDs = new ArrayList<>();

    private static final String ARGS_ASSIGN_ID = "assignID";
    private static final String ARGS_CASE_ID = "caseID";
    private static final String ARGS_DESCRIPTION = "description";
    public static final String TAG = AddEditAssignDialogFragment.class.getSimpleName();

    public static AddEditAssignDialogFragment newInstance(int assignID, int caseID, String description) {
        AddEditAssignDialogFragment fragment = new AddEditAssignDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ARGS_ASSIGN_ID, assignID);
        args.putInt(ARGS_CASE_ID, caseID);
        args.putString(ARGS_DESCRIPTION, description);

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

        fetchUsers();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_edit_assign_dialog,
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
        viewModel = new ViewModelProvider(requireActivity()).get(AssignViewModel.class);
    }

    private void setupObserver() {
        viewModel.getUsersResultSingleLiveEvent().observe(this, new Observer<UserResult>() {
            @Override
            public void onChanged(UserResult userResult) {
                if (userResult.getErrorCode().equals("0")) {
                    setupSpinner(userResult.getUsers());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(userResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getAddAssignResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("موفقیت آمیز بود Assign");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefreshAssigns().setValue(true);
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(assignResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    dismiss();
                }
            }
        });

        viewModel.getEditAssignResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("موفقیت آمیز بود Assign");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefreshAssigns().setValue(true);
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(assignResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    dismiss();
                }
            }
        });
    }

    private void fetchUsers() {
        viewModel.getSipSupporterServiceUsers(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/users/List/";
        viewModel.fetchUsers(path, userLoginKey);
    }

    private void initViews() {
        String description = getArguments().getString(ARGS_DESCRIPTION);
        binding.edTextDescription.setText(description);
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
                AssignInfo assignInfo = new AssignInfo();
                assignInfo.setDescription(binding.edTextDescription.getText().toString());

                int caseID = getArguments().getInt(ARGS_CASE_ID);
                assignInfo.setCaseID(caseID);

                assignInfo.setAssignUserID(assignUserID);

                int assignID = getArguments().getInt(ARGS_ASSIGN_ID);
                assignInfo.setAssignID(assignID);

                if (assignID == 0) {
                    addAssign(assignInfo);
                } else {
                    editAssign(assignInfo);
                }
            }
        });

        binding.spinnerAssignUserFullName.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String userFullName = (String) item;
                for (int i = 0; i < userFullNames.size(); i++) {
                    if (userFullNames.get(i).equals(userFullName)) {
                        assignUserID = userIDs.get(i);
                        break;
                    }
                }
            }
        });
    }

    private void setupSpinner(UserInfo[] userInfoArray) {
        for (int i = 0; i < userInfoArray.length; i++) {
            userFullNames.add(i, userInfoArray[i].getUserFullName());
            userIDs.add(i, userInfoArray[i].getUserID());
        }
        assignUserID = userIDs.get(0);
        binding.spinnerAssignUserFullName.setItems(userFullNames);
    }

    private void addAssign(AssignInfo assignInfo) {
        viewModel.getSipSupporterServiceAddAssign(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Add/";
        viewModel.addAssign(path, userLoginKey, assignInfo);
    }

    private void editAssign(AssignInfo assignInfo) {
        viewModel.getSipSupporterServiceEditAssign(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Edit/";
        viewModel.editAssign(path, userLoginKey, assignInfo);
    }
}