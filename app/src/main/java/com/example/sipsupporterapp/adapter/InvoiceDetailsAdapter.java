package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.InvoiceDetailsAdapterItemBinding;
import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.viewmodel.InvoiceViewModel;
import com.example.sipsupporterapp.viewmodel.PrintViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class InvoiceDetailsAdapter extends RecyclerView.Adapter<InvoiceDetailsAdapter.InvoiceDetailsHolder> {
    private Context context;
    private InvoiceViewModel viewModel;
    private PrintViewModel printViewModel;
    private List<InvoiceDetailsResult.InvoiceDetailsInfo> invoiceDetailsInfoList;

    public InvoiceDetailsAdapter(Context context, InvoiceViewModel viewModel, List<InvoiceDetailsResult.InvoiceDetailsInfo> invoiceDetailsInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.invoiceDetailsInfoList = invoiceDetailsInfoList;
    }

    public InvoiceDetailsAdapter(Context context, PrintViewModel printViewModel, List<InvoiceDetailsResult.InvoiceDetailsInfo> invoiceDetailsInfoList) {
        this.context = context;
        this.printViewModel = printViewModel;
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

        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("ویرایش", R.drawable.new_edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.new_delete))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getEditClicked().setValue(invoiceDetailsInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(invoiceDetailsInfoList.get(position).getInvoiceDetailsID());
                                powerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
            }
        });
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
            binding.txtProductDescription.setText(info.getDescription());
            binding.txtProductID.setText(String.valueOf(info.getProductID()));
            binding.txtProductName.setText(info.getProductName());
        }
    }
}
