package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.CustomerSupportAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.CustomerSupportViewModel;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class CustomerSupportAdapter extends RecyclerView.Adapter<CustomerSupportAdapter.CustomerSupportHolder> {
    private final CustomerSupportViewModel viewModel;
    private final List<CustomerSupportResult.CustomerSupportInfo> customerSupports;

    public CustomerSupportAdapter(CustomerSupportViewModel viewModel, List<CustomerSupportResult.CustomerSupportInfo> customerSupports) {
        this.viewModel = viewModel;
        this.customerSupports = customerSupports;
    }

    @NonNull
    @Override
    public CustomerSupportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerSupportHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.customer_support_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerSupportHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        CustomerSupportResult.CustomerSupportInfo info = customerSupports.get(position);
        holder.bind(info);

        holder.binding.ivMore.setOnClickListener(view -> {
            PowerMenu powerMenu = new PowerMenu.Builder(context)
                    .addItem(new PowerMenuItem(context.getString(R.string.power_menu_see_attachments_item_title), R.drawable.see))
                    .setTextColor(Color.BLACK)
                    .setTextGravity(Gravity.RIGHT)
                    .setIconSize(24)
                    .setTextSize(12)
                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                    .build();

            powerMenu.setOnMenuItemClickListener((i, item) -> {
                if (i == 0) {
                    viewModel.getSeeAttachmentsClicked().setValue(info);
                    powerMenu.dismiss();
                }
            });
            powerMenu.showAsDropDown(view);
        });
    }

    @Override
    public int getItemCount() {
        return customerSupports != null ? customerSupports.size() : 0;
    }

    public class CustomerSupportHolder extends RecyclerView.ViewHolder {
        private final CustomerSupportAdapterItemBinding binding;

        public CustomerSupportHolder(CustomerSupportAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CustomerSupportResult.CustomerSupportInfo info) {
            binding.tvCustomerSupportID.setText(String.valueOf(info.getCustomerSupportID()));
            binding.tvRegTime.setText(info.getRegTime());
            binding.tvQuestion.setText(Converter.letterConverter(info.getQuestion()));
            binding.tvAnswer.setText(Converter.letterConverter(info.getAnswer()));
            binding.tvUserFullName.setText(Converter.letterConverter(info.getUserFullName()).concat(":"));
        }
    }
}
