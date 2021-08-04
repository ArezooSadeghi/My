package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.CustomerAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerHolder> {

    private Context context;
    private CustomerViewModel viewModel;
    private List<CustomerResult.CustomerInfo> customerInfoList;
    private String date;

    public CustomerAdapter(Context context, CustomerViewModel viewModel, List<CustomerResult.CustomerInfo> customerInfoList, String date) {
        this.context = context;
        this.viewModel = viewModel;
        this.customerInfoList = customerInfoList;
        this.date = date;
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.customer_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerHolder holder, int position) {
        CustomerResult.CustomerInfo customerInfo = customerInfoList.get(position);
        holder.bindCustomerInfo(customerInfo);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getItemClicked().setValue(customerInfo.getCustomerID());
                SipSupportSharedPreferences.setCustomerName(context, customerInfo.getCustomerName());
                SipSupportSharedPreferences.setCustomerTel(context, customerInfo.getTel());
            }
        });

        String date = customerInfo.getLastSeen().substring(0, 10);

        if (date != null && this.date != null && this.date.equals(date)) {
            holder.binding.txtLastSeen.setTextColor(Color.parseColor("#FFFF00"));
            holder.binding.txtCustomerName.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            holder.binding.txtLastSeen.setTextColor(Color.parseColor("#A0A0A0"));
            holder.binding.txtCustomerName.setTextColor(Color.parseColor("#B8B8B8"));
        }
    }

    @Override
    public int getItemCount() {
        return customerInfoList == null ? 0 : customerInfoList.size();
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        private CustomerAdapterItemBinding binding;

        public CustomerHolder(CustomerAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindCustomerInfo(CustomerResult.CustomerInfo customerInfo) {
            binding.txtCity.setVisibility(!customerInfo.getCity().isEmpty() ? View.VISIBLE : View.GONE);
            String city = Converter.letterConverter(customerInfo.getCity());
            binding.txtCity.setText(city);

            binding.txtLastSeen.setVisibility(!customerInfo.getLastSeen().isEmpty() ? View.VISIBLE : View.GONE);
            binding.txtLastSeen.setText(customerInfo.getLastSeen());

            String customerName = Converter.letterConverter(customerInfo.getCustomerName());
            binding.txtCustomerName.setText(customerName);
            binding.txtCustomerID.setText(String.valueOf(customerInfo.getCustomerID()));
        }
    }
}

