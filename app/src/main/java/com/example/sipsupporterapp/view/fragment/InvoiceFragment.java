package com.example.sipsupporterapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentInvoiceBinding;
import com.example.sipsupporterapp.viewmodel.InvoiceViewModel;

public class InvoiceFragment extends Fragment {
    private FragmentInvoiceBinding binding;
    private InvoiceViewModel viewModel;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_invoice,
                container,
                false);
        return binding.getRoot();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
    }
}