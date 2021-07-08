package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.ProductAdapter;
import com.example.sipsupporterapp.databinding.FragmentCaseProductsBinding;
import com.example.sipsupporterapp.model.CaseProductResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CaseProductsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaseProductsFragment extends Fragment {
    private FragmentCaseProductsBinding binding;
    private CaseProductsViewModel viewModel;
    private String centerName, userLoginKey;
    private ServerData serverData;
    private List<ProductResult.ProductInfo> productInfoList = new ArrayList<>();

    public static CaseProductsFragment newInstance() {
        CaseProductsFragment fragment = new CaseProductsFragment();
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

        fetchProductGroups();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_case_products,
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

    private void initViews() {
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProducts.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewProducts.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(CaseProductsViewModel.class);
    }

    private void fetchProductGroups() {
        viewModel.getSipSupporterServiceCaseProductsWithSelected(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/CaseProduct/ListWithSelected/";
        viewModel.fetchCaseProductsWithSelected(path, userLoginKey, 80);
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

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });
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

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
    }

    private void setupAdapter(CaseProductResult.CaseProductInfo[] caseProductInfoArray) {
        ProductAdapter adapter = new ProductAdapter(getContext(), Arrays.asList(caseProductInfoArray));
        binding.recyclerViewProducts.setAdapter(adapter);
    }
}