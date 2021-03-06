package com.example.sipsupporterapp.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.InvoiceDetailsAdapter;
import com.example.sipsupporterapp.databinding.FragmentInvoiceBinding;
import com.example.sipsupporterapp.eventbus.NavigateEvent;
import com.example.sipsupporterapp.eventbus.newDeleteEvent;
import com.example.sipsupporterapp.eventbus.selectProductEvent;
import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.model.InvoiceResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.dialog.EditInvoiceDetailsDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDialogFragment;
import com.example.sipsupporterapp.viewmodel.InvoiceViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class InvoiceFragment extends Fragment {
    private FragmentInvoiceBinding binding;
    private InvoiceViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey, customerName;
    private long cost;
    private int caseID, customerID, invoiceID, invoiceDetailsID, sum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
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
        initRecyclerView();

        return binding.getRoot();
    }

    private void initRecyclerView() {
        binding.recyclerViewInvoiceDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider_recycler_view));
        binding.recyclerViewInvoiceDetails.addItemDecoration(dividerItemDecoration);
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
        viewModel = new ViewModelProvider(requireActivity()).get(InvoiceViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceInvoiceResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.getSipSupporterServiceInvoiceDetailsResult(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.getSipSupporterServiceProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void handleEvents() {
        binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvoiceFragmentDirections.ActionInvoiceFragmentToProductsFragment action = InvoiceFragmentDirections.actionInvoiceFragmentToProductsFragment();
                action.setFlag(true);
                NavHostFragment.findNavController(InvoiceFragment.this).navigate(action);
            }
        });

        binding.ivAddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.txtProductID.getText().toString().isEmpty()) {
                    handleError("???????? ?????????? ?????????? ???? ???????????? ????????");
                } else {
                    addInvoiceDetails();
                }
            }
        });

        binding.fabPrintInvoice.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                InvoiceFragmentDirections.ActionInvoiceFragmentToPrintFragment action = InvoiceFragmentDirections.actionInvoiceFragmentToPrintFragment();
                action.setInvoiceID(invoiceID);
                NavHostFragment.findNavController(InvoiceFragment.this).navigate(action);
            }
        });

        binding.edTextQTY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    int QTY = Integer.valueOf(s.toString());
                    sum = (int) (QTY * cost);
                    String currencyFormatOne = NumberFormat.getNumberInstance(Locale.US).format(sum);
                    binding.txtSum.setText(currencyFormatOne);
                }
            }
        });
    }

    private void handleError(String msg) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(msg);
        dialog.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
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
        binding.txtInvoiceID.setText("??????????: " + invoiceInfo.getInvoiceID());
        binding.txtInvoiceDate.setText("??????????: " + invoiceInfo.getInvoiceDate());
        if (invoiceInfo.getCustomerName() != null) {
            binding.txtCustomerName.setText(Converter.letterConverter("?????? ??????????: " + invoiceInfo.getCustomerName()));
        } else {
            binding.txtCustomerName.setText("?????? ??????????: -");
        }
    }

    private void setupObserver() {
        viewModel.getInvoiceInfoByCaseIDResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceResult>() {
            @Override
            public void onChanged(InvoiceResult invoiceResult) {
                if (invoiceResult.getErrorCode().equals("0")) {
                    if (invoiceResult.getInvoices().length == 0) {
                        if (customerID > 0) {
                            addInvoice();
                        }
                    } else {
                        invoiceID = invoiceResult.getInvoices()[0].getInvoiceID();
                        fetchInvoiceDetails();
                        initViews(invoiceResult.getInvoices()[0]);
                    }
                } else {
                    handleError(invoiceResult.getError());
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
                    handleError(invoiceResult.getError());
                }
            }
        });

        viewModel.getProductInfoResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<ProductResult>() {
            @Override
            public void onChanged(ProductResult productResult) {
                if (productResult.getErrorCode().equals("0")) {
                    cost = productResult.getProducts()[0].getCost();
                    int QTY = Integer.valueOf(binding.edTextQTY.getText().toString());
                    sum = (int) (QTY * cost);

                    String currencyFormatOne = NumberFormat.getNumberInstance(Locale.US).format(sum);
                    binding.txtSum.setText(currencyFormatOne);

                    String currencyFormatTwo = NumberFormat.getNumberInstance(Locale.US).format(productResult.getProducts()[0].getCost());
                    binding.edTextUnitPrice.setText(currencyFormatTwo);

                    binding.txtProductName.setText(productResult.getProducts()[0].getProductName());
                    binding.txtProductID.setText(String.valueOf(productResult.getProducts()[0].getProductID()));
                } else {
                    handleError(productResult.getError());
                }
            }
        });

        viewModel.getInvoiceDetailsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceDetailsResult>() {
            @Override
            public void onChanged(InvoiceDetailsResult invoiceDetailsResult) {
                if (invoiceDetailsResult.getErrorCode().equals("0")) {
                    long finalSumPrice = 0;
                    for (InvoiceDetailsResult.InvoiceDetailsInfo info : invoiceDetailsResult.getInvoiceDetails()) {
                        finalSumPrice += info.getUnitPrice();
                    }

                    String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(finalSumPrice);
                    binding.btnSumPrice.setText(currencyFormat);
                    setupAdapter(invoiceDetailsResult.getInvoiceDetails());
                }
            }
        });

        viewModel.getAddInvoiceDetailsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceDetailsResult>() {
            @Override
            public void onChanged(InvoiceDetailsResult invoiceDetailsResult) {
                if (invoiceDetailsResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("?????????? ???? ???????????? ?????? ????");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchInvoiceDetails();
                } else {
                    handleError(invoiceDetailsResult.getError());
                }
            }
        });

        viewModel.getEditClicked().observe(getViewLifecycleOwner(), new Observer<InvoiceDetailsResult.InvoiceDetailsInfo>() {
            @Override
            public void onChanged(InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo) {
                EditInvoiceDetailsDialogFragment fragment = EditInvoiceDetailsDialogFragment.newInstance(invoiceDetailsInfo.getInvoiceDetailsID(), invoiceDetailsInfo.getInvoiceID(), invoiceDetailsInfo.getProductID(), invoiceDetailsInfo.getProductName(), invoiceDetailsInfo.getQTY(), invoiceDetailsInfo.getUnitPrice(), invoiceDetailsInfo.getDescription());
                fragment.show(getParentFragmentManager(), EditInvoiceDetailsDialogFragment.TAG);
            }
        });

        viewModel.getRefresh().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refresh) {
                fetchInvoiceDetails();
            }
        });

        viewModel.getDeleteClicked().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer invoiceDetails_ID) {
                invoiceDetailsID = invoiceDetails_ID;
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance("?????? ???? ???????????? ?????????? ?????????????? ???? ?????? ??????????????");
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });

        viewModel.getDeleteInvoiceDetailsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<InvoiceDetailsResult>() {
            @Override
            public void onChanged(InvoiceDetailsResult invoiceDetailsResult) {
                if (invoiceDetailsResult.getErrorCode().equals("0")) {
                    SuccessDialogFragment fragment = SuccessDialogFragment.newInstance("?????????? ?????????????? ???? ???????????? ?????? ????");
                    fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
                    fetchInvoiceDetails();
                } else {
                    handleError(invoiceDetailsResult.getError());
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
        binding.txtProductName.setText(event.getCaseProductInfo().getProductName());
        binding.txtProductID.setText(String.valueOf(event.getCaseProductInfo().getProductID()));
        binding.edTextUnitPrice.setText(String.valueOf(event.getCaseProductInfo()));
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true)
    public void getProductGroupIDEvent(selectProductEvent event) {
        fetchProductInfo(event.getProductGroupID());
        binding.edTextQTY.setText("1");
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe
    public void getYesDeleteEvent(newDeleteEvent event) {
        deleteInvoiceDetails(invoiceDetailsID);
    }

    private void fetchProductInfo(int productGroupID) {
        String path = "/api/v1/products/Info/";
        viewModel.fetchProductInfo(path, userLoginKey, productGroupID);
    }

    private void fetchInvoiceDetails() {
        String path = "/api/v1/InvoiceDetails/List/";
        viewModel.fetchInvoiceDetails(path, userLoginKey, invoiceID);
    }

    private void setupAdapter(InvoiceDetailsResult.InvoiceDetailsInfo[] invoiceDetailsInfoArray) {
        InvoiceDetailsAdapter adapter = new InvoiceDetailsAdapter(viewModel, Arrays.asList(invoiceDetailsInfoArray), false);
        binding.recyclerViewInvoiceDetails.setAdapter(adapter);
    }

    private void addInvoiceDetails() {
        String path = "/api/v1/InvoiceDetails/Add/";
        InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo = new InvoiceDetailsResult.InvoiceDetailsInfo();
        invoiceDetailsInfo.setInvoiceID(invoiceID);
        invoiceDetailsInfo.setProductName(binding.txtProductName.getText().toString());
        invoiceDetailsInfo.setQTY(Integer.valueOf(binding.edTextQTY.getText().toString()));
        invoiceDetailsInfo.setProductID(Integer.valueOf(binding.txtProductID.getText().toString()));
        invoiceDetailsInfo.setDescription(binding.edTextProductDescription.getText().toString());

        String unitPrice = binding.edTextUnitPrice.getText().toString().replaceAll(",", "");
        invoiceDetailsInfo.setUnitPrice(Integer.valueOf(unitPrice));

        viewModel.addInvoiceDetails(path, userLoginKey, invoiceDetailsInfo);
    }

    private void deleteInvoiceDetails(int invoiceDetailsID) {
        String path = "/api/v1/InvoiceDetails/Delete/";
        viewModel.deleteInvoiceDetails(path, userLoginKey, invoiceDetailsID);
    }
}