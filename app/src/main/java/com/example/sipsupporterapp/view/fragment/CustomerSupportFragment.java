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
import com.example.sipsupporterapp.adapter.CustomerSupportAdapter;
import com.example.sipsupporterapp.databinding.FragmentCustomerSupportBinding;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerSupportViewModel;

import java.util.Arrays;
import java.util.List;

public class CustomerSupportFragment extends Fragment {
    private FragmentCustomerSupportBinding binding;
    private CustomerSupportViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int customerID;

    private static final String ARGS_CUSTOMER_ID = "customerID";

    public static CustomerSupportFragment newInstance(int customerID) {
        CustomerSupportFragment fragment = new CustomerSupportFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CUSTOMER_ID, customerID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        createViewModel();
        fetchCustomerSupports();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_customer_support,
                container,
                false);

        initView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerSupportViewModel.class);
    }

    private void initView() {
        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtUserFullName.setText(customerName);

        binding.recyclerViewSupportHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSupportHistory.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewSupportHistory.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setupAdapter(CustomerSupportResult.CustomerSupportInfo[] customerSupportInfoArray) {
        List<CustomerSupportResult.CustomerSupportInfo> customerSupportInfoList = Arrays.asList(customerSupportInfoArray);
        CustomerSupportAdapter adapter = new CustomerSupportAdapter(
                getContext(), viewModel, customerSupportInfoList);
        binding.recyclerViewSupportHistory.setAdapter(adapter);
    }

    private void fetchCustomerSupports() {
        viewModel.getSipSupporterServiceCustomerSupportResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerSupports/ListByCustomer/";
        viewModel.fetchCustomerSupports(path, userLoginKey, customerID);
    }

    private void setupObserver() {
        viewModel.getCustomerSupportsResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<CustomerSupportResult>() {
                    @Override
                    public void onChanged(CustomerSupportResult customerSupportResult) {
                        binding.progressBar.setVisibility(View.GONE);

                        if (customerSupportResult.getErrorCode().equals("0")) {
                            binding.recyclerViewSupportHistory.setVisibility(View.VISIBLE);

                            StringBuilder stringBuilder = new StringBuilder();
                            String listSize = String.valueOf(customerSupportResult.getCustomerSupports().length);

                            for (int i = 0; i < listSize.length(); i++) {
                                stringBuilder.append((char) ((int) listSize.charAt(i) - 48 + 1632));
                            }

                            binding.txtCount.setText("تعداد پشتیبانی ها: " + stringBuilder.toString());
                            setupAdapter(customerSupportResult.getCustomerSupports());
                        } else if (customerSupportResult.getErrorCode().equals("-9001")) {
                            ejectUser();
                        } else {
                            handleError(customerSupportResult.getError());
                        }
                    }
                });

        viewModel.getNoConnectionExceptionSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBar.setVisibility(View.GONE);
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBar.setVisibility(View.GONE);
                handleError(message);
            }
        });

        viewModel.getSeeCustomerSupportAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<CustomerSupportResult.CustomerSupportInfo>() {
            @Override
            public void onChanged(CustomerSupportResult.CustomerSupportInfo customerSupportInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(
                        getContext(),
                        customerSupportInfo.getCustomerSupportID(),
                        0,
                        0,
                        0);
                startActivity(starter);
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
}