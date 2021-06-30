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
import com.example.sipsupporterapp.model.CustomerInfo;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerHolder> {

    private Context context;
    private CustomerViewModel viewModel;
    private List<CustomerInfo> customerInfoList;
    private String date;

    public CustomerAdapter(Context context, CustomerViewModel viewModel, List<CustomerInfo> customerInfoList, String date) {
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
        CustomerInfo customerInfo = customerInfoList.get(position);
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
            holder.binding.frameLayout.setBackgroundColor(Color.parseColor("#FAFD0F"));
            holder.binding.txtLastSeen.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.binding.frameLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.binding.txtLastSeen.setTextColor(Color.parseColor("#808080"));
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

        public void bindCustomerInfo(CustomerInfo info) {
            String city = Converter.letterConverter(info.getCity());
            binding.txtCity.setText(city);
            binding.txtLastSeen.setText(info.getLastSeen());

            String customerName = Converter.letterConverter(info.getCustomerName());
            binding.txtCustomerName.setText(customerName);
            binding.txtCustomerID.setText(String.valueOf(info.getCustomerID()));
        }
    }
}

