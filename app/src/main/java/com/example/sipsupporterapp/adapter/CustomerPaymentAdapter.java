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
    private List<CustomerPaymentInfo> customerPaymentInfoList;
    private CustomerPaymentViewModel viewModel;

    public CustomerPaymentAdapter(Context context, List<CustomerPaymentInfo> customerPaymentInfoList, CustomerPaymentViewModel viewModel) {
        this.context = context;
        this.customerPaymentInfoList = customerPaymentInfoList;
        this.viewModel = viewModel;
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
                                viewModel.getEditCustomerPaymentClicked().setValue(customerPaymentInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteCustomerPaymentClicked().setValue(customerPaymentInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getSeeDocumentsClickedSingleLiveEvent().setValue(customerPaymentInfoList.get(position));
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

        public void bindCustomerPaymentInfo(CustomerPaymentInfo customerPaymentInfo) {
            String bankAccountName = Converter.convert(customerPaymentInfo.getBankAccountName());
            binding.txtBankAccountName.setText(bankAccountName);
            binding.txtBankAccountNo.setText(customerPaymentInfo.getBankAccountNO());
            String bankName = Converter.convert(customerPaymentInfo.getBankName());
            binding.txtBankName.setText(bankName);

            String currencyFormat = NumberFormat.getNumberInstance(Locale.US).format(customerPaymentInfo.getPrice());
            binding.txtPrice.setText(currencyFormat + "تومان");

            if (customerPaymentInfo.getDatePayment() != 0) {
                String date = String.valueOf(customerPaymentInfo.getDatePayment());
                String year = date.substring(0, 4);
                String month = date.substring(4, 6);
                String day = date.substring(6);
                String dateFormat = year + "/" + month + "/" + day;
                binding.txtDatePayment.setText(dateFormat);
            }
        }
    }
}