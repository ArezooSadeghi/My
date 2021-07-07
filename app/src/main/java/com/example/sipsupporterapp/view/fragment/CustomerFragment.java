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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.CustomerAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerBinding;
import com.example.sipsupporterapp.eventbus.PostCustomerIDEvent;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.ItemClickedContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CustomerFragment extends Fragment {
    private FragmentCustomerBinding binding;
    private CustomerViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;

    public static CustomerFragment newInstance() {
        CustomerFragment fragment = new CustomerFragment();
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

        if (SipSupportSharedPreferences.getLastSearchQuery(getContext()) != null) {
            binding.progressBarLoading.setVisibility(View.VISIBLE);
            viewModel.getSupporterServiceCustomerResult(serverData.getIpAddress() + ":" + serverData.getPort());
            String path = "/api/v1/customers/";
            viewModel.fetchCustomersResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), SipSupportSharedPreferences.getLastSearchQuery(getContext()));
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);
    }

    private void fetchDate() {
        viewModel.getSipSupporterServiceDateResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/common/getDate/";
        viewModel.fetchDateResult(path, userLoginKey);
    }

    private void initViews() {
        binding.recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCustomers.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewCustomers.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter(List<CustomerResult.CustomerInfo> customerInfoList) {
        CustomerAdapter adapter = new CustomerAdapter(
                getContext(),
                viewModel, customerInfoList, SipSupportSharedPreferences.getDate(getContext()));
        binding.recyclerViewCustomers.setAdapter(adapter);
    }

    private void setupObserver() {
        viewModel.getCustomersResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerResult>() {
            @Override
            public void onChanged(CustomerResult customerResult) {
                binding.progressBarLoading.setVisibility(View.GONE);

                if (customerResult.getErrorCode().equals("0")) {
                    binding.recyclerViewCustomers.setVisibility(View.VISIBLE);
                    List<CustomerResult.CustomerInfo> customerInfoList = new ArrayList<>();
                    for (CustomerResult.CustomerInfo customerInfo : customerResult.getCustomers()) {
                        customerInfoList.add(customerInfo);
                    }
                    setupAdapter(customerInfoList);
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                binding.progressBarLoading.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(error);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getItemClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer customerID) {
                Intent starter = ItemClickedContainerActivity.start(getContext(), customerID);
                startActivity(starter);
            }
        });

        viewModel.getDateResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<DateResult>() {
            @Override
            public void onChanged(DateResult dateResult) {
                SipSupportSharedPreferences.setDate(getContext(), dateResult.getDate());
            }
        });

        viewModel.getShowProgressBarSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getSearchQuery().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String searchQuery) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                viewModel.getSupporterServiceCustomerResult(serverData.getIpAddress() + ":" + serverData.getPort());
                String path = "/api/v1/customers/search";
                viewModel.fetchCustomersResult(path, userLoginKey, searchQuery);
            }
        });

        viewModel.getNavigateToAddEditCaseDialog().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list) {
                EventBus.getDefault().postSticky(new PostCustomerIDEvent(Integer.valueOf(list.get(0)), list.get(1)));
                NavHostFragment.findNavController(CustomerFragment.this).navigate(R.id.menu_tasks);
            }
        });
    }
}