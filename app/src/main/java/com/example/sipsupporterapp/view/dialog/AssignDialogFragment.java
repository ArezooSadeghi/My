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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.AssignAdapter;
import com.example.sipsupporterapp.databinding.FragmentAssignDialogBinding;
import com.example.sipsupporterapp.model.AssignInfo;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.AssignViewModel;

import java.util.Arrays;

public class AssignDialogFragment extends DialogFragment {
    private FragmentAssignDialogBinding binding;
    private AssignViewModel viewModel;
    private int caseID, assignID;
    private ServerData serverData;
    private String centerName, userLoginKey;

    private static final String ARGS_CASE_ID = "caseID";
    public static final String TAG = AssignDialogFragment.class.getSimpleName();

    public static AssignDialogFragment newInstance(int caseID) {
        AssignDialogFragment fragment = new AssignDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CASE_ID, caseID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        caseID = getArguments().getInt(ARGS_CASE_ID);

        createViewModel();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        fetchAssigns();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_assign_dialog,
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

    private void fetchAssigns() {
        viewModel.getSipSupporterServiceAssigns(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/List/";
        viewModel.fetchAssigns(path, userLoginKey, caseID);
    }

    private void setupObserver() {
        viewModel.getEditClicked().observe(this, new Observer<AssignInfo>() {
            @Override
            public void onChanged(AssignInfo assignInfo) {
                AddEditAssignDialogFragment fragment = AddEditAssignDialogFragment.newInstance(assignInfo.getAssignID(), assignInfo.getCaseID(), assignInfo.getDescription());
                fragment.show(getParentFragmentManager(), AddEditAssignDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer assign_ID) {
                assignID = assign_ID;
                QuestionDeleteAssignDialogFragment fragment = QuestionDeleteAssignDialogFragment.newInstance("آیا می خواهید assign موردنظر را حذف کنید؟");
                fragment.show(getParentFragmentManager(), QuestionDeleteAssignDialogFragment.TAG);
            }
        });

        viewModel.getYesDeleteClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDeleteClicked) {
                deleteAssign();
            }
        });

        viewModel.getDeleteAssignResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("assign موردنظر با موفقیت حذف شد");
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    fetchAssigns();
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(assignResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    dismiss();
                }
            }
        });

        viewModel.getRegisterCommentClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean registerCommentClicked) {
                //TODO
            }
        });

        viewModel.getAssignsResultSingleLiveEvent().observe(this, new Observer<AssignResult>() {
            @Override
            public void onChanged(AssignResult assignResult) {
                if (assignResult.getErrorCode().equals("0")) {
                    setupAdapter(assignResult.getAssigns());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(assignResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getRefreshAssigns().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refreshAssigns) {
                fetchAssigns();
            }
        });
    }

    private void initViews() {
        binding.recyclerViewAssigns.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewAssigns.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewAssigns.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.fabAddNewAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditAssignDialogFragment fragment = AddEditAssignDialogFragment.newInstance(0, caseID, "");
                fragment.show(getParentFragmentManager(), AddEditAssignDialogFragment.TAG);
            }
        });
    }

    private void setupAdapter(AssignInfo[] assignInfoArray) {
        if (assignInfoArray.length != 0) {
            binding.txtNoAssign.setVisibility(View.INVISIBLE);
            binding.recyclerViewAssigns.setVisibility(View.VISIBLE);
            AssignAdapter adapter = new AssignAdapter(getContext(), viewModel, Arrays.asList(assignInfoArray));
            binding.recyclerViewAssigns.setAdapter(adapter);
        }
    }

    private void deleteAssign() {
        viewModel.getSipSupporterServiceDeleteAssign(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/Assign/Delete/";
        viewModel.deleteAssign(path, userLoginKey, assignID);
    }
}