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
import com.example.sipsupporterapp.databinding.CommentAdapterItemBinding;
import com.example.sipsupporterapp.model.CommentResult;
import com.example.sipsupporterapp.viewmodel.CommentViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private Context context;
    private CommentViewModel viewModel;
    private List<CommentResult.CommentInfo> commentInfoList;

    public CommentAdapter(Context context, CommentViewModel viewModel, List<CommentResult.CommentInfo> commentInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.commentInfoList = commentInfoList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.comment_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.bindCommentInfo(commentInfoList.get(position));
        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("اصلاح", R.drawable.edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.remove))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextSize(12)
                        .setIconSize(24)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getEditClicked().setValue(commentInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(commentInfoList.get(position).getCommentID());
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
        return commentInfoList != null ? commentInfoList.size() : 0;
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        private CommentAdapterItemBinding binding;

        public CommentHolder(CommentAdapterItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bindCommentInfo(CommentResult.CommentInfo commentInfo) {
            binding.txtUserFullName.setText(commentInfo.getUserFullName());
            binding.txtComment.setText(commentInfo.getComment());
        }
    }
}
