package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.InvoiceDetailsAdapter;
import com.example.sipsupporterapp.databinding.FragmentPrintBinding;
import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.model.InvoiceResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.PrintViewModel;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class PrintFragment extends Fragment {
    private FragmentPrintBinding binding;
    private PrintViewModel viewModel;
    private ServerData serverData;
    private int invoiceID, finalSum;
    private String centerName, userLoginKey;

    public static PrintFragment newInstance() {
        PrintFragment fragment = new PrintFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrintFragmentArgs args = PrintFragmentArgs.fromBundle(getArguments());
        invoiceID = args.getInvoiceID();

        createViewModel();
        initVariables();
        fetchInvoiceInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_print,
                container,
                false);

        initViews();

        binding.ivPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.container.setDrawingCacheEnabled(true);
                binding.container.buildDrawingCache();
                Bitmap bitmap = binding.container.getDrawingCache();
                PrintHelper printHelper = new PrintHelper(getActivity());
                printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                printHelper.printBitmap("Print", bitmap);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(PrintViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceInvoiceDetailsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.getSipSupporterServiceInvoiceResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void fetchInvoiceInfo() {
        String path = "/api/v1/invoice/Info/";
        viewModel.fetchInvoiceInfo(path, userLoginKey, invoiceID);
    }

    private void fetchInvoiceDetails() {
        String path = "/api/v1/InvoiceDetails/List/";
        viewModel.fetchInvoiceDetails(path, userLoginKey, invoiceID);
    }

    private void setupObserver() {
        viewModel.getInvoiceDetailsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceDetailsResult>() {
            @Override
            public void onChanged(InvoiceDetailsResult invoiceDetailsResult) {
                binding.progressBarLoading.setVisibility(View.INVISIBLE);
                if (invoiceDetailsResult.getErrorCode().equals("0")) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    setupAdapter(invoiceDetailsResult.getInvoiceDetails());

                    for (int i = 0; i < invoiceDetailsResult.getInvoiceDetails().length; i++) {
                        int sum = ((invoiceDetailsResult.getInvoiceDetails()[i].getQTY() * invoiceDetailsResult.getInvoiceDetails()[i].getUnitPrice()) - invoiceDetailsResult.getInvoiceDetails()[i].getSumDiscountPrice());
                        finalSum += sum;
                    }

                    String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(finalSum);
                    binding.txtFinalSum.setText("جمع نهایی: " + currencyFormat);
                } else if (invoiceDetailsResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(invoiceDetailsResult.getError());
                }
            }
        });

        viewModel.getInvoiceInfoResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceResult>() {
            @Override
            public void onChanged(InvoiceResult invoiceResult) {
                if (invoiceResult.getErrorCode().equals("0")) {
                    InvoiceResult.InvoiceInfo invoiceInfo = invoiceResult.getInvoices()[0];
                    binding.txtCustomerName.setText("نام مشتری: " + Converter.letterConverter(invoiceInfo.getCustomerName()));
                    binding.txtInvoiceID.setText("تاریخ: " + invoiceInfo.getInvoiceID());
                    binding.txtInvoiceDate.setText("شماره: " + invoiceInfo.getInvoiceDate());
                    fetchInvoiceDetails();
                } else if (invoiceResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(invoiceResult.getError());
                }
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
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void setupAdapter(InvoiceDetailsResult.InvoiceDetailsInfo[] invoiceDetailsInfoArray) {
        InvoiceDetailsAdapter adapter = new InvoiceDetailsAdapter(viewModel, Arrays.asList(invoiceDetailsInfoArray));
        binding.recyclerView.setAdapter(adapter);
    }
}