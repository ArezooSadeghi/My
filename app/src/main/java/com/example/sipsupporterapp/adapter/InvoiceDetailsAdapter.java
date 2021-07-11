package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.InvoiceDetailsAdapterItemBinding;
import com.example.sipsupporterapp.model.InvoiceDetailsResult;

import java.util.List;

public class InvoiceDetailsAdapter extends RecyclerView.Adapter<InvoiceDetailsAdapter.InvoiceDetailsHolder> {
    private Context context;
    private List<InvoiceDetailsResult.InvoiceDetailsInfo> invoiceDetailsInfoList;

    public InvoiceDetailsAdapter(Context context, List<InvoiceDetailsResult.InvoiceDetailsInfo> invoiceDetailsInfoList) {
        this.context = context;
        this.invoiceDetailsInfoList = invoiceDetailsInfoList;
    }

    @NonNull
    @Override
    public InvoiceDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InvoiceDetailsHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.invoice_details_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceDetailsHolder holder, int position) {
        holder.bindInvoiceDetailsInfo(invoiceDetailsInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return invoiceDetailsInfoList != null ? invoiceDetailsInfoList.size() : 0;
    }

    public class InvoiceDetailsHolder extends RecyclerView.ViewHolder {
        private InvoiceDetailsAdapterItemBinding binding;

        public InvoiceDetailsHolder(InvoiceDetailsAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindInvoiceDetailsInfo(InvoiceDetailsResult.InvoiceDetailsInfo info) {
            binding.txtQTY.setText(String.valueOf(info.getQTY()));
            binding.txtProductDescription.setText(info.getDescription());
            binding.txtProductID.setText(info.getProductID());
            binding.txtProductName.setText(info.getProductName());
        }
    }
}
