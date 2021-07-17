package com.example.sipsupporterapp.view.fragment;

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
import com.example.sipsupporterapp.adapter.CustomerAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerSearchBinding;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;

import java.util.Arrays;

public class CustomerSearchFragment extends Fragment {
    private FragmentCustomerSearchBinding binding;
    private CustomerViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;

    public static CustomerSearchFragment newInstance() {
        CustomerSearchFragment fragment = new CustomerSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_customer_search,
                container,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSupporterServiceCustomerResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void initViews() {
        binding.recyclerViewCustomer.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.recyclerViewCustomer.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewCustomer.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void handleEvents() {
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                fetchCustomer();
            }
        });
    }

    private void fetchCustomer() {
        String path = "/api/v1/customers/search/";
        viewModel.fetchCustomers(path, userLoginKey, binding.edTextSearch.getText().toString());
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void setupAdapter(CustomerResult.CustomerInfo[] customerInfoArray) {
        CustomerAdapter adapter = new CustomerAdapter(getContext(), viewModel, Arrays.asList(customerInfoArray), SipSupportSharedPreferences.getDate(getContext()), true);
        binding.recyclerViewCustomer.setAdapter(adapter);
    }

    private void setupObserver() {
        viewModel.getCustomersResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerResult>() {
            @Override
            public void onChanged(CustomerResult customerResult) {
                binding.progressBarLoading.setVisibility(View.INVISIBLE);
                if (customerResult.getErrorCode().equals("0")) {
                    binding.recyclerViewCustomer.setVisibility(View.VISIBLE);
                    setupAdapter(customerResult.getCustomers());
                } else {
                    handleError(customerResult.getError());
                }
            }
        });
    }
}