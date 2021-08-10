package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentLoginBinding;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.MainActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.IPAddressListDialogFragment;
import com.example.sipsupporterapp.view.dialog.RequireIPAddressDialogFragment;
import com.example.sipsupporterapp.viewmodel.LoginViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private String spinnerValue;
    private IPAddressListDialogFragment fragment;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();

        if (viewModel.getServerDataList().size() == 0) {
            RequireIPAddressDialogFragment fragment = RequireIPAddressDialogFragment.newInstance();
            fragment.show(getParentFragmentManager(), RequireIPAddressDialogFragment.TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_login,
                container,
                false);

        if (SipSupportSharedPreferences.getUserName(getContext()) != null) {
            binding.edTextUserName.setText(SipSupportSharedPreferences.getUserName(getContext()));
            binding.edTextUserName.setSelection(binding.edTextUserName.getText().toString().length());
        }

        if (viewModel.getServerDataList().size() != 0) {
            setupSpinner();
        }

        handleEvents();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private void setupSpinner() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        List<ServerData> serverDataList = viewModel.getServerDataList();
        List<String> centerNameList = new ArrayList<>();
        for (ServerData serverData : serverDataList) {
            centerNameList.add(serverData.getCenterName());
        }

        List<String> newCenterNameList = new ArrayList<>();

        if (centerName != null) {
            for (String center_Name : centerNameList) {
                if (!center_Name.equals(center_Name)) {
                    newCenterNameList.add(center_Name);
                }
            }
            newCenterNameList.add(0, centerName);
            binding.spinner.setItems(newCenterNameList);

        } else {
            binding.spinner.setItems(centerNameList);
        }
        if (binding.spinner.getItems().size() > 0) {
            spinnerValue = (String) binding.spinner.getItems().get(0);
        }
    }

    private void handleEvents() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (viewModel.getServerDataList() != null & viewModel.getServerDataList().size() == 0) {
                        RequireIPAddressDialogFragment fragment = RequireIPAddressDialogFragment.newInstance();
                        fragment.show(getParentFragmentManager(), RequireIPAddressDialogFragment.TAG);
                    } else {
                        binding.edTextPassword.setEnabled(false);
                        binding.edTextUserName.setEnabled(false);
                        binding.btnLogin.setEnabled(false);
                        binding.ivMore.setEnabled(false);

                        String userName = binding.edTextUserName.getText().toString().replaceAll(" ", "");
                        String password = binding.edTextPassword.getText().toString().replaceAll(" ", "");

                        UserResult.UserLoginParameter userLoginParameter = new UserResult().new UserLoginParameter(userName, password);

                        if (spinnerValue != null) {
                            ServerData serverData = viewModel.getServerData(spinnerValue);
                            viewModel.getSipSupporterServiceUserResult(serverData.getIpAddress() + ":" + serverData.getPort());
                            String path = "/api/v1/users/Login/";
                            viewModel.fetchUserResult(path, userLoginParameter);
                            binding.loadingLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception exception) {
                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = IPAddressListDialogFragment.newInstance();
                fragment.show(getParentFragmentManager(), IPAddressListDialogFragment.TAG);
            }
        });

        binding.edTextUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.edTextPassword.requestFocus();
                }
                return false;
            }
        });

        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                spinnerValue = (String) item;
            }
        });
    }

    private void setupObserver() {
        viewModel.getUserResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<UserResult>() {
            @Override
            public void onChanged(UserResult userResult) {
                binding.loadingLayout.setVisibility(View.GONE);
                binding.edTextPassword.setEnabled(true);
                binding.edTextUserName.setEnabled(true);
                binding.btnLogin.setEnabled(true);
                binding.ivMore.setEnabled(true);

                if (userResult.getErrorCode().equals("0")) {
                    SipSupportSharedPreferences.setUserName(getContext(), binding.edTextUserName.getText().toString());
                    SipSupportSharedPreferences.setCenterName(getContext(), spinnerValue);
                    binding.loadingLayout.setVisibility(View.GONE);
                    UserResult.UserInfo[] userInfoArray = userResult.getUsers();
                    if (userInfoArray.length != 0) {
                        SipSupportSharedPreferences.setUserLoginKey(getContext(), userInfoArray[0].getUserLoginKey());
                        SipSupportSharedPreferences.setUserFullName(getContext(), userInfoArray[0].getUserFullName());

                        Intent starter = MainActivity.start(getContext());
                        startActivity(starter);
                        getActivity().finish();
                    }
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(userResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                binding.loadingLayout.setVisibility(View.GONE);
                binding.edTextPassword.setEnabled(true);
                binding.edTextUserName.setEnabled(true);
                binding.btnLogin.setEnabled(true);
                binding.ivMore.setEnabled(true);

                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(error);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String message) {
                        binding.loadingLayout.setVisibility(View.GONE);
                        binding.edTextPassword.setEnabled(true);
                        binding.edTextUserName.setEnabled(true);
                        binding.btnLogin.setEnabled(true);
                        binding.ivMore.setEnabled(true);

                        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getWrongIpAddressSingleLiveEvent()
                .observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String error) {
                        binding.loadingLayout.setVisibility(View.GONE);
                        binding.edTextPassword.setEnabled(true);
                        binding.edTextUserName.setEnabled(true);
                        binding.btnLogin.setEnabled(true);
                        binding.ivMore.setEnabled(true);

                        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(error);
                        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                    }
                });

        viewModel.getInsertSpinnerSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isInsert) {
                setupSpinner();
            }
        });

        viewModel.getYesDeleteSpinner().observe(getViewLifecycleOwner(), new Observer<ServerData>() {
            @Override
            public void onChanged(ServerData serverData) {
                if (viewModel.getServerDataList().size() == 0) {
                    SipSupportSharedPreferences.setCenterName(getContext(), null);
                    setupSpinner();
                    fragment.dismiss();
                    binding.edTextPassword.setText("");
                    binding.edTextUserName.setText("");
                    RequireIPAddressDialogFragment fragment = RequireIPAddressDialogFragment.newInstance();
                    fragment.show(getChildFragmentManager(), RequireIPAddressDialogFragment.TAG);
                } else {
                    SipSupportSharedPreferences.setCenterName(getContext(), null);
                    setupSpinner();
                }
            }
        });
    }
}