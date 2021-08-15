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
import com.example.sipsupporterapp.adapter.CaseProductAdapter;
import com.example.sipsupporterapp.databinding.FragmentNewCaseProductBinding;
import com.example.sipsupporterapp.eventbus.NavigateEvent;
import com.example.sipsupporterapp.model.CaseProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.CaseProductViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

public class NewCaseProductFragment extends Fragment {
    private FragmentNewCaseProductBinding binding;
    private CaseProductViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private int caseID;

    public static NewCaseProductFragment newInstance() {
        NewCaseProductFragment fragment = new NewCaseProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
                R.layout.fragment_new_case_product,
                container,
                false);

        initViews();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewCaseProductFragmentArgs args = NewCaseProductFragmentArgs.fromBundle(getArguments());
        caseID = args.getCaseID();
        fetchCaseProducts();
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CaseProductViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceCaseProductResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void initViews() {
        binding.recyclerViewNewCaseProduct.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchCaseProducts() {
        String path = "/api/v1/CaseProduct/ListWithSelected/";
        viewModel.fetchCaseProductsWithSelected(path, userLoginKey, caseID);
    }

    private void setupObserver() {
        viewModel.getCaseProductsWithSelectedResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<CaseProductResult>() {
            @Override
            public void onChanged(CaseProductResult caseProductResult) {
                if (caseProductResult.getErrorCode().equals("0")) {
                    setupAdapter(caseProductResult.getCaseProducts());
                } else {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(caseProductResult.getError());
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                }
            }
        });

        viewModel.getItemClicked().observe(getViewLifecycleOwner(), new Observer<CaseProductResult.CaseProductInfo>() {
            @Override
            public void onChanged(CaseProductResult.CaseProductInfo caseProductInfo) {
                EventBus.getDefault().postSticky(new NavigateEvent(caseProductInfo));
                NavHostFragment.findNavController(NewCaseProductFragment.this).navigate(R.id.invoiceFragment);
            }
        });
    }

    private void setupAdapter(CaseProductResult.CaseProductInfo[] caseProductInfoArray) {
        CaseProductAdapter adapter = new CaseProductAdapter(viewModel, Arrays.asList(caseProductInfoArray), true);
        binding.recyclerViewNewCaseProduct.setAdapter(adapter);
    }
}