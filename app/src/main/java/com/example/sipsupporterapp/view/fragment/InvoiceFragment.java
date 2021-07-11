package com.example.sipsupporterapp.view.fragment;

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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.InvoiceDetailsAdapter;
import com.example.sipsupporterapp.databinding.FragmentInvoiceBinding;
import com.example.sipsupporterapp.eventbus.NavigateEvent;
import com.example.sipsupporterapp.eventbus.PostProductGroupIDEvent;
import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.model.InvoiceResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.InvoiceViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

public class InvoiceFragment extends Fragment {
    private FragmentInvoiceBinding binding;
    private InvoiceViewModel viewModel;
    private ServerData serverData;
    private int caseID, customerID, invoiceID;
    private String centerName, userLoginKey, customerName;

    public static InvoiceFragment newInstance() {
        InvoiceFragment fragment = new InvoiceFragment();
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
        viewModel.getSipSupporterServiceInvoiceResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_invoice,
                container,
                false);

        handleEvents();
        binding.recyclerViewInvoiceDetails.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InvoiceFragmentArgs args = InvoiceFragmentArgs.fromBundle(getArguments());
        caseID = args.getCaseID();
        if (caseID != 0) {
            SipSupportSharedPreferences.setCaseID(getContext(), caseID);
        }
        customerID = args.getCustomerID();
        customerName = args.getCustomerName();
        fetchInvoiceInfoByCaseID();
        setupObserver();

    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
    }

    private void handleEvents() {
        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoiceFragmentDirections.ActionInvoiceFragmentToProductsFragment action =
                        InvoiceFragmentDirections.actionInvoiceFragmentToProductsFragment();
                action.setFlag(true);
                NavHostFragment.findNavController(InvoiceFragment.this).navigate(action);
            }
        });

        binding.ivAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void fetchInvoiceInfoByCaseID() {
        String path = "/api/v1/invoice/Info_byCaseID/";
        viewModel.fetchInvoiceInfoByCaseID(path, userLoginKey, SipSupportSharedPreferences.getCaseID(getContext()));
    }

    private void addInvoice() {
        InvoiceResult.InvoiceInfo invoiceInfo = new InvoiceResult().new InvoiceInfo();
        invoiceInfo.setCaseID(SipSupportSharedPreferences.getCaseID(getContext()));
        invoiceInfo.setCustomerID(customerID);
        invoiceInfo.setInvoiceDate(SipSupportSharedPreferences.getDate(getContext()));
        invoiceInfo.setDescription("");
        invoiceInfo.setCustomerName(customerName);
        String path = "/api/v1/invoice/Add/";
        viewModel.addInvoice(path, userLoginKey, invoiceInfo);
    }

    private void initViews(InvoiceResult.InvoiceInfo invoiceInfo) {
        binding.btnInvoiceID.setText(String.valueOf(invoiceInfo.getInvoiceID()));
        binding.btnInvoiceDate.setText(invoiceInfo.getInvoiceDate());
        binding.btnCustomerName.setText(invoiceInfo.getCustomerName());
    }

    private void setupObserver() {
        viewModel.getInvoiceInfoByCaseIDResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceResult>() {
            @Override
            public void onChanged(InvoiceResult invoiceResult) {
                if (invoiceResult.getErrorCode().equals("0")) {
                    if (invoiceResult.getInvoices().length == 0) {
                        addInvoice();
                    } else {
                        invoiceID = invoiceResult.getInvoices()[0].getInvoiceID();
                        fetchInvoiceDetails();
                        initViews(invoiceResult.getInvoices()[0]);
                    }
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(invoiceResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getAddInvoiceResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceResult>() {
            @Override
            public void onChanged(InvoiceResult invoiceResult) {
                if (invoiceResult.getErrorCode().equals("0")) {
                    invoiceID = invoiceResult.getInvoices()[0].getInvoiceID();
                    fetchInvoiceDetails();
                    initViews(invoiceResult.getInvoices()[0]);
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(invoiceResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getProductInfoResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<ProductResult>() {
            @Override
            public void onChanged(ProductResult productResult) {
                if (productResult.getErrorCode().equals("0")) {
                    int QTY = Integer.valueOf(binding.edTextQTY.getText().toString());
                    int sum = (int) (QTY * productResult.getProducts()[0].getCost());
                    binding.btnSum.setText(String.valueOf(sum));
                    binding.edTextUnitPrice.setText(String.valueOf(productResult.getProducts()[0].getCost()));
                    binding.btnProductName.setText(productResult.getProducts()[0].getProductName());
                    binding.btnProductID.setText(String.valueOf(productResult.getProducts()[0].getProductID()));
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(productResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getInvoiceDetailsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceDetailsResult>() {
            @Override
            public void onChanged(InvoiceDetailsResult invoiceDetailsResult) {
                if (invoiceDetailsResult.getErrorCode().equals("0")) {
                    setupAdapter(invoiceDetailsResult.getInvoiceDetails());
                }
            }
        });
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
    public void getNavigateEvent(NavigateEvent event) {
        binding.btnProductName.setText(event.getCaseProductInfo().getProductName());
        binding.btnProductID.setText(String.valueOf(event.getCaseProductInfo().getProductID()));
        binding.edTextUnitPrice.setText(String.valueOf(event.getCaseProductInfo()));
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true)
    public void getProductGroupIDEvent(PostProductGroupIDEvent event) {
        fetchProductInfo(event.getProductGroupID());
        binding.edTextQTY.setText("1");
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void fetchProductInfo(int productGroupID) {
        viewModel.getSipSupporterServiceProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/products/Info/";
        viewModel.fetchProductInfo(path, userLoginKey, productGroupID);
    }

    private void fetchInvoiceDetails() {
        viewModel.getSipSupporterServiceAddInvoiceDetailsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/InvoiceDetails/List/";
        viewModel.fetchInvoiceDetails(path, userLoginKey, invoiceID);
    }

    private void setupAdapter(InvoiceDetailsResult.InvoiceDetailsInfo[] invoiceDetailsInfoArray) {
        InvoiceDetailsAdapter adapter = new InvoiceDetailsAdapter(getContext(), Arrays.asList(invoiceDetailsInfoArray));
        binding.recyclerViewInvoiceDetails.setAdapter(adapter);
    }

    private void addInvoiceDetails() {
        viewModel.getSipSupporterServiceAddInvoiceDetailsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/InvoiceDetails/Add/";
        InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo = new InvoiceDetailsResult().new InvoiceDetailsInfo();
        viewModel.addInvoiceDetails(path, userLoginKey, invoiceDetailsInfo);
    }
}