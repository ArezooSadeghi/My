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
import com.example.sipsupporterapp.adapter.CustomerSupportAdapter;
import com.example.sipsupporterapp.databinding.BaseLayoutBinding;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CustomerSupportViewModel;

import java.util.Arrays;

public class CustomerSupportFragment extends Fragment {
    private BaseLayoutBinding binding;
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

        createViewModel();
        initVariables();
        fetchCustomerSupports(customerID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.base_layout,
                container,
                false);

        initView();
        handleEvents();

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

    private void initVariables() {
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initView() {
        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void handleEvents() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupAdapter(CustomerSupportResult.CustomerSupportInfo[] customerSupportInfoArray) {
        binding.txtEmpty.setVisibility(customerSupportInfoArray.length == 0 ? View.VISIBLE : View.GONE);
        CustomerSupportAdapter adapter = new CustomerSupportAdapter(getContext(), viewModel, Arrays.asList(customerSupportInfoArray));
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
        SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void fetchCustomerSupports(int customerID) {
        viewModel.getSipSupporterServiceCustomerSupportResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerSupports/ListByCustomer/";
        viewModel.fetchCustomerSupports(path, userLoginKey, customerID);
    }

    private void setupObserver() {
        viewModel.getCustomerSupportsResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<CustomerSupportResult>() {
                    @Override
                    public void onChanged(CustomerSupportResult customerSupportResult) {
                        binding.progressBarLoading.setVisibility(View.GONE);
                        if (customerSupportResult.getErrorCode().equals("0")) {
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            String count = Converter.numberConverter(String.valueOf(customerSupportResult.getCustomerSupports().length));
                            binding.txtCount.setText("تعداد پشتیبانی ها: " + count);
                            setupAdapter(customerSupportResult.getCustomerSupports());
                        } else if (customerSupportResult.getErrorCode().equals("-9001")) {
                            ejectUser();
                        } else {
                            handleError(customerSupportResult.getError());
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
}