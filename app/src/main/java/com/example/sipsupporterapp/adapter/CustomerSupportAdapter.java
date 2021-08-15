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
import com.example.sipsupporterapp.databinding.CustomerSupportAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.viewmodel.CustomerSupportViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class CustomerSupportAdapter extends RecyclerView.Adapter<CustomerSupportAdapter.CustomerSupportInfoHolder> {
    private Context context;
    private CustomerSupportViewModel viewModel;
    private List<CustomerSupportResult.CustomerSupportInfo> customerSupportInfoList;

    public CustomerSupportAdapter(CustomerSupportViewModel viewModel, List<CustomerSupportResult.CustomerSupportInfo> customerSupportInfoList) {
        this.viewModel = viewModel;
        this.customerSupportInfoList = customerSupportInfoList;
    }

    @NonNull
    @Override
    public CustomerSupportInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CustomerSupportInfoHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.customer_support_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerSupportInfoHolder holder, int position) {
        CustomerSupportResult.CustomerSupportInfo customerSupportInfo = customerSupportInfoList.get(position);
        holder.bindCustomerSupportInfo(customerSupportInfoList.get(position));

        holder.binding.imgBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("مشاهده مستندات", R.drawable.see_document))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextGravity(Gravity.RIGHT)
                        .setIconSize(24)
                        .setTextSize(12)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int position, PowerMenuItem item) {
                        switch (position) {
                            case 0:
                                viewModel.getSeeCustomerSupportAttachmentsClicked().setValue(customerSupportInfo);
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
        return customerSupportInfoList == null ? 0 : customerSupportInfoList.size();
    }

    public class CustomerSupportInfoHolder extends RecyclerView.ViewHolder {
        private CustomerSupportAdapterItemBinding binding;

        public CustomerSupportInfoHolder(CustomerSupportAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindCustomerSupportInfo(CustomerSupportResult.CustomerSupportInfo customerSupportInfo) {
            binding.setCustomerSupportInfo(customerSupportInfo);
        }
    }
}
