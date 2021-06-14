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
import com.example.sipsupporterapp.adapter.CustomerAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerBinding;
import com.example.sipsupporterapp.model.CustomerInfo;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.ItemClickedContainerActivity;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.PopupDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;

import java.util.ArrayList;
import java.util.List;

public class CustomerFragment extends Fragment {
    private FragmentCustomerBinding binding;
    private CustomerViewModel viewModel;


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
        handleEvents();

        if (SipSupportSharedPreferences.getLastSearchQuery(getContext()) != null) {
            binding.progressBarLoading.setVisibility(View.VISIBLE);
            ServerData serverData = viewModel
                    .getServerData(SipSupportSharedPreferences.getCenterName(getContext()));
            viewModel.getSupporterServicePostCustomerParameter(
                    serverData.getIpAddress() + ":" + serverData.getPort());
            String path = "/api/v1/customers/";
            viewModel.fetchCustomersResult(path, SipSupportSharedPreferences.getUserLoginKey(getContext()), SipSupportSharedPreferences.getLastSearchQuery(getContext()));
        }

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setObserver();
    }


    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);
    }


    private void fetchDate() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceDateResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/common/getDate/";
        viewModel.fetchDateResult(path, userLoginKey);
    }


    private void setObserver() {
        viewModel.getCustomersResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<CustomerResult>() {
                    @Override
                    public void onChanged(CustomerResult customerResult) {
                        binding.progressBarLoading.setVisibility(View.GONE);

                        StringBuilder stringBuilder = new StringBuilder();
                        String listSize = String.valueOf(customerResult.getCustomers().length);

                        for (int i = 0; i < listSize.length(); i++) {
                            stringBuilder.append((char) ((int) listSize.charAt(i) - 48 + 1632));
                        }

                        binding.txtCount.setText("تعداد مراکز: " + stringBuilder.toString());
                        binding.progressBarLoading.setVisibility(View.GONE);
                        binding.recyclerViewCustomerList.setVisibility(View.VISIBLE);
                        List<CustomerInfo> customerInfoList = new ArrayList<>();
                        for (CustomerInfo customerInfo : customerResult.getCustomers()) {
                            customerInfoList.add(customerInfo);
                        }
                        setupAdapter(customerInfoList);
                    }
                });

        viewModel.getErrorCustomersResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                binding.progressBarLoading.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(error);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
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

        viewModel.getTimeoutExceptionHappenSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String message) {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        ErrorDialogFragment fragment = ErrorDialogFragment
                                .newInstance(message);
                        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getDangerousUserSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
                        SipSupportSharedPreferences.setUserFullName(getContext(), null);
                        SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
                        SipSupportSharedPreferences.setCustomerName(getContext(), null);
                        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
                        SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
                        Intent intent = LoginContainerActivity.start(getContext());
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        viewModel.getItemClicked()
                .observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer customerID) {
                        Intent intent = ItemClickedContainerActivity.start(getContext(), customerID);
                        startActivity(intent);
                    }
                });

        viewModel.getDateResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<DateResult>() {
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
    }


    private void initViews() {
        String userName = Converter.convert(SipSupportSharedPreferences.getUserFullName(getContext()));
        binding.txtUserName.setText(userName);
        binding.recyclerViewCustomerList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCustomerList.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewCustomerList.getContext(),
                DividerItemDecoration.VERTICAL));
    }


    private void handleEvents() {
        binding.imgBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupDialogFragment fragment = PopupDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), PopupDialogFragment.TAG);
            }
        });

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarLoading.setVisibility(View.VISIBLE);
                String centerName = SipSupportSharedPreferences.getCenterName(getContext());
                String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
                ServerData serverData = viewModel.getServerData(centerName);
                viewModel.getSupporterServicePostCustomerParameter(serverData.getIpAddress() + ":" + serverData.getPort());
                String path = "/api/v1/customers/search";
                viewModel.fetchCustomersResult(path, userLoginKey, binding.edTextSearch.getText().toString());
            }
        });
    }


    private void setupAdapter(List<CustomerInfo> customerInfoList) {
        CustomerAdapter adapter = new CustomerAdapter(
                getContext(),
                viewModel, customerInfoList, SipSupportSharedPreferences.getDate(getContext()));
        binding.recyclerViewCustomerList.setAdapter(adapter);
    }
}