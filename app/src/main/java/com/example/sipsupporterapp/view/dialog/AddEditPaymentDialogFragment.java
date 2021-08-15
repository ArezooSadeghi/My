package com.example.sipsupporterapp.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAddEditPaymentDialogBinding;
import com.example.sipsupporterapp.eventbus.PostBankAccountResultEvent;
import com.example.sipsupporterapp.eventbus.PostPaymentSubjectIDEvent;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PaymentSubjectContainerActivity;
import com.example.sipsupporterapp.viewmodel.PaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;

public class AddEditPaymentDialogFragment extends DialogFragment {
    private FragmentAddEditPaymentDialogBinding binding;
    private PaymentViewModel viewModel;
    private ServerData serverData;
    private String currentDate, centerName, userLoginKey;
    private BankAccountResult.BankAccountInfo[] bankAccountInfoArray;
    private PaymentResult.PaymentInfo paymentInfo;
    private PaymentSubjectResult.PaymentSubjectInfo paymentSubjectInfo;
    private int paymentID, bankAccountID, currentYear, currentMonth, currentDay, index;

    private static final String ARGS_PAYMENT_ID = "paymentID";
    private static final String ARGS_BANK_ACCOUNT_ID = "bankAccountID";
    public static final String TAG = AddEditPaymentDialogFragment.class.getSimpleName();

    public static AddEditPaymentDialogFragment newInstance(int paymentID, int bankAccountID) {
        AddEditPaymentDialogFragment fragment = new AddEditPaymentDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PAYMENT_ID, paymentID);
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

        if (paymentID > 0) {
            fetchPaymentInfo(paymentID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_add_edit_payment_dialog,
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
    public void getPaymentSubjectIDEvent(PostPaymentSubjectIDEvent event) {
        fetchPaymentSubjectInfo(event.getPaymentSubjectID());
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true)
    public void getBankAccountResultEvent(PostBankAccountResultEvent event) {
        bankAccountInfoArray = event.getBankAccountResult().getBankAccounts();
        setupSpinner(bankAccountInfoArray);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
    }

    private void initVariables() {
        index = -1;
        paymentID = getArguments().getInt(ARGS_PAYMENT_ID);
        bankAccountID = getArguments().getInt(ARGS_BANK_ACCOUNT_ID);
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        if (paymentInfo != null) {

            if (paymentInfo.getDatePayment() != 0) {
                String dateFormat = Converter.dateFormat(String.valueOf(paymentInfo.getDatePayment()));
                binding.btnDatePayment.setText(dateFormat);
            } else {
                binding.btnDatePayment.setText(SipSupportSharedPreferences.getDate(getContext()));
            }

            binding.edTextPrice.setText(String.valueOf(paymentInfo.getPrice()));
            binding.edTextPrice.setSelection(binding.edTextPrice.getText().length());
            binding.edTextDescription.setText(Converter.letterConverter(paymentInfo.getDescription()));
            binding.edTextDescription.setSelection(binding.edTextDescription.getText().length());
        } else {
            binding.btnDatePayment.setText(SipSupportSharedPreferences.getDate(getContext()));
        }

        if (paymentSubjectInfo != null) {
            binding.btnPaymentSubject.setText(paymentSubjectInfo.getParentPaymentSubject() != null ? paymentSubjectInfo.getParentPaymentSubject() + "(" + paymentSubjectInfo.getPaymentSubject() + ")" : paymentSubjectInfo.getPaymentSubject());
        }
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
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

    private void editPayment(PaymentResult.PaymentInfo paymentInfo) {
        viewModel.getSipSupporterServicePaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/Edit/";
        viewModel.editPayment(path, userLoginKey, paymentInfo);
    }

    private void addPayment(PaymentResult.PaymentInfo paymentInfo) {
        viewModel.getSipSupporterServicePaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/Add/";
        viewModel.addPayment(path, userLoginKey, paymentInfo);
    }

    private void fetchPaymentSubjectInfo(int paymentSubjectID) {
        viewModel.getSipSupporterServicePaymentSubjectResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/paymentSubjects/Info/";
        viewModel.fetchPaymentSubjectInfo(path, userLoginKey, paymentSubjectID);
    }

    private void fetchPaymentInfo(int paymentID) {
        viewModel.getSipSupporterServicePaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/Info/";
        viewModel.fetchPaymentInfo(path, userLoginKey, paymentID);
    }

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        List<String> bankAccountNameList = new ArrayList<>();
        if (bankAccountID != 0) {
            for (int i = 0; i < bankAccountInfoArray.length; i++) {
                if (bankAccountInfoArray[i].getBankAccountID() == bankAccountID) {
                    index = i;
                    continue;
                } else {
                    bankAccountNameList.add(bankAccountInfoArray[i].getBankAccountName());
                }
            }
        } else {
            for (int i = 0; i < bankAccountInfoArray.length; i++) {
                bankAccountNameList.add(bankAccountInfoArray[i].getBankAccountName());
            }
        }
        if (index != -1) {
            bankAccountNameList.add(0, bankAccountInfoArray[index].getBankAccountName());
        }
        binding.spinner.setItems(bankAccountNameList);
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = binding.edTextPrice.getText().toString().replaceAll(",", "");

                if (paymentSubjectInfo == null && paymentID == 0) {
                    handleError(getString(R.string.determine_payment_subject_message));
                } else if (price.isEmpty() || Long.valueOf(price) == 0) {
                    handleError(getString(R.string.empty_zero_price_message));
                } else {
                    PaymentResult.PaymentInfo paymentInfo = new PaymentResult.PaymentInfo();

                    String description = binding.edTextDescription.getText().toString();
                    paymentInfo.setDescription(description);

                    paymentInfo.setPrice(Long.valueOf(price));

                    paymentInfo.setPaymentID(paymentID);
                    paymentInfo.setPaymentSubjectID(paymentSubjectInfo.getPaymentSubjectID());
                    paymentInfo.setBankAccountID(bankAccountID);

                    String date = binding.btnDatePayment.getText().toString().replaceAll("/", "");
                    paymentInfo.setDatePayment(Integer.valueOf(date));

                    if (paymentID == 0) {
                        addPayment(paymentInfo);
                    } else {
                        editPayment(paymentInfo);
                    }
                }
            }
        });

