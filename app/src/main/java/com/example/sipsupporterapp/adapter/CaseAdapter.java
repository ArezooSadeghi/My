package com.example.sipsupporterapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.CaseAdapterItemBinding;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.List;
import java.util.Random;

public class CaseAdapter extends RecyclerView.Adapter<CaseAdapter.CaseHolder> {
    private Context context;
    private CaseViewModel viewModel;
    private List<CaseResult.CaseInfo> caseInfoList;

    final Random random = new Random();

    public CaseAdapter(CaseViewModel viewModel, List<CaseResult.CaseInfo> caseInfoList) {
        this.viewModel = viewModel;
        this.caseInfoList = caseInfoList;
    }

    @NonNull
    @Override
    public CaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CaseHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.case_adapter_item,
                parent,
                false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CaseHolder holder, int position) {
        holder.binding.container.removeAllViews();
        CaseResult.CaseInfo caseInfo = caseInfoList.get(position);
        holder.bindCaseInfo(caseInfo);
        int color = generateRandomColor();
        holder.binding.cardView.setCardBackgroundColor(color);

        for (int i = 0; i < caseInfo.getAssings().length; i++) {
            AssignResult.AssignInfo assignInfo = caseInfo.getAssings()[i];
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(50, 0, 0, 0);
            CheckBox checkBoxSeen = new CheckBox(context);
            CheckBox checkBoxFinish = new CheckBox(context);

            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(checkBoxSeen, ColorStateList.valueOf(R.color.pink));
            } else {
                checkBoxSeen.setButtonTintList(ColorStateList.valueOf(R.color.pink));
            }

            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(checkBoxFinish, ColorStateList.valueOf(R.color.pink));
            } else {
                checkBoxFinish.setButtonTintList(ColorStateList.valueOf(R.color.pink));
            }

            checkBoxSeen.setText("seen");
            checkBoxFinish.setText("finish");
            checkBoxSeen.setChecked(assignInfo.isSeen());
            checkBoxFinish.setChecked(assignInfo.isFinish());
            TextView assignUserFullName = new TextView(context);
            assignUserFullName.setTextColor(Color.BLACK);
            assignUserFullName.setTextSize(12);
            assignUserFullName.setLayoutParams(params);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.regular);
            assignUserFullName.setTypeface(typeface);
            assignUserFullName.setText(Converter.letterConverter(assignInfo.getAssignUserFullName()));
            linearLayout.addView(checkBoxFinish);
            linearLayout.addView(checkBoxSeen);
            linearLayout.addView(assignUserFullName);
            holder.binding.container.addView(linearLayout);

            checkBoxSeen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkBoxSeen.isChecked()) {
                        assignInfo.setSeen(checkBoxSeen.isChecked());
                        viewModel.getSeenClicked().setValue(assignInfo);
                    }
                }
            });

            checkBoxFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkBoxFinish.isChecked()) {
                        assignInfo.setFinish(checkBoxFinish.isChecked());
                        viewModel.getFinishClicked().setValue(assignInfo);
                    }
                }
            });
        }

        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("چاپ فاکتور", R.drawable.invoice_print))
                        .addItem(new PowerMenuItem("assign", R.drawable.assign))
                        .addItem(new PowerMenuItem("comment", R.drawable.comment))
                        .addItem(new PowerMenuItem("ویرایش", R.drawable.edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.remove))
                        .addItem(new PowerMenuItem("پایان کار", R.drawable.case_finish))
                        .addItem(new PowerMenuItem("مشاهده", R.drawable.magnifier))
                        .addItem(new PowerMenuItem("ثبت مبالغ واریزی", R.drawable.payment))
                        .addItem(new PowerMenuItem("محصولات این case"))
                        .addItem(new PowerMenuItem("تغییر گروه"))
                        .setIconSize(24)
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextSize(12)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getPrintInvoiceClicked().setValue(caseInfo);
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getAssignToOthersClicked().setValue(caseInfo.getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getRegisterCommentClicked().setValue(caseInfo.getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 3:
                                viewModel.getEditClicked().setValue(caseInfo);
                                powerMenu.dismiss();
                                break;
                            case 4:
                                viewModel.getDeleteClicked().setValue(caseInfo.getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 5:
                                viewModel.getCaseFinishClicked().setValue(caseInfo);
                                powerMenu.dismiss();
                                break;
                            case 6:
                                viewModel.getSeeClicked().setValue(caseInfo.getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 7:
                                viewModel.getAddPaymentClicked().setValue(caseInfo.getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 8:
                                viewModel.getCaseProductsClicked().setValue(caseInfo.getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 9:
                                viewModel.getChangeCaseTypeClicked().setValue(caseInfo);
                                powerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
            }
        });

        holder.binding.imageBadgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getSeeCommentClicked().setValue(caseInfo.getCaseID());
            }
        });
    }

    public int generateRandomColor() {
        final int baseColor = Color.WHITE;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + random.nextInt(256)) / 2;
        final int green = (baseGreen + random.nextInt(256)) / 2;
        final int blue = (baseBlue + random.nextInt(256)) / 2;

        return Color.rgb(red, green, blue);
    }

    @Override
    public int getItemCount() {
        return caseInfoList != null ? caseInfoList.size() : 0;
    }

    public class CaseHolder extends RecyclerView.ViewHolder {
        private CaseAdapterItemBinding binding;

        public CaseHolder(CaseAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindCaseInfo(CaseResult.CaseInfo caseInfo) {
            binding.setCaseInfo(caseInfo);
        }
    }
}
