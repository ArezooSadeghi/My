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
import com.example.sipsupporterapp.adapter.PaymentAdapter;
import com.example.sipsupporterapp.databinding.FragmentPaymentBinding;
import com.example.sipsupporterapp.eventbus.PostBankAccountResultEvent;
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.activity.PhotoGalleryContainerActivity;
import com.example.sipsupporterapp.view.dialog.AddEditPaymentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.PaymentViewModel;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;

public class PaymentFragment extends Fragment {
    private FragmentPaymentBinding binding;
    private PaymentViewModel viewModel;
    private ServerData serverData;
    private String lastValueSpinner, centerName, userLoginKey;
    private int paymentID, bankAccountID;
    private BankAccountResult.BankAccountInfo[] bankAccountInfoArray;

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);

        fetchBankAccounts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_payment,
                container,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
    }

    private void initViews() {
        binding.recyclerViewPayment.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerViewPayment.addItemDecoration(dividerItemDecoration);

        binding.recyclerViewPayment.setHasFixedSize(true);
    }

    private void handleEvents() {
        binding.fabAddCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditPaymentDialogFragment fragment = AddEditPaymentDialogFragment.newInstance(0, "", 0, 0, bankAccountID, 0, "");
                fragment.show(getParentFragmentManager(), AddEditPaymentDialogFragment.TAG);
            }
        });

        binding.spinnerBankAccountNames.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                lastValueSpinner = (String) item;
                bankAccountID = bankAccountInfoArray[position].getBankAccountID();
                fetchCostsByBankAccountID();
            }
        });
    }

    private void setupSpinner(BankAccountResult.BankAccountInfo[] bankAccountInfoArray) {
        String[] bankAccountNameArray = new String[bankAccountInfoArray.length];
        for (int i = 0; i < bankAccountNameArray.length; i++) {
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

    private void deleteCost() {
        viewModel.getSipSupportServicePaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/Delete/";
        viewModel.deletePayment(path, userLoginKey, paymentID);
    }

    private void fetchCostsByBankAccountID() {
        viewModel.getSipSupporterServicePaymentResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/payments/ListByBankAccount/";
        viewModel.fetchPaymentsByBankAccount(path, userLoginKey, bankAccountID);
    }

    private void fetchBankAccounts() {
        viewModel.getSipSupporterServiceBankAccountResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/bankAccounts/List/";
        viewModel.fetchBankAccounts(path, userLoginKey);
    }

    private void setupAdapter(PaymentResult.PaymentInfo[] paymentInfoArray) {
        List<PaymentResult.PaymentInfo> paymentInfoList = Arrays.asList(paymentInfoArray);
        PaymentAdapter adapter = new PaymentAdapter(getContext(), viewModel, paymentInfoList);
        binding.recyclerViewPayment.setAdapter(adapter);
    }

    private void setupObserver() {
        viewModel.getBankAccountsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<BankAccountResult>() {
            @Override
            public void onChanged(BankAccountResult bankAccountResult) {
                if (bankAccountResult.getErrorCode().equals("0")) {
                    EventBus.getDefault().postSticky(new PostBankAccountResultEvent(bankAccountResult));
                    bankAccountInfoArray = bankAccountResult.getBankAccounts();
                    setupSpinner(bankAccountResult.getBankAccounts());
                    fetchCostsByBankAccountID();
                } else if (bankAccountResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(bankAccountResult.getError());
                }
            }
        });

        viewModel.getPaymentsByBankAccountResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                binding.progressBarLoading.setVisibility(View.GONE);

                if (paymentResult.getErrorCode().equals("0")) {
                    binding.recyclerViewPayment.setVisibility(View.VISIBLE);
                    setupAdapter(paymentResult.getPayments());
                } else if (paymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(paymentResult.getError());
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

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<PaymentResult.PaymentInfo>() {
            @Override
            public void onChanged(PaymentResult.PaymentInfo paymentInfo) {
                AddEditPaymentDialogFragment fragment = AddEditPaymentDialogFragment.newInstance(paymentInfo.getPaymentID(), paymentInfo.getDescription(), paymentInfo.getDatePayment(), paymentInfo.getPrice(), paymentInfo.getBankAccountID(), paymentInfo.getPaymentSubjectID(), paymentInfo.getPaymentSubject());
                fragment.show(getParentFragmentManager(), AddEditPaymentDialogFragment.TAG);
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<PaymentResult.PaymentInfo>() {
            @Override
            public void onChanged(PaymentResult.PaymentInfo paymentInfo) {
                paymentID = paymentInfo.getPaymentID();
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.question_delete_cost_message));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeletePaymentResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentResult>() {
            @Override
            public void onChanged(PaymentResult paymentResult) {
                if (paymentResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(getString(R.string.success_delete_cost_message));
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchCostsByBankAccountID();
                } else if (paymentResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(paymentResult.getError());
                }
            }
        });

        viewModel.getSeePaymentAttachmentsClicked().observe(getViewLifecycleOwner(), new Observer<PaymentResult.PaymentInfo>() {
            @Override
            public void onChanged(PaymentResult.PaymentInfo paymentInfo) {
                Intent starter = PhotoGalleryContainerActivity.start(getContext(), 0, 0, 0, paymentInfo.getPaymentID());
                startActivity(starter);
            }
        });

        viewModel.getUpdatingSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer bankAccountID) {
                for (int i = 0; i < bankAccountInfoArray.length; i++) {
                    if (bankAccountInfoArray[i].getBankAccountID() == bankAccountID) {
                        PaymentFragment.this.bankAccountID = bankAccountID;
                    }
                }

                setupSpinner(bankAccountInfoArray);
                fetchCostsByBankAccountID();
            }
        });
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

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
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

    @Subscribe
    public void getYesDeleteEvent(YesDeleteEvent event) {
        deleteCost();
    }
}