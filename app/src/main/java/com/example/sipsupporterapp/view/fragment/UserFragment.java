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
import com.example.sipsupporterapp.adapter.UserAdapter;
import com.example.sipsupporterapp.databinding.FragmentUserBinding;
import com.example.sipsupporterapp.model.CustomerUserInfo;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.MainActivity;
import com.example.sipsupporterapp.view.dialog.AddEditCustomerSupportDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.List;

public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private UserViewModel viewModel;

    private int customerID;
    private String date;

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

        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);

        createViewModel();
        getDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user,
                container,
                false);

        initViews();
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

    private void getDate() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceDateResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/common/getDate/";
        viewModel.fetchDate(path, userLoginKey);
    }

    private void initViews() {
        binding.txtCustomerName.setText(SipSupportSharedPreferences.getCustomerName(getContext()));
        binding.recyclerViewCustomerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCustomerUsers.addItemDecoration(new DividerItemDecoration(
                binding.recyclerViewCustomerUsers.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void fetchUsers() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceUsersResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerUsers/userList/";
        viewModel.fetchUsers(path, userLoginKey, customerID);
    }

    private void setupObserver() {
        viewModel.getUsersResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CustomerUserResult>() {
            @Override
            public void onChanged(CustomerUserResult customerUserResult) {
                binding.progressBar.setVisibility(View.GONE);

                if (customerUserResult.getErrorCode().equals("0")) {
                    binding.recyclerViewCustomerUsers.setVisibility(View.VISIBLE);

                    String str = (customerUserResult.getCustomerUsers().length) + "";
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < str.length(); i++) {
                        stringBuilder.append((char) ((int) str.charAt(i) - 48 + 1632));
                    }
                    binding.txtCount.setText("تعداد کاربران: " + stringBuilder.toString());
                    setupAdapter(customerUserResult.getCustomerUsers());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(customerUserResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBar.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBar.setVisibility(View.GONE);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getDangerousUserSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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

        viewModel.getItemClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer customerID) {
                AddEditCustomerSupportDialogFragment fragment = AddEditCustomerSupportDialogFragment.newInstance(customerID);
                fragment.show(getActivity().getSupportFragmentManager(), AddEditCustomerSupportDialogFragment.TAG);
            }
        });

        viewModel.getDateResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<DateResult>() {
            @Override
            public void onChanged(DateResult dateResult) {
                date = dateResult.getDate();
            }
        });

        viewModel.getSuccessfulRegisterCustomerUsersSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccessfulRegister) {
                Intent starter = MainActivity.start(getContext());
                starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(starter);
                getActivity().finish();
            }
        });
    }

    private void setupAdapter(CustomerUserInfo[] customerUserInfoArray) {
        List<CustomerUserInfo> customerUserInfoList = Arrays.asList(customerUserInfoArray);
        UserAdapter adapter = new UserAdapter(getContext(), viewModel, customerUserInfoList, date);
        binding.recyclerViewCustomerUsers.setAdapter(adapter);
    }
}