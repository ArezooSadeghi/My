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
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentChangeCaseTypeDialogBinding;
import com.example.sipsupporterapp.eventbus.CaseTypesEvent;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ChangeCaseTypeDialogFragment extends DialogFragment {
    private FragmentChangeCaseTypeDialogBinding binding;
    private CaseViewModel viewModel;

    private int caseTypeID;
    private String centerName, userLoginKey;
    private ServerData serverData;
    private List<String> caseTypes = new ArrayList<>();
    private List<Integer> caseTypeIDs = new ArrayList<>();

    public static final String TAG = ChangeCaseTypeDialogFragment.class.getSimpleName();

    public static ChangeCaseTypeDialogFragment newInstance() {
        ChangeCaseTypeDialogFragment fragment = new ChangeCaseTypeDialogFragment();
        Bundle args = new Bundle();
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
                viewModel.getSaveClicked().setValue(caseTypeID);
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

    private void setupSpinner(CaseTypeResult.CaseTypeInfo[] caseTypeInfoArray) {
        for (int i = 0; i < caseTypeInfoArray.length; i++) {
            caseTypes.add(i, caseTypeInfoArray[i].getCaseType());
            caseTypeIDs.add(i, caseTypeInfoArray[i].getCaseTypeID());
        }
        caseTypeID = caseTypeIDs.get(0);
        binding.spinnerCaseTypes.setItems(caseTypes);
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
    public void getEvent(CaseTypesEvent event) {
        setupSpinner(event.getCaseTypeResult().getCaseTypes());
    }

    private void fetchCaseTypes() {
        viewModel.getSipSupporterServiceCaseTypeResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/caseType/List/";
        viewModel.fetchCaseTypes(path, userLoginKey);
    }
}