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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CustomerAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerBinding;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.ItemClickedContainerActivity;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;

import java.util.Arrays;

public class CustomerFragment extends Fragment {
    private FragmentCustomerBinding binding;
    private CustomerViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
        fetchDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_customer,
                container,
                false);

        initViews();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CustomerFragmentArgs args = CustomerFragmentArgs.fromBundle(getArguments());
        flag = args.getFlag();
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setupAdapter(CustomerResult.CustomerInfo[] customerInfoArray) {
        binding.txtEmpty.setVisibility(customerInfoArray.length == 0 ? View.VISIBLE : View.GONE);
        String date = SipSupportSharedPreferences.getDate(getContext());
        CustomerAdapter adapter = new CustomerAdapter(viewModel, Arrays.asList(customerInfoArray), date);
        binding.recyclerView.setAdapter(adapter);
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
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

    private void fetchDate() {
        viewModel.getSipSupporterServiceDateResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/common/getDate/";
        viewModel.fetchDate(path, userLoginKey);
    }

    private void fetchCustomers(String searchQuery) {
        viewModel.getSupporterServiceCustomerResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customers/search";
        viewModel.fetchCustomers(path, userLoginKey, searchQuery);
    }

    private void setupObserver() {
        viewModel.getCustomersResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerResult>() {
            @Override
            public void onChanged(CustomerResult customerResult) {
                binding.progressBarLoading.setVisibility(View.GONE);

                if (customerResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    setupAdapter(customerResult.getCustomers());
                } else if (customerResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                handleError(message);
            }
        });

        viewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<CustomerResult.CustomerInfo>() {
            @Override
            public void onChanged(CustomerResult.CustomerInfo customerInfo) {
                SipSupportSharedPreferences.setCustomerName(getContext(), customerInfo.getCustomerName());
                SipSupportSharedPreferences.setCustomerTel(getContext(), customerInfo.getTel());
                if (flag) {
                    CustomerFragmentDirections.ActionMenuSearchToMenuTasks action = CustomerFragmentDirections.actionMenuSearchToMenuTasks();
                    action.setCustomerID(customerInfo.getCustomerID());
                    NavHostFragment.findNavController(CustomerFragment.this).navigate(action);
                } else {
                    Intent starter = ItemClickedContainerActivity.start(getContext(), customerInfo.getCustomerID());
                    startActivity(starter);
                }
            }
        });

        viewModel.getDateResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<DateResult>() {
            @Override
            public void onChanged(DateResult dateResult) {
                if (dateResult != null) {
                    if (dateResult.getErrorCode().equals("0")) {
                        SipSupportSharedPreferences.setDate(getContext(), dateResult.getDate());
                    }
                }
            }
        });

        viewModel.getSearchQuery().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String searchQuery) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                fetchCustomers(searchQuery);
            }
        });
    }
}