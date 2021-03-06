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
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.CustomerPaymentViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerPaymentAdapter extends RecyclerView.Adapter<CustomerPaymentAdapter.CustomerPaymentInfoHolder> {
    private Context context;
    private CustomerPaymentViewModel viewModel;
    private List<CustomerPaymentResult.CustomerPaymentInfo> customerPaymentInfoList;
    private boolean flag;

    public CustomerPaymentAdapter(CustomerPaymentViewModel viewModel, List<CustomerPaymentResult.CustomerPaymentInfo> customerPaymentInfoList, boolean flag) {
        this.flag = flag;
        this.viewModel = viewModel;
        this.customerPaymentInfoList = customerPaymentInfoList;
    }

    public CustomerPaymentAdapter(List<CustomerPaymentResult.CustomerPaymentInfo> customerPaymentInfoList, boolean flag) {
        this.flag = flag;
        this.customerPaymentInfoList = customerPaymentInfoList;
    }

    @NonNull
    @Override
    public CustomerPaymentInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CustomerPaymentInfoHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.customer_payment_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerPaymentInfoHolder holder, int position) {
        holder.bind(customerPaymentInfoList.get(position));
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    PowerMenu powerMenu = new PowerMenu.Builder(context)
                            .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_see_attachments_item_title), R.drawable.see))
                            .setTextColor(Color.BLACK)
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
                                    viewModel.getSeeCustomerPaymentAttachmentsClicked().setValue(customerPaymentInfoList.get(position));
                                    powerMenu.dismiss();
                                    break;
                            }
                        }
                    });
                    powerMenu.showAsDropDown(view);
                } else {
                    PowerMenu powerMenu = new PowerMenu.Builder(context)
                            .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_edit_item_title), R.drawable.edit))
                            .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_delete_item_title), R.drawable.delete))
                            .addItem(new PowerMenuItem(context.getResources().getString(R.string.power_menu_see_attachments_item_title), R.drawable.see))
                            .setTextColor(Color.BLACK)
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

        public void bind(CustomerPaymentResult.CustomerPaymentInfo info) {
            binding.tvBankAccountName.setText(info.getBankAccountName());
            binding.tvBankName.setVisibility(!info.getBankName().isEmpty() ? View.VISIBLE : View.GONE);
            binding.tvBankName.setText(info.getBankName());
            binding.tvBankAccountNo.setText("???? ?????????? ????????:" + info.getBankAccountNO());
            binding.ivMore.setVisibility(viewModel == null ? View.GONE : View.VISIBLE);
            String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(info.getPrice());
            binding.tvPrice.setText(currencyFormat + "????????");
            String dateFormat = Converter.dateFormat(String.valueOf(info.getDatePayment()));
            binding.tvDatePayment.setText(dateFormat);
        }
    }
}
