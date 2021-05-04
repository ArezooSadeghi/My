package com.example.sipsupporterapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.DirectoryNodeBinder;
import com.example.sipsupporterapp.adapter.FileNodeBinder;
import com.example.sipsupporterapp.databinding.FragmentPaymentSubjectBinding;
import com.example.sipsupporterapp.eventbus.PostSelectedPaymentSubjectEvent;
import com.example.sipsupporterapp.model.Dir;
import com.example.sipsupporterapp.model.PaymentSubjectInfo;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.PaymentSubjectViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

public class PaymentSubjectFragment extends Fragment {
    private FragmentPaymentSubjectBinding binding;
    private PaymentSubjectViewModel viewModel;

    private List<TreeNode> treeNodeList = new ArrayList<>();
    private List<PaymentSubjectInfo> paymentSubjectInfoList = new ArrayList<>();
    private TreeViewAdapter adapter;

    public static PaymentSubjectFragment newInstance() {
        PaymentSubjectFragment fragment = new PaymentSubjectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        fetchPaymentSubjects();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_payment_subject,
                container,
                false);

        initViews();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(PaymentSubjectViewModel.class);
    }

    private void fetchPaymentSubjects() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServicePaymentSubjects(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.fetchPaymentSubjects(userLoginKey);
    }

    private void initViews() {
        binding.recyclerViewSubjectPayment.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupObserver() {
        viewModel.getPaymentSubjectsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<PaymentSubjectResult>() {
            @Override
            public void onChanged(PaymentSubjectResult paymentSubjectResult) {
                paymentSubjectInfoList = Arrays.asList(paymentSubjectResult.getPaymentSubjects());
                for (int i = 0; i < paymentSubjectResult.getPaymentSubjects().length; i++) {
                    PaymentSubjectInfo paymentSubjectInfo = paymentSubjectResult.getPaymentSubjects()[i];
                    if (paymentSubjectInfo.getParentID() == 0 || paymentSubjectInfo.getParentPaymentSubject() == null) {
                        String paymentSubject = Converter.convert(paymentSubjectInfo.getPaymentSubject());
                        TreeNode<Dir> dirNode = new TreeNode<>(new Dir(paymentSubject));
                        treeNodeList.add(dirNode);
                        addChild(dirNode, paymentSubjectInfo.getPaymentSubjectID());
                    }
                }

                setupAdapter();
                handleEvents();
            }
        });

        viewModel.getErrorPaymentSubjectsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getNoConnection().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeoutExceptionHappen().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTimeOtuExceptionHappen) {
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.timeout_exception_happen_message));
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });
    }

    private void addChild(TreeNode treeNode, int paymentSubjectID) {
        for (int i = 0; i < paymentSubjectInfoList.size(); i++) {
            if (paymentSubjectInfoList.get(i).getParentID() == paymentSubjectID) {
                PaymentSubjectInfo paymentSubjectInfo = paymentSubjectInfoList.get(i);
                String paymentSubject = Converter.convert(paymentSubjectInfo.getPaymentSubject());
                TreeNode<Dir> dirNode = new TreeNode<>(new Dir(paymentSubject));
                treeNode.addChild(dirNode);
                addChild(dirNode, paymentSubjectInfo.getPaymentSubjectID());
            }
        }
    }

    private void setupAdapter() {
        adapter = new TreeViewAdapter(treeNodeList, Arrays.asList(new FileNodeBinder(), new DirectoryNodeBinder()));
        binding.recyclerViewSubjectPayment.setAdapter(adapter);
    }

    private void handleEvents() {
        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                if (!node.isLeaf()) {
                    onToggle(!node.isExpand(), holder);
                } else {
                    Dir dirNode = (Dir) node.getContent();
                    String paymentSubject = dirNode.getDirName();
                    int paymentSubjectID = getPaymentSubjectID(paymentSubject);
                    PostSelectedPaymentSubjectEvent event = new PostSelectedPaymentSubjectEvent(paymentSubject, paymentSubjectID);
                    EventBus.getDefault().postSticky(event);
                    getActivity().finish();
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                DirectoryNodeBinder.DirectoryNodeHolder directoryNodeHolder = (DirectoryNodeBinder.DirectoryNodeHolder) holder;
                final ImageView ivArrow = directoryNodeHolder.getIvArrow();
                int rotateDegree = isExpand ? -90 : 90;
                ivArrow.animate().rotationBy(rotateDegree)
                        .start();
            }
        });
    }

    private int getPaymentSubjectID(String paymentSubject) {
        for (int i = 0; i < paymentSubjectInfoList.size(); i++) {
            String payment_Subject = Converter.convert(paymentSubjectInfoList.get(i).getPaymentSubject());
            if (payment_Subject.equals(paymentSubject)) {
                return paymentSubjectInfoList.get(i).getPaymentSubjectID();
            }
        }
        return 0;
    }
}