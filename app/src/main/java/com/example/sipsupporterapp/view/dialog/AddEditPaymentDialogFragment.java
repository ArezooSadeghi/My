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
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PaymentSubjectContainerActivity;
import com.example.sipsupporterapp.viewmodel.PaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Locale;

import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;

public class AddEditPaymentDialogFragment extends DialogFragment {
    private FragmentAddEditPaymentDialogBinding binding;
    private PaymentViewModel viewModel;

    private int paymentID, datePayment, paymentSubjectID, bankAccountID, currentYear, currentMonth, currentDay;
    private String lastValueSpinner, description, paymentSubject, currentDate;
    private BankAccountResult.BankAccountInfo[] bankAccountInfoArray;
    private long price;

    private static final String ARGS_PAYMENT_ID = "paymentID";
    private static final String ARGS_DESCRIPTION = "description";
    private static final String ARGS_DATE_PAYMENT = "datePayment";
    private static final String ARGS_PRICE = "price";
    private static final String ARGS_BANK_ACCOUNT_ID = "bankAccountID";
    private static final String ARGS_PAYMENT_SUBJECT_ID = "paymentSubjectID";
    private static final String ARGS_PAYMENT_SUBJECT = "paymentSubject";

    public static final String TAG = AddEditPaymentDialogFragment.class.getSimpleName();

    public static AddEditPaymentDialogFragment newInstance(int paymentID, String description, int datePayment, long price, int bankAccountID, int paymentSubjectID, String paymentSubject) {
        AddEditPaymentDialogFragment fragment = new AddEditPaymentDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ARGS_PAYMENT_ID, paymentID);
        args.putString(ARGS_DESCRIPTION, description);
        args.putInt(ARGS_DATE_PAYMENT, datePayment);
        args.putLong(ARGS_PRICE, price);
        args.putInt(ARGS_BANK_ACCOUNT_ID, bankAccountID);
        args.putInt(ARGS_PAYMENT_SUBJECT_ID, paymentSubjectID);
        args.putString(ARGS_PAYMENT_SUBJECT, paymentSubject);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paymentID = getArguments().getInt(ARGS_PAYMENT_ID);
        description = getArguments().getString(ARGS_DESCRIPTION);
        datePayment = getArguments().getInt(ARGS_DATE_PAYMENT);
        price = getArguments().getLong(ARGS_PRICE);
        bankAccountID = getArguments().getInt(ARGS_BANK_ACCOUNT_ID);
        paymentSubjectID = getArguments().getInt(ARGS_PAYMENT_SUBJECT_ID);
        paymentSubject = getArguments().getString(ARGS_PAYMENT_SUBJECT);

