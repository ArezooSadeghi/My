package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.UserAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.UserViewModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.CustomerUsersHolder> {
    private Context context;
    private UserViewModel viewModel;
    private List<CustomerUserResult.CustomerUserInfo> customerUserInfoList;
    private String _date;

    public UserAdapter(UserViewModel viewModel, List<CustomerUserResult.CustomerUserInfo> customerUserInfoList, String _date) {
        this.viewModel = viewModel;
        this.customerUserInfoList = customerUserInfoList;
        this._date = _date;
    }

    @NonNull
    @Override
    public CustomerUsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CustomerUsersHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.user_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerUsersHolder holder, int position) {
        holder.bind(position);
        String date = customerUserInfoList.get(position).getLastSeen().substring(0, 10);

        if (_date.equals(date)) {
            holder.binding.ivStatus.setImageResource(R.drawable.online_user);
        } else {
            holder.binding.ivStatus.setImageResource(R.drawable.offline_user);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getItemClicked().setValue(customerUserInfoList.get(position).getCustomerID());
                SipSupportSharedPreferences.setCustomerUserId(context, customerUserInfoList.get(position).getCustomerUserID());
            }
        });

        String rowNumber = Converter.numberConverter((position + 1) + "");
        holder.binding.tvRow.setText(rowNumber);
    }

    @Override
    public int getItemCount() {
        return customerUserInfoList == null ? 0 : customerUserInfoList.size();
    }

    public class CustomerUsersHolder extends RecyclerView.ViewHolder {
        private UserAdapterItemBinding binding;

        public CustomerUsersHolder(UserAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Integer position) {
            binding.setCustomerUserInfo(customerUserInfoList.get(position));
            binding.setUserViewModel(viewModel);
            binding.setPosition(position);
        }
    }
}
