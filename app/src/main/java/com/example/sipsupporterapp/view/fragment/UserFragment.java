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
import com.example.sipsupporterapp.adapter.UserAdapter;
import com.example.sipsupporterapp.databinding.BaseLayoutBinding;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.MainActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerSupportDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.UserViewModel;

import java.util.Arrays;

public class UserFragment extends Fragment {
    private BaseLayoutBinding binding;
    private UserViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private String date;
    private int customerID;

    private static final String ARGS_CUSTOMER_ID = "customerID";

    public static UserFragment newInstance(int customerID) {
        UserFragment fragment = new UserFragment();
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
        getDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.base_layout,
                container,
                false);

        initViews();
        handleEvents();
        fetchUsers();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    private void initVariables() {
        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void getDate() {
        viewModel.getSipSupporterServiceDateResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/common/getDate/";
        viewModel.fetchDate(path, userLoginKey);
    }

    private void fetchUsers() {
        viewModel.getSipSupporterServiceCustomerUserResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerUsers/userList/";
        viewModel.fetchUsers(path, userLoginKey, customerID);
    }

    private void initViews() {
        String customerName = Converter.letterConverter(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.txtCustomerName.setText(customerName);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
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

    private void setupAdapter(CustomerUserResult.CustomerUserInfo[] customerUserInfoArray) {
        binding.txtEmpty.setVisibility(customerUserInfoArray.length == 0 ? View.VISIBLE : View.GONE);
        UserAdapter adapter = new UserAdapter(viewModel, Arrays.asList(customerUserInfoArray), date);
        binding.recyclerView.setAdapter(adapter);
    }

    private void handleEvents() {
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupObserver() {
        viewModel.getUsersResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerUserResult>() {
            @Override
            public void onChanged(CustomerUserResult customerUserResult) {
                binding.progressBarLoading.setVisibility(View.GONE);
                if (customerUserResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    String count = Converter.numberConverter(String.valueOf(customerUserResult.getCustomerUsers().length));
                    binding.txtCount.setText("تعداد کاربران: " + count);
                    setupAdapter(customerUserResult.getCustomerUsers());
                } else if (customerUserResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerUserResult.getError());
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

        viewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<CustomerUserResult.CustomerUserInfo>() {
            @Override
            public void onChanged(CustomerUserResult.CustomerUserInfo customerUserInfo) {
                SipSupportSharedPreferences.setCustomerUserId(getContext(), customerUserInfo.getCustomerUserID());
                AddEditCustomerSupportDialogFragment fragment = AddEditCustomerSupportDialogFragment.newInstance(customerUserInfo.getCustomerID());
                fragment.show(getActivity().getSupportFragmentManager(), AddEditCustomerSupportDialogFragment.TAG);
            }
        });

        viewModel.getDateResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<DateResult>() {
            @Override
            public void onChanged(DateResult dateResult) {
                if (dateResult.getErrorCode().equals("0")) {
                    date = dateResult.getDate();
                }
            }
        });

        viewModel.getRefresh().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccessfulRegister) {
                Intent starter = MainActivity.start(getContext());
                starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(starter);
                getActivity().finish();
            }
        });
    }
}