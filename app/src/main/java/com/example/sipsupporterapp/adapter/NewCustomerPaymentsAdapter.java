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
import com.example.sipsupporterapp.viewmodel.NewCustomerPaymentsViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class NewCustomerPaymentsAdapter extends RecyclerView.Adapter<NewCustomerPaymentsAdapter.NewCustomerPaymentsHolder> {
    private Context context;
    private NewCustomerPaymentsViewModel viewModel;
    private List<CustomerPaymentInfo> customerPaymentInfoList;

    public NewCustomerPaymentsAdapter(Context context, NewCustomerPaymentsViewModel viewModel, List<CustomerPaymentInfo> customerPaymentInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.customerPaymentInfoList = customerPaymentInfoList;
    }

    @NonNull
    @Override
    public NewCustomerPaymentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewCustomerPaymentsHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.customer_payment_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewCustomerPaymentsHolder holder, int position) {
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
        return customerPaymentInfoList != null ? customerPaymentInfoList.size() : 0;
    }

    public class NewCustomerPaymentsHolder extends RecyclerView.ViewHolder {
        private CustomerPaymentAdapterItemBinding binding;

        public NewCustomerPaymentsHolder(CustomerPaymentAdapterItemBinding binding) {
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
