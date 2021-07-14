package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditIPAddressDialogBinding;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.LoginViewModel;

import java.util.List;

public class AddEditIPAddressDialogFragment extends DialogFragment {
    private FragmentAddEditIPAddressDialogBinding binding;
    private LoginViewModel viewModel;

    private String centerName, ipAddress, port;
    private boolean flag;

    private static final String ARGS_CENTER_NAME = "centerName";
    private static final String ARGS_IP_ADDRESS = "ipAddress";
    private static final String ARGS_PORT = "port";

    public static final String TAG = AddEditIPAddressDialogFragment.class.getSimpleName();


    public static AddEditIPAddressDialogFragment newInstance(
            String centerName, String ipAddress, String port) {
        AddEditIPAddressDialogFragment fragment = new AddEditIPAddressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_CENTER_NAME, centerName);
        args.putString(ARGS_IP_ADDRESS, ipAddress);
        args.putString(ARGS_PORT, port);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        centerName = getArguments().getString(ARGS_CENTER_NAME);
        ipAddress = getArguments().getString(ARGS_IP_ADDRESS);
        port = getArguments().getString(ARGS_PORT);

        viewModel = new ViewModelProvider(requireActivity())
                .get(LoginViewModel.class);

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_add_edit_i_p_address_dialog,
                null,
                false);

        binding.edTextCenterName.setText(centerName);
        binding.edTextIpAddress.setText(ipAddress);
        binding.edTextPort.setText(port);

        binding.edTextCenterName.setSelection(binding.edTextCenterName.getText().length());
        binding.edTextIpAddress.setSelection(binding.edTextIpAddress.getText().length());
        binding.edTextPort.setSelection(binding.edTextPort.getText().length());

        setListener();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(),  R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        return dialog;
    }


    private void setListener() {
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                String centerName = binding.edTextCenterName.getText().toString();
                String ipAddress = binding.edTextIpAddress.getText().toString();
                String port = binding.edTextPort.getText().toString();

                if (centerName.isEmpty() || ipAddress.isEmpty() || port.isEmpty()) {
                    ErrorDialogFragment fragment = ErrorDialogFragment
                            .newInstance("لطفا موارد خواسته شده را پر کنید");
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else if (ipAddress.length() < 7 || !Converter.hasThreeDots(ipAddress)
                        || Converter.hasEnglishLetter(ipAddress) || Converter.hasEnglishLetter(port)) {
                    ErrorDialogFragment fragment = ErrorDialogFragment
                            .newInstance("فرمت آدرس ip نادرست است");
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else {
                    List<ServerData> serverDataList = viewModel.getServerDataList();
                    if (serverDataList.size() > 0) {
                        for (ServerData serverData : serverDataList) {
                            if (serverData.getCenterName().equals(centerName)) {
                                ErrorDialogFragment fragment = ErrorDialogFragment
                                        .newInstance("نام مرکز تکراری می باشد");
                                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                                flag = true;
                            }
                        }
                    }
                    if (!flag) {
                        String newIpAddress = Converter.numberConverter(ipAddress);
                        String newPort = Converter.numberConverter(port);

                        ServerData serverData = new ServerData(centerName, newIpAddress, newPort);

                        if (serverData.getCenterName().equals(SipSupportSharedPreferences
                                .getCenterName(getContext()))) {
                            SipSupportSharedPreferences
                                    .setCenterName(getContext(), serverData.getCenterName());
                        }

                        viewModel.insertServerData(serverData);

                        viewModel.getInsertSpinnerSingleLiveEvent().setValue(true);
                        viewModel.getInsertIPAddressListSingleLiveEvent().setValue(true);
                        dismiss();
                    }
                }
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.edTextCenterName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.edTextIpAddress.requestFocus();
                }
                return false;
            }
        });

        binding.edTextIpAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.edTextPort.requestFocus();
                }
                return false;
            }
        });
    }
}