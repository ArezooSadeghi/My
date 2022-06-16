package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddNewCustomerPaymentBinding;
import com.example.sipsupporterapp.eventbus.PostBankAccountResultEvent;
import com.example.sipsupporterapp.eventbus.SuccessEvent;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;

public class AddNewCustomerPaymentFragment extends DialogFragment {
    private FragmentAddNewCustomerPaymentBinding binding;
    private CustomerPaymentViewModel viewModel;
    private BankAccountResult.BankAccountInfo[] bankAccountInfoArray;
    private ServerData serverData;
    private String centerName, userLoginKey, currentDate;
    private int bankAccountID, currentYear, currentMonth, currentDay;

    private static final String ARGS_BANK_ACCOUNT_ID = "bankAccountID";
    public static final String TAG = AddNewCustomerPaymentFragment.class.getSimpleName();

    public static AddNewCustomerPaymentFragment newInstance(int bankAccountID) {
        AddNewCustomerPaymentFragment fragment = new AddNewCustomerPaymentFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_BANK_ACCOUNT_ID, bankAccountID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createViewModel();
        initVariables();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_add_new_customer_payment,
                null,
                false);

        initViews();
        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void getBankAccountResultEvent(PostBankAccountResultEvent event) {
        bankAccountInfoArray = event.getBankAccountResult().getBankAccounts();
        setupSpinner(event.getBankAccountResult().getBankAccounts());
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true)
    public void getSuccessEvent(SuccessEvent event) {
        viewModel.getRefresh().setValue(true);
        dismiss();
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerPaymentViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
    }

    private void setupObserver() {
        viewModel.getAddCustomerPaymentResultSingleLiveEvent().observe(this, customerPaymentResult -> {
            if (customerPaymentResult != null) {
                if (customerPaymentResult.getErrorCode().equals("0")) {
                    showSuccessDialog("واریزی با موفقیت ثبت شد");
                } else if (customerPaymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(customerPaymentResult.getError());
                }
            }
        });
    }

    private void initViews() {
        String date = SipSupportSharedPreferences.getDate(getContext());
        if (date != null) {
            binding.btnDatePayment.setText(date);
        }
    }

    private void handleEvents() {
        binding.spinner.setOnItemSelectedListener((view, position, id, item) -> bankAccountID = bankAccountInfoArray[position].getBankAccountID());

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSave.setOnClickListener(v -> {
            String description = binding.edTextDescription.getText().toString();
            long price = Long.parseLong(binding.edTextPrice.getText().toString().replaceAll(",", ""));
            int datePayment = Integer.parseInt(binding.btnDatePayment.getText().toString().replaceAll("/", "").replaceAll(" ", ""));
            CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo = new CustomerPaymentResult().new CustomerPaymentInfo();
            customerPaymentInfo.setDescription(description);
            customerPaymentInfo.setPrice(price);
            customerPaymentInfo.setDatePayment(datePayment);
            customerPaymentInfo.setBankAccountID(bankAccountID);
            addCustomerPayment(customerPaymentInfo);
        });

        binding.edTextPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edTextPrice.removeTextChangedListener(this);
                try {
                    String text = editable.toString();
                    Long textToLong;
                    if (text.contains(",")) {
                        text = text.replaceAll(",", "");
                    }
                    textToLong = Long.parseLong(text);
                    String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(textToLong);
                    binding.edTextPrice.setText(currencyFormat);
                    binding.edTextPrice.setSelection(binding.edTextPrice.getText().length());
                } catch (NumberFormatException exception) {
                    Log.e(TAG, exception.getMessage());
                }
                binding.edTextPrice.addTextChangedListener(this);
            }
        });

        binding.btnDatePayment.setOnClickListener(view -> {
            currentDate = binding.btnDatePayment.getText().toString().replaceAll(" ", "");
            currentYear = Integer.parseInt(currentDate.substring(0, 4).replaceAll(" ", ""));
            currentMonth = Integer.parseInt(currentDate.substring(5, 7).replaceAll(" ", ""));
            currentDay = Integer.parseInt(currentDate.substring(8).replaceAll(" ", ""));
            PersianDatePickerDialog persianDatePickerDialog = new PersianDatePickerDialog(getContext())
                    .setPositiveButtonString(getString(R.string.ok))
                    .setNegativeButton(getString(R.string.cancel))
                    .setMinYear(1300)
                    .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                    .setInitDate(currentYear, currentMonth, currentDay)
                    .setActionTextColor(Color.BLACK)
                    .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                    .setCancelable(false)
                    .setListener(new PersianPickerListener() {
                        @Override
                        public void onDateSelected(PersianPickerDate persianPickerDate) {
                            int year = persianPickerDate.getPersianYear();
                            int month = persianPickerDate.getPersianMonth();
                            int day = persianPickerDate.getPersianDay();
                            if (String.valueOf(month).length() == 1 && String.valueOf(day).length() == 1) {
                                binding.btnDatePayment.setText(Converter.dateFormat(year + "0" + month + "0" + day));
                            } else if (String.valueOf(month).length() == 1) {
                                binding.btnDatePayment.setText(Converter.dateFormat(year + "0" + month + "" + day));
                            } else if (String.valueOf(day).length() == 1) {
                                binding.btnDatePayment.setText(Converter.dateFormat(year + "" + month + "0" + day));
                            } else {
                                binding.btnDatePayment.setText(Converter.dateFormat(year + "" + month + "" + day));
                            }
                        }

                        @Override
                        public void onDismissed() {

                        }
                    });
            persianDatePickerDialog.show();
        });
    }

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        List<String> bankAccountNameList = new ArrayList<>();
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNameList.add(i, Converter.letterConverter(bankAccountInfoArray[i].getBankAccountName()));
        }
        binding.spinner.setItems(bankAccountNameList);

        if (bankAccountID != 0) {
            for (int i = 0; i < bankAccountInfoArray.length; i++) {
                if (bankAccountInfoArray[i].getBankAccountID() == bankAccountID) {
                    binding.spinner.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            bankAccountID = bankAccountInfoArray[0].getBankAccountID();
        }
    }

    private void addCustomerPayment(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        viewModel.getSipSupporterServiceCustomerPaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/customerPayments/Add/";
        viewModel.addCustomerPaymentResult(path, userLoginKey, customerPaymentInfo);
    }

    private void handleError(String msg) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(msg);
        dialog.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
    }

    private void ejectUser() {
        SipSupportSharedPreferences.setUserFullName(getContext(), null);
        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
        SipSupportSharedPreferences.setCenterName(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);
        SipSupportSharedPreferences.setCaseID(getContext(), 0);
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }
}