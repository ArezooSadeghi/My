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
import com.example.sipsupporterapp.databinding.CustomerPaymentAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class CustomerPaymentAdapter extends RecyclerView.Adapter<CustomerPaymentAdapter.CustomerPaymentInfoHolder> {
    private Context context;
    private CustomerPaymentViewModel viewModel;
    private List<CustomerPaymentResult.CustomerPaymentInfo> customerPaymentInfoList;

    public CustomerPaymentAdapter(CustomerPaymentViewModel viewModel, List<CustomerPaymentResult.CustomerPaymentInfo> customerPaymentInfoList) {
        this.viewModel = viewModel;
        this.customerPaymentInfoList = customerPaymentInfoList;
    }

    public CustomerPaymentAdapter(List<CustomerPaymentResult.CustomerPaymentInfo> customerPaymentInfoList) {
        this.customerPaymentInfoList = customerPaymentInfoList;
    }

    @NonNull
    @Override
    public CustomerPaymentInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CustomerPaymentInfoHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.customer_payment_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerPaymentInfoHolder holder, int position) {
        CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo = customerPaymentInfoList.get(position);
        holder.bindCustomerPaymentInfo(customerPaymentInfo);
        holder.binding.imgBtnMore.setVisibility(viewModel == null ? View.GONE : View.VISIBLE);
        holder.binding.imgBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("ویرایش", R.drawable.edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.remove))
                        .addItem(new PowerMenuItem("مشاهده مستندات", R.drawable.see_document))
                        .setTextColor(Color.parseColor("#000000"))
                        .setIconSize(24)
                        .setTextSize(12)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getEditClicked().setValue(customerPaymentInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(customerPaymentInfoList.get(position).getCustomerPaymentID());
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getSeeCustomerPaymentAttachmentsClicked().setValue(customerPaymentInfoList.get(position));
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
        return customerPaymentInfoList == null ? 0 : customerPaymentInfoList.size();
    }

    public class CustomerPaymentInfoHolder extends RecyclerView.ViewHolder {
        private CustomerPaymentAdapterItemBinding binding;

        public CustomerPaymentInfoHolder(CustomerPaymentAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindCustomerPaymentInfo(CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
            binding.setCustomerPaymentInfo(customerPaymentInfo);
        }
    }
}
