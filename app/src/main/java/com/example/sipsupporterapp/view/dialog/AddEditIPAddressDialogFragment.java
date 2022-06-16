package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
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
import com.example.sipsupporterapp.databinding.FragmentAddEditIpAddressDialogBinding;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.LoginViewModel;

import java.util.List;

public class AddEditIPAddressDialogFragment extends DialogFragment {
    private FragmentAddEditIpAddressDialogBinding binding;
    private LoginViewModel viewModel;

    private String centerName, ipAddress, port;
    private boolean flag;

    private static final String ARGS_CENTER_NAME = "centerName";
    private static final String ARGS_IP_ADDRESS = "ipAddress";
    private static final String ARGS_PORT = "port";
    public static final String TAG = AddEditIPAddressDialogFragment.class.getSimpleName();

    public static AddEditIPAddressDialogFragment newInstance(String centerName, String ipAddress, String port) {
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
        createViewModel();
        initVariables();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_add_edit_ip_address_dialog,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext())
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        return dialog;
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private void initVariables() {
        centerName = getArguments().getString(ARGS_CENTER_NAME);
        ipAddress = getArguments().getString(ARGS_IP_ADDRESS);
        port = getArguments().getString(ARGS_PORT);
    }

    private void initViews() {
        binding.edTxtCenterName.setText(centerName);
        binding.edTextIp.setText(ipAddress);
        binding.edTxtPort.setText(port);
        binding.edTxtCenterName.setSelection(binding.edTxtCenterName.getText().length());
        binding.edTextIp.setSelection(binding.edTextIp.getText().length());
        binding.edTxtPort.setSelection(binding.edTxtPort.getText().length());
    }

    private void handleEvents() {
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                String centerName = binding.edTxtCenterName.getText().toString();
                String ipAddress = binding.edTextIp.getText().toString();
                String port = binding.edTxtPort.getText().toString();

                if (centerName.isEmpty() || ipAddress.isEmpty() || port.isEmpty()) {
                    handleError("لطفا موارد خواسته شده را پر کنید");
                } else if (ipAddress.length() < 7 || !Converter.hasThreeDots(ipAddress)
                        || Converter.hasEnglishLetter(ipAddress) || Converter.hasEnglishLetter(port)) {
                    handleError("فرمت آدرس ip نادرست می باشد");
                } else {
                    List<ServerData> serverDataList = viewModel.getServerDataList();
                    if (serverDataList.size() > 0) {
                        for (ServerData serverData : serverDataList) {
                            if (serverData.getCenterName().equals(centerName)) {
                                handleError("نام سرور تکراری می باشد");
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

        binding.edTxtCenterName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.edTextIp.requestFocus();
                }
                return false;
            }
        });

        binding.edTextIp.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.edTxtPort.requestFocus();
                }
                return false;
            }
        });
    }

    private void handleError(String msg) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(msg);
        dialog.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }
}