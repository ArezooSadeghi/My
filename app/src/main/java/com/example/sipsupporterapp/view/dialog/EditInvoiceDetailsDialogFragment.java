package com.example.sipsupporterapp.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentEditInvoiceDetailsDialogBinding;
import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.InvoiceViewModel;

public class EditInvoiceDetailsDialogFragment extends DialogFragment {
    private FragmentEditInvoiceDetailsDialogBinding binding;
    private InvoiceViewModel viewModel;

    private static final String ARGS_INVOICE_DETAILS_ID = "invoiceDetailsID";
    private static final String ARGS_INVOICE_ID = "invoiceID";
    private static final String ARGS_PRODUCT_ID = "productID";
    private static final String ARGS_PRODUCT_NAME = "productName";
    private static final String ARGS_QTY = "QTY";
    private static final String ARGS_UNIT_PRICE = "unitPrice";
    private static final String ARGS_DESCRIPTION = "description";

    public static final String TAG = EditInvoiceDetailsDialogFragment.class.getSimpleName();

    public static EditInvoiceDetailsDialogFragment newInstance(int invoiceDetailsID, int invoiceID, int productID, String productName, int QTY, int unitPrice, String description) {
        EditInvoiceDetailsDialogFragment fragment = new EditInvoiceDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_INVOICE_DETAILS_ID, invoiceDetailsID);
        args.putInt(ARGS_INVOICE_ID, invoiceID);
        args.putInt(ARGS_PRODUCT_ID, productID);
        args.putString(ARGS_PRODUCT_NAME, productName);
        args.putInt(ARGS_QTY, QTY);
        args.putInt(ARGS_UNIT_PRICE, unitPrice);
        args.putString(ARGS_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_edit_invoice_details_dialog,
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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(InvoiceViewModel.class);
    }

    private void initViews() {
        int productID = getArguments().getInt(ARGS_PRODUCT_ID);
        binding.btnProductID.setText(String.valueOf(productID));

        String productName = getArguments().getString(ARGS_PRODUCT_NAME);
        binding.btnProductName.setText(productName);

        int QTY = getArguments().getInt(ARGS_QTY);
        binding.edTextQTY.setText(String.valueOf(QTY));

        int unitPrice = getArguments().getInt(ARGS_UNIT_PRICE);
        binding.edTextUnitPrice.setText(String.valueOf(unitPrice));

        String description = getArguments().getString(ARGS_DESCRIPTION);
        binding.edTextDescription.setText(description);
    }

    private void handleEvents() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo = new InvoiceDetailsResult.InvoiceDetailsInfo();
                invoiceDetailsInfo.setProductID(Integer.valueOf(binding.btnProductID.getText().toString()));
                invoiceDetailsInfo.setProductName(binding.btnProductName.getText().toString());
                invoiceDetailsInfo.setQTY(Integer.valueOf(binding.edTextQTY.getText().toString()));
                invoiceDetailsInfo.setUnitPrice((int) Long.parseLong(binding.edTextUnitPrice.getText().toString()));
                invoiceDetailsInfo.setDescription(binding.edTextDescription.getText().toString());

                int invoiceID = getArguments().getInt(ARGS_INVOICE_ID);
                invoiceDetailsInfo.setInvoiceID(invoiceID);

                int invoiceDetailsID = getArguments().getInt(ARGS_INVOICE_DETAILS_ID);
                invoiceDetailsInfo.setInvoiceDetailsID(invoiceDetailsID);

                editInvoiceDetails(invoiceDetailsInfo);
            }
        });
    }

    private void editInvoiceDetails(InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceInvoiceDetailsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/InvoiceDetails/Edit/";
        viewModel.editInvoiceDetails(path, userLoginKey, invoiceDetailsInfo);
    }

    private void setupObserver() {
        viewModel.getEditInvoiceDetailsResultSingleLiveEvent().observe(this, new Observer<InvoiceDetailsResult>() {
            @Override
            public void onChanged(InvoiceDetailsResult invoiceDetailsResult) {
                if (invoiceDetailsResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("محصول موردنظر با موفقیت ثبت شد");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    viewModel.getRefresh().setValue(true);
                    dismiss();
                } else {
                    handleError(invoiceDetailsResult.getError());
                }
            }
        });
    }

    private void handleError(String msg) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(msg);
        dialog.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }
}