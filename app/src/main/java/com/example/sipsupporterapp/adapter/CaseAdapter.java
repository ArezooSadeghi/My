package com.example.sipsupporterapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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

    public CaseAdapter(Context context, CaseViewModel viewModel, List<CaseResult.CaseInfo> caseInfoList) {
        this.context = context;
        this.viewModel = viewModel;
        this.caseInfoList = caseInfoList;
    }

    @NonNull
    @Override
    public CaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CaseHolder(DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.case_adapter_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull CaseHolder holder, int position) {
        holder.bindCaseInfo(caseInfoList.get(position));
        int color = generateRandomColor();
        holder.binding.cardView.setCardBackgroundColor(color);

        for (int i = 0; i < caseInfoList.get(position).getAssings().length; i++) {
            AssignResult.AssignInfo assignInfo = caseInfoList.get(position).getAssings()[i];
            LinearLayout linearLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 50, 0);
            RadioButton radioButtonSeen = new RadioButton(context);
            RadioButton radioButtonFinish = new RadioButton(context);
            radioButtonSeen.setText("seen");
            radioButtonFinish.setText("finish");
            radioButtonSeen.setChecked(assignInfo.isSeen());
            radioButtonFinish.setChecked(assignInfo.isFinish());
            radioButtonSeen.setLayoutParams(params);
            TextView assignUserFullName = new TextView(context);
            assignUserFullName.setTextColor(Color.BLACK);
            assignUserFullName.setTextSize(12);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.regular);
            assignUserFullName.setTypeface(typeface);
            assignUserFullName.setText(assignInfo.getAssignUserFullName());
            linearLayout.addView(radioButtonFinish);
            linearLayout.addView(radioButtonSeen);
            linearLayout.addView(assignUserFullName);
            holder.binding.container.addView(linearLayout);
        }

        holder.binding.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = new PowerMenu.Builder(context)
                        .addItem(new PowerMenuItem("محصول های این case"))
                        .addItem(new PowerMenuItem("صدور فاکتور", R.drawable.ic_file))
                        .addItem(new PowerMenuItem("به همکاران assign", R.drawable.user))
                        .addItem(new PowerMenuItem("ثبت comment", R.drawable.com))
                        .addItem(new PowerMenuItem("اصلاح", R.drawable.new_edit))
                        .addItem(new PowerMenuItem("حذف", R.drawable.new_delete))
                        .addItem(new PowerMenuItem("پایان کار"))
                        .addItem(new PowerMenuItem("مشاهده", R.drawable.magnifier))
                        .addItem(new PowerMenuItem("تغییر گروه"))
                        .setTextColor(Color.parseColor("#000000"))
                        .setTextSize(14)
                        .setTextGravity(Gravity.RIGHT)
                        .build();

                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                viewModel.getCaseProductsClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 1:
                                viewModel.getPrintInvoiceClicked().setValue(caseInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 2:
                                viewModel.getAssignToOthersClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 3:
                                viewModel.getRegisterCommentClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 4:
                                viewModel.getEditClicked().setValue(caseInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 5:
                                viewModel.getDeleteClicked().setValue(caseInfoList.get(position).getCaseID());
                                powerMenu.dismiss();
                                break;
                            case 6:
                                viewModel.getCaseFinishClicked().setValue(caseInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                            case 8:
                                viewModel.getChangeCaseTypeClicked().setValue(caseInfoList.get(position));
                                powerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
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
            binding.txtCaseID.setText(String.valueOf(caseInfo.getCaseID()));
            binding.txtDescription.setText(caseInfo.getDescription());
            binding.txtCustomerName.setText(caseInfo.getCustomerName());
            binding.txtUserFullName.setText(Converter.letterConverter(caseInfo.getUserFullName()));
            binding.txtAddTime.setText(formatDate(caseInfo.getAddTime()));

        }

        private String formatDate(long addTime) {
            String date = String.valueOf(addTime).substring(0, 8);
            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6);
            return year + "/" + month + "/" + day;
        }
    }
}
