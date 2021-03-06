package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.CustomerAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerHolder> {
    private Context context;
    private CustomerViewModel viewModel;
    private List<CustomerResult.CustomerInfo> customerInfoList;
    private String _date;

    public CustomerAdapter(CustomerViewModel viewModel, List<CustomerResult.CustomerInfo> customerInfoList, String _date) {
        this.viewModel = viewModel;
        this.customerInfoList = customerInfoList;
        this._date = _date;
    }

    @NonNull
    @Override
    public CustomerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CustomerHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.customer_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerHolder holder, int position) {
        holder.bind(position);
        CustomerResult.CustomerInfo customerInfo = customerInfoList.get(position);
        String date = customerInfo.getLastSeen().substring(0, 10);

        if (_date.equals(date)) {
            holder.binding.tvLastSeen.setTextColor(Color.YELLOW);
            holder.binding.tvCustomerID.setTextColor(Color.WHITE);
            holder.binding.tvCustomerName.setTextColor(Color.WHITE);
            holder.binding.tvCity.setTextColor(Color.WHITE);
        } else {
            holder.binding.tvLastSeen.setTextColor(context.getResources().getColor(R.color.dark_silver));
            holder.binding.tvCustomerID.setTextColor(context.getResources().getColor(R.color.light_silver));
            holder.binding.tvCustomerName.setTextColor(context.getResources().getColor(R.color.light_silver));
            holder.binding.tvCity.setTextColor(context.getResources().getColor(R.color.light_silver));
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

        public void bind(Integer position) {
            binding.setCustomerInfo(customerInfoList.get(position));
            binding.setCustomerViewModel(viewModel);
            binding.setPosition(position);
        }
    }
}

