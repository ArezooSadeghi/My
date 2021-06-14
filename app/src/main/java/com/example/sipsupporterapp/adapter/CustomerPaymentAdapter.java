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
import com.example.sipsupporterapp.databinding.CustomerPaymentAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
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
    private List<CustomerPaymentInfo> customerPaymentInfoList;

    public CustomerPaymentAdapter(Context context, CustomerPaymentViewModel viewModel, List<CustomerPaymentInfo> customerPaymentInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.customerPaymentInfoList = customerPaymentInfoList;
    }

    @NonNull
    @Override
    public CustomerPaymentInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerPaymentInfoHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.customer_payment_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerPaymentInfoHolder holder, int position) {
        holder.bindCustomerPaymentInfo(customerPaymentInfoList.get(position));

        holder.binding.imgBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("ویرایش", R.drawable.new_edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.new_delete))
                        .addItem(new PowerMenuItem("مشاهده مستندات"))
                        .setTextColor(Color.parseColor("#000000"))
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
                                viewModel.getDeleteClicked().setValue(customerPaymentInfoList.get(position));
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

        public void bindCustomerPaymentInfo(CustomerPaymentInfo info) {
            String bankAccountName = Converter.convert(info.getBankAccountName());
            binding.txtBankAccountName.setText(bankAccountName);
            binding.txtBankAccountNo.setText(info.getBankAccountNO());
            String bankName = Converter.convert(info.getBankName());
            binding.txtBankName.setText(bankName);

            String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(info.getPrice());
            binding.txtPrice.setText(currencyFormat + "تومان");

            if (info.getDatePayment() != 0) {
                String date = String.valueOf(info.getDatePayment());
                String year = date.substring(0, 4);
                String month = date.substring(4, 6);
                String day = date.substring(6);
                String dateFormat = year + "/" + month + "/" + day;
                binding.txtDatePayment.setText(dateFormat);
            }
        }
    }
}
