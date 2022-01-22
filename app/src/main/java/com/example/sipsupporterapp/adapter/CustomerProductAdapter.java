package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.CustomerProductAdapeterItemBinding;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.CustomerProductViewModel;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.CustomerProductHolder> {
    private final CustomerProductViewModel viewModel;
    private final List<CustomerProductResult.CustomerProductInfo> customerProducts;

    public CustomerProductAdapter(CustomerProductViewModel viewModel, List<CustomerProductResult.CustomerProductInfo> customerProducts) {
        this.viewModel = viewModel;
        this.customerProducts = customerProducts;
    }

    @NonNull
    @Override
    public CustomerProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerProductHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.customer_product_adapeter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerProductHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        CustomerProductResult.CustomerProductInfo info = customerProducts.get(position);
        holder.bind(info);

        holder.binding.ivMore.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(context)
                    .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_edit_item_title), R.drawable.edit))
                    .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_delete_item_title), R.drawable.delete))
                    .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_see_attachments_item_title), R.drawable.see))
                    .setTextColor(Color.BLACK)
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .setTextSize(12)
                    .setIconSize(24)
                    .setTextGravity(Gravity.RIGHT)
                    .build();

            powerMenu.setOnMenuItemClickListener((i, item) -> {
                switch (i) {
                    case 0:
                        viewModel.getEditClicked().setValue(info.getCustomerProductID());
                        powerMenu.dismiss();
                        break;
                    case 1:
                        viewModel.getDeleteClicked().setValue(info.getCustomerProductID());
                        powerMenu.dismiss();
                        break;
                    case 2:
                        viewModel.getSeeAttachmentsClicked().setValue(info);
                        powerMenu.dismiss();
                        break;
                }
            });
            powerMenu.showAsDropDown(view);
        });
    }

    @Override
    public int getItemCount() {
        return customerProducts != null ? customerProducts.size() : 0;
    }

    public class CustomerProductHolder extends RecyclerView.ViewHolder {
        private final CustomerProductAdapeterItemBinding binding;

        public CustomerProductHolder(CustomerProductAdapeterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CustomerProductResult.CustomerProductInfo info) {
            binding.tvProductName.setText(String.valueOf(info.getProductName()));
            binding.tvInvoicePrice.setText(Converter.currencyFormat(info.getInvoicePrice()).concat("ریال"));
            binding.tvUserFullName.setText(Converter.letterConverter(info.getUserFullName()));
            binding.checkBoxInvoicePayment.setChecked(info.isInvoicePayment());
            binding.checkBoxFinish.setChecked(info.isFinish());
            if (!info.getDescription().isEmpty()) {
                binding.tvDescription.setVisibility(View.VISIBLE);
                binding.tvDescription.setText(Converter.letterConverter(info.getDescription()));
            } else
                binding.tvDescription.setVisibility(View.GONE);
        }
    }
}