        createViewModel();
        setupObserver();
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
                .Builder(getContext())
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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
    }

    private void setupObserver() {
        viewModel.getAddPaymentResultSingleLiveEvent().observe(this, new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                if (paymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_cost_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getUpdatingSingleLiveEvent().setValue(paymentResult.getPayments()[0].getBankAccountID());
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(paymentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getEditPaymentResultSingleLiveEvent().observe(this, new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                if (paymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_register_cost_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getUpdatingSingleLiveEvent().setValue(paymentResult.getPayments()[0].getBankAccountID());
                    dismiss();
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(paymentResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getDangerousUserSingleLiveEvent().observe(this, new Observer<Boolean>() {
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

        viewModel.getPaymentSubjectInfoResultSingleLiveEvent().observe(this, new Observer<PaymentSubjectResult>() {
            @Override
            public void onChanged(PaymentSubjectResult paymentSubjectResult) {
                paymentSubject = paymentSubjectResult.getPaymentSubjects()[0].getParentPaymentSubject() + " " + paymentSubjectResult.getPaymentSubjects()[0].getPaymentSubject();
                binding.btnWhat.setText(paymentSubject);
            }
        });
    }

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        String[] bankAccountNameArray = new String[bankAccountInfoArray.length];
        for (int i = 0; i < bankAccountInfoArray.length; i++) {
            bankAccountNameArray[i] = bankAccountInfoArray[i].getBankAccountName();
        }
        if (bankAccountNameArray.length != 0) {
            if (bankAccountID == 0) {
                lastValueSpinner = bankAccountNameArray[0];
                bankAccountID = bankAccountInfoArray[0].getBankAccountID();
                binding.spinnerBankAccountNames.setItems(bankAccountNameArray);
            } else {
                for (int i = 0; i < bankAccountInfoArray.length; i++) {
                    if (bankAccountInfoArray[i].getBankAccountID() == bankAccountID) {
                        lastValueSpinner = bankAccountInfoArray[i].getBankAccountName();
                        BankAccountResult.BankAccountInfo bankAccountInfo = bankAccountInfoArray[i];
                        bankAccountID = bankAccountInfo.getBankAccountID();
                        bankAccountNameArray[i] = bankAccountNameArray[0];
                        bankAccountNameArray[0] = lastValueSpinner;
                        bankAccountInfoArray[i] = bankAccountInfoArray[0];
                        bankAccountInfoArray[0] = bankAccountInfo;
                    }
                }
                binding.spinnerBankAccountNames.setItems(bankAccountNameArray);
            }
        }
    }

    private void initViews() {
        binding.edTextDescription.setText(description);
        binding.edTextDescription.setSelection(binding.edTextDescription.getText().length());

        binding.btnWhat.setText(paymentSubject);

        if (datePayment == 0) {
            binding.btnDatePayment.setText(SipSupportSharedPreferences.getDate(getContext()));
        } else {
            String dateFormat = formatDate();
            binding.btnDatePayment.setText(dateFormat);
        }
    }

    @NotNull
    private String formatDate() {
        String date = String.valueOf(datePayment);
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        return year + "/" + month + "/" + day;
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price = binding.edTextInvoicePrice.getText().toString().replaceAll(",", "");

                if (paymentSubjectID == 0) {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.not_exist_payment_subject));
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else if (Long.valueOf(price) == 0) {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.zero_price));
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else {
                    PaymentResult.PaymentInfo paymentInfo = new PaymentResult().new PaymentInfo();

                    String bankAccountName = lastValueSpinner;
                    paymentInfo.setBankAccountName(bankAccountName);

                    paymentInfo.setBankAccountID(bankAccountID);

                    String description = binding.edTextDescription.getText().toString();
                    paymentInfo.setDescription(description);

                    paymentInfo.setPaymentSubject(paymentSubject);

                    paymentInfo.setPrice(Long.valueOf(price));

                    paymentInfo.setPaymentID(paymentID);
                    paymentInfo.setPaymentSubjectID(paymentSubjectID);

                    String date = binding.btnDatePayment.getText().toString().replaceAll("/", "");
                    paymentInfo.setDatePayment(Integer.valueOf(date));

                    if (paymentID == 0) {
                        addCost(paymentInfo);
                    } else {
                        editCost(paymentInfo);
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
                        .setPositiveButtonString("تایید")
                        .setNegativeButton("انصراف")
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
                                    datePayment = Integer.parseInt(year + "0" + month + "0" + day);
                                    binding.btnDatePayment.setText(formatDate());
                                } else if (String.valueOf(month).length() == 1) {
                                    datePayment = Integer.parseInt(year + "0" + month + "" + day);
                                    binding.btnDatePayment.setText(formatDate());
                                } else if (String.valueOf(day).length() == 1) {
                                    datePayment = Integer.parseInt(year + "" + month + "0" + day);
                                    binding.btnDatePayment.setText(formatDate());
                                } else {
                                    datePayment = Integer.parseInt(year + "" + month + "" + day);
                                    binding.btnDatePayment.setText(formatDate());
                                }
                            }

                            @Override
                            public void onDismissed() {

                            }
                        });
                persianDatePickerDialog.show();
            }
        });

        binding.btnShowSubjectPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent starter = PaymentSubjectContainerActivity.start(getContext());
                startActivity(starter);
            }
        });

        binding.edTextInvoicePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edTextInvoicePrice.removeTextChangedListener(this);
                try {
                    String text = editable.toString();
                    Long textToLong;
                    if (text.contains(",")) {
                        text = text.replaceAll(",", "");
                    }
                    textToLong = Long.parseLong(text);
                    String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(textToLong);
                    binding.edTextInvoicePrice.setText(currencyFormat);
                    binding.edTextInvoicePrice.setSelection(binding.edTextInvoicePrice.getText().length());
                } catch (NumberFormatException exception) {
                    Log.e(TAG, exception.getMessage());
                }
                binding.edTextInvoicePrice.addTextChangedListener(this);
            }
        });

        binding.edTextInvoicePrice.setText(String.valueOf(price));
        binding.edTextInvoicePrice.setSelection(binding.edTextInvoicePrice.getText().length());

        binding.spinnerBankAccountNames.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                lastValueSpinner = (String) item;
                bankAccountID = bankAccountInfoArray[position].getBankAccountID();
            }
        });
    }

    private void editCost(PaymentResult.PaymentInfo paymentInfo) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceEditPayment(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/Edit/";
        viewModel.editPayment(path, userLoginKey, paymentInfo);
    }

    private void addCost(PaymentResult.PaymentInfo paymentInfo) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceAddPayment(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/Add/";
        viewModel.addPayment(path, userLoginKey, paymentInfo);
    }

    private void fetchPaymentSubjectInfo(int paymentSubjectID) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServicePaymentInfo(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/paymentSubjects/Info/";
        viewModel.fetchPaymentSubjectInfo(path, userLoginKey, paymentSubjectID);
    }

    @Subscribe(sticky = true)
    public void getPaymentSubjectIDEvent(PostPaymentSubjectIDEvent event) {
        paymentSubjectID = event.getPaymentSubjectID();
        fetchPaymentSubjectInfo(paymentSubjectID);
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true)
    public void getBankAccountResultEvent(PostBankAccountResultEvent event) {
        bankAccountInfoArray = event.getBankAccountResult().getBankAccounts();
        setupSpinner(bankAccountInfoArray);
        EventBus.getDefault().removeStickyEvent(event);
    }
}