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
import com.example.sipsupporterapp.databinding.PaymentAdapterItemBinding;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.viewmodel.PaymentViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {
    private Context context;
    private PaymentViewModel viewModel;
    private List<PaymentResult.PaymentInfo> paymentInfoList;

    public PaymentAdapter(PaymentViewModel viewModel, List<PaymentResult.PaymentInfo> paymentInfoList) {
        this.viewModel = viewModel;
        this.paymentInfoList = paymentInfoList;
    }

    @NonNull
    @Override
    public PaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new PaymentHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.payment_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHolder holder, int position) {
        PaymentResult.PaymentInfo paymentInfo = paymentInfoList.get(position);
        holder.bindPaymentInfo(paymentInfo);

        holder.binding.imgBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("ویرایش", R.drawable.edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.remove))
                        .addItem(new PowerMenuItem("مشاهده مستندات", R.drawable.see_document))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextGravity(Gravity.RIGHT)
                        .setIconSize(24)
                        .setTextSize(12)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getEditClicked().setValue(paymentInfo);
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getDeleteClicked().setValue(paymentInfo.getPaymentID());
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getSeePaymentAttachmentsClicked().setValue(paymentInfo);
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
        return paymentInfoList == null ? 0 : paymentInfoList.size();
    }

    public class PaymentHolder extends RecyclerView.ViewHolder {
        private PaymentAdapterItemBinding binding;

        public PaymentHolder(PaymentAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindPaymentInfo(PaymentResult.PaymentInfo paymentInfo) {
            binding.setPaymentInfo(paymentInfo);
        }
    }
}
