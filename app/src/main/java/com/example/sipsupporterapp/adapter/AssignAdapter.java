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
import com.example.sipsupporterapp.databinding.AssignAdapterItemBinding;
import com.example.sipsupporterapp.model.AssignInfo;
import com.example.sipsupporterapp.viewmodel.AssignViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class AssignAdapter extends RecyclerView.Adapter<AssignAdapter.AssignHolder> {
    private Context context;
    private AssignViewModel viewModel;
    private List<AssignInfo> assignInfoList;

    public AssignAdapter(Context context, AssignViewModel viewModel, List<AssignInfo> assignInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.assignInfoList = assignInfoList;
    }

    @NonNull
    @Override
    public AssignHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssignHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.assign_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull AssignHolder holder, int position) {
        holder.bindAssignInfo(assignInfoList.get(position));

        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("اصلاح", R.drawable.new_edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.new_delete))
                        .addItem(new PowerMenuItem("ثبت نظر"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextSize(14)
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
                                viewModel.getRegisterCommentClicked().setValue(true);
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

        public void bindAssignInfo(AssignInfo assignInfo) {
            binding.txtAssignUserFullName.setText(assignInfo.getAssignUserFullName());
            binding.txtDescription.setText(assignInfo.getDescription());
            if (assignInfo.isFinish()) {
                binding.checkBoxFinish.setChecked(true);
            }
            if (assignInfo.isSeen()) {
                binding.checkBoxSeen.setChecked(true);
            }
            if (assignInfo.isSeenAssigner()) {
                binding.checkBoxSeenAssigner.setChecked(true);
            }
        }
    }
}