        binding.btnDatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate = binding.btnDatePayment.getText().toString();
                currentYear = Integer.parseInt(currentDate.substring(0, 4));
                currentMonth = Integer.parseInt(currentDate.substring(5, 7));
                currentDay = Integer.parseInt(currentDate.substring(8));

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
            }
        });

        binding.btnShowPaymentSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent starter = PaymentSubjectContainerActivity.start(getContext());
                startActivity(starter);
            }
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

        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                bankAccountID = bankAccountInfoArray[position].getBankAccountID();
            }
        });
    }

    private void setupObserver() {
        viewModel.getPaymentInfoResultSingleLiveEvent().observe(this, new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                if (paymentResult.getErrorCode().equals("0")) {
                    paymentInfo = paymentResult.getPayments()[0];
                    fetchPaymentSubjectInfo(paymentInfo.getPaymentSubjectID());
                    initViews();
                }
            }
        });

        viewModel.getAddPaymentResultSingleLiveEvent().observe(this, new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                if (paymentResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_payment_message));
                    viewModel.getRefresh().setValue(paymentResult.getPayments()[0].getBankAccountID());
                    dismiss();
                } else if (paymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(paymentResult.getError());
                }
            }
        });

        viewModel.getEditPaymentResultSingleLiveEvent().observe(this, new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                if (paymentResult.getErrorCode().equals("0")) {
                    showSuccessDialog(getString(R.string.success_add_edit_payment_message));
                    viewModel.getRefresh().setValue(paymentResult.getPayments()[0].getBankAccountID());
                    dismiss();
                } else if (paymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(paymentResult.getError());
                }
            }
        });

        viewModel.getPaymentSubjectInfoResultSingleLiveEvent().observe(this, new Observer<PaymentSubjectResult>() {
            @Override
            public void onChanged(PaymentSubjectResult paymentSubjectResult) {
                if (paymentSubjectResult.getErrorCode().equals("0")) {
                    paymentSubjectInfo = paymentSubjectResult.getPaymentSubjects()[0];
                    initViews();
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });
    }
}