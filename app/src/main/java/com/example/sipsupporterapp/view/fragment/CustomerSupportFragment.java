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
import com.example.sipsupporterapp.model.CustomerSupportInfo;
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

    private void fetchCustomerSupports() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceCustomerSupportResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerSupports/ListByCustomer/";
        viewModel.fetchCustomerSupports(path, userLoginKey, customerID);
    }

    private void initView() {
        String customerName = Converter.convert(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtUserFullName.setText(customerName);
        binding.recyclerViewSupportHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSupportHistory.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewSupportHistory.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setupObserver() {
        viewModel.getCustomerSupportsResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<CustomerSupportResult>() {
                    @Override
                    public void onChanged(CustomerSupportResult customerSupportResult) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.recyclerViewSupportHistory.setVisibility(View.VISIBLE);

                        StringBuilder stringBuilder = new StringBuilder();
                        String listSize = String.valueOf(customerSupportResult.getCustomerSupports().length);

                        for (int i = 0; i < listSize.length(); i++) {
                            stringBuilder.append((char) ((int) listSize.charAt(i) - 48 + 1632));
                        }

                        binding.txtCount.setText("تعداد پشتیبانی ها: " + stringBuilder.toString());
                        setupAdapter(customerSupportResult.getCustomerSupports());
                    }
                });

        viewModel.getErrorCustomerSupportsResultSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String message) {
                        binding.progressBar.setVisibility(View.GONE);
                        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getNoConnectionExceptionSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBar.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String message) {
                        binding.progressBar.setVisibility(View.GONE);
                        ErrorDialogFragment fragment = ErrorDialogFragment
                                .newInstance(message);
                        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getDangerousUserSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isDangerousUser) {
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

        viewModel.getSeeCustomerSupportAttachmentsClicked()
                .observe(getViewLifecycleOwner(), new Observer<CustomerSupportInfo>() {
                    @Override
                    public void onChanged(CustomerSupportInfo customerSupportInfo) {
                        Intent starter = PhotoGalleryContainerActivity.start(getContext(), customerSupportInfo.getCustomerSupportID(), 0, 0, 0);
                        startActivity(starter);
                    }
                });
    }

    private void setupAdapter(CustomerSupportInfo[] customerSupportInfoArray) {
        List<CustomerSupportInfo> customerSupportInfoList = Arrays.asList(customerSupportInfoArray);
        CustomerSupportAdapter adapter = new CustomerSupportAdapter(
                getContext(), viewModel, customerSupportInfoList);
        binding.recyclerViewSupportHistory.setAdapter(adapter);
    }
}