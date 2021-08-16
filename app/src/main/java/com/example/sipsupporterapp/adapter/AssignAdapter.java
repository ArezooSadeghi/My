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
import com.example.sipsupporterapp.databinding.AssignAdapterItemBinding;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.viewmodel.AssignViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class AssignAdapter extends RecyclerView.Adapter<AssignAdapter.AssignHolder> {
    private Context context;
    private AssignViewModel viewModel;
    private List<AssignResult.AssignInfo> assignInfoList;

    public AssignAdapter(AssignViewModel viewModel, List<AssignResult.AssignInfo> assignInfoList) {
        this.viewModel = viewModel;
        this.assignInfoList = assignInfoList;
    }

    @NonNull
    @Override
    public AssignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new AssignHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.assign_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull AssignHolder holder, int position) {
        holder.bind(position);

        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("اصلاح", R.drawable.edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.remove))
                        .addItem(new PowerMenuItem("ثبت نظر", R.drawable.comment))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextSize(12)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setIconSize(24)
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getEditClicked().setValue(assignInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(assignInfoList.get(position).getAssignID());
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getAddClicked().setValue(true);
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
        return assignInfoList != null ? assignInfoList.size() : 0;
    }

    public class AssignHolder extends RecyclerView.ViewHolder {
        private AssignAdapterItemBinding binding;

        public AssignHolder(AssignAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Integer position) {
            binding.setAssignInfo(assignInfoList.get(position));
            binding.setAssignViewModel(viewModel);
            binding.setPosition(position);
        }
    }
}
