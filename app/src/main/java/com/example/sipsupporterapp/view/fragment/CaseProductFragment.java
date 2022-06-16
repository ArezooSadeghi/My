package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CaseProductAdapter;
import com.example.sipsupporterapp.databinding.FragmentCaseProductBinding;
import com.example.sipsupporterapp.model.CaseProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CaseProductViewModel;

import java.util.Arrays;

public class CaseProductFragment extends Fragment {
    private FragmentCaseProductBinding binding;
    private CaseProductViewModel viewModel;
    private String centerName, userLoginKey;
    private ServerData serverData;
    private int caseID;

    public static CaseProductFragment newInstance() {
        CaseProductFragment fragment = new CaseProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();

        CaseProductFragmentArgs args = CaseProductFragmentArgs.fromBundle(getArguments());
        caseID = args.getCaseID();

        fetchCaseProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_case_product,
                container,
                false);

        initViews();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(CaseProductViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCaseProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.getSipSupporterServiceCaseProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void initViews() {
        binding.recyclerViewCaseProduct.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerViewCaseProduct.addItemDecoration(dividerItemDecoration);
    }

    private void fetchCaseProducts() {
        String path = "/api/v1/CaseProduct/ListWithSelected/";
        viewModel.fetchCaseProductsWithSelected(path, userLoginKey, caseID);
    }

    private void setupObserver() {
        viewModel.getCaseProductsWithSelectedResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseProductResult>() {
            @Override
            public void onChanged(CaseProductResult caseProductResult) {
                if (caseProductResult.getErrorCode().equals("0")) {
                    setupAdapter(caseProductResult.getCaseProducts());
                } else if (caseProductResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(caseProductResult.getError());
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> handleError(msg));

        viewModel.getUpdate().observe(getViewLifecycleOwner(), new Observer<CaseProductResult.CaseProductInfo>() {
            @Override
            public void onChanged(CaseProductResult.CaseProductInfo caseProductInfo) {
                if (caseProductInfo.isSelected()) {
                    caseProductInfo.setCaseID(caseID);
                    addCaseProduct(caseProductInfo);
                } else {
                    caseProductInfo.setCaseID(0);
                    deleteCaseProduct(caseProductInfo);
                }
            }
        });

        viewModel.getAddCaseProductResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseProductResult>() {
            @Override
            public void onChanged(CaseProductResult caseProductResult) {
                if (caseProductResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                }
            }
        });

        viewModel.getDeleteCaseProductResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseProductResult>() {
            @Override
            public void onChanged(CaseProductResult caseProductResult) {
                if (caseProductResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                }
            }
        });
    }

    private void handleError(String msg) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(msg);
        dialog.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
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

    private void setupAdapter(CaseProductResult.CaseProductInfo[] caseProductInfoArray) {
        CaseProductAdapter adapter = new CaseProductAdapter(viewModel, Arrays.asList(caseProductInfoArray), false);
        binding.recyclerViewCaseProduct.setAdapter(adapter);
    }

    private void addCaseProduct(CaseProductResult.CaseProductInfo caseProductInfo) {
        String path = "/api/v1/CaseProduct/Add/";
        viewModel.addCaseProduct(path, userLoginKey, caseProductInfo);
    }

    private void deleteCaseProduct(CaseProductResult.CaseProductInfo caseProductInfo) {
        String path = "/api/v1/CaseProduct/Delete/";
        viewModel.deleteCaseProduct(path, userLoginKey, caseProductInfo.getCaseProductID());
    }
}