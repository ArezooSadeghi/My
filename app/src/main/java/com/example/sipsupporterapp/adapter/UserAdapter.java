package com.example.sipsupporterapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.UserAdapterItemBinding;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.UserViewModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private final UserViewModel viewModel;
    private final List<CustomerUserResult.CustomerUserInfo> users;
    private String _date;

    public UserAdapter(UserViewModel viewModel, List<CustomerUserResult.CustomerUserInfo> users, String _date) {
        this.viewModel = viewModel;
        this.users = users;
        this._date = _date;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.user_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        CustomerUserResult.CustomerUserInfo info = users.get(position);
        holder.bind(info);
        String date = users.get(position).getLastSeen().substring(0, 10);

        if (_date.equals(date))
            holder.binding.ivStatus.setImageResource(R.drawable.online_user);
        else
            holder.binding.ivStatus.setImageResource(R.drawable.offline_user);

        String rowNumber = Converter.numberConverter((position + 1) + "");
        holder.binding.tvRow.setText(rowNumber);

        holder.binding.getRoot().setOnClickListener(view -> {
            viewModel.getItemClicked().setValue(info);
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        private final UserAdapterItemBinding binding;

        public UserHolder(UserAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CustomerUserResult.CustomerUserInfo info) {
            binding.tvUserName.setText(Converter.letterConverter(info.getUserName()));
            binding.tvLastSeen.setText(info.getLastSeen());
        }
    }
}
