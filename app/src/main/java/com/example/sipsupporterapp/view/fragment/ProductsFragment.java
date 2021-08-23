package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.DirectoryNodeBinder;
import com.example.sipsupporterapp.adapter.FileNodeBinder;
import com.example.sipsupporterapp.databinding.FragmentProductsBinding;
import com.example.sipsupporterapp.eventbus.selectProductEvent;
import com.example.sipsupporterapp.model.Dir;
import com.example.sipsupporterapp.model.ProductGroupResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.ProductsViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

public class ProductsFragment extends Fragment {
    private FragmentProductsBinding binding;
    private ProductsViewModel viewModel;
    private boolean flag;

    private List<TreeNode> treeNodeList;
    private List<ProductGroupResult.ProductGroupInfo> productGroupInfoList;
    private List<ProductResult.ProductInfo> productInfoList;
    private TreeViewAdapter adapter;
    private ServerData serverData;
    private String userLoginKey, centerName;

    private static final String ARGS_FLAG = "flag";

    public static ProductsFragment newInstance(boolean flag) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_FLAG, flag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flag = getArguments().getBoolean(ARGS_FLAG);

        createViewModel();
        initVariables();
        fetchProductGroups();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_products,
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

    private void initViews() {
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initVariables() {
        treeNodeList = new ArrayList<>();
        productGroupInfoList = new ArrayList<>();
        productInfoList = new ArrayList<>();

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerDate(centerName);
        viewModel.getSipSupporterServiceProductGroupResult(serverData.getIpAddress() + ":" + serverData.getPort());
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
    }

    private void fetchProductGroups() {
        String path = "/api/v1/productGroup/ListWithProduct/";
        viewModel.fetchProductGroups(path, userLoginKey);
    }

    private void setupObserver() {
        viewModel.getProductGroupsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<ProductGroupResult>() {
            @Override
            public void onChanged(ProductGroupResult productGroupResult) {
                if (productGroupResult.getErrorCode().equals("0")) {
                    productGroupInfoList = Arrays.asList(productGroupResult.getProductGroups());
                    for (int i = 0; i < productGroupResult.getProductGroups().length; i++) {
                        ProductGroupResult.ProductGroupInfo productGroupInfo = productGroupResult.getProductGroups()[i];
                        if (productGroupInfo.getParentID() == 0) {
                            String productGroup = Converter.letterConverter(productGroupInfo.getProductGroup());
                            TreeNode<Dir> dirNode = new TreeNode<>(new Dir(productGroup));
                            treeNodeList.add(dirNode);
                            for (int j = 0; j < productGroupInfo.getProducts().length; j++) {
                                productInfoList.add(productGroupInfo.getProducts()[j]);
                                String productName = Converter.letterConverter(productGroupInfo.getProducts()[j].getProductName());
                                TreeNode<Dir> dirTreeNode = new TreeNode<>(new Dir(productName));
                                dirNode.addChild(dirTreeNode);
                            }
                            addChild(dirNode, productGroupInfo.getProductGroupID());
                        }
                    }
                    setupAdapter();
                    handleEvents();
                } else if (productGroupResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(productGroupResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
            }
        });
    }

    private void handleError(String message) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void ejectUser() {
        SipSupportSharedPreferences.setUserFullName(getContext(), null);
        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
        SipSupportSharedPreferences.setCenterName(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
    }

    private void addChild(TreeNode treeNode, int productGroupID) {
        for (int i = 0; i < productGroupInfoList.size(); i++) {
            if (productGroupInfoList.get(i).getParentID() == productGroupID) {
                ProductGroupResult.ProductGroupInfo productGroupInfo = productGroupInfoList.get(i);
                String paymentSubject = Converter.letterConverter(productGroupInfo.getProductGroup());
                TreeNode<Dir> dirNode = new TreeNode<>(new Dir(paymentSubject));
                treeNode.addChild(dirNode);
                for (int j = 0; j < productGroupInfo.getProducts().length; j++) {
                    productInfoList.add(productGroupInfo.getProducts()[j]);
                    String productName = Converter.letterConverter(productGroupInfo.getProducts()[j].getProductName());
                    TreeNode<Dir> dirTreeNode = new TreeNode<>(new Dir(productName));
                    dirNode.addChild(dirTreeNode);
                }
                addChild(dirNode, productGroupInfo.getProductGroupID());
            }
        }
    }

    private void setupAdapter() {
        adapter = new TreeViewAdapter(treeNodeList, Arrays.asList(new FileNodeBinder(), new DirectoryNodeBinder()));
        binding.recyclerViewProducts.setAdapter(adapter);
    }

    private void handleEvents() {
        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                if (!node.isLeaf()) {
                    onToggle(!node.isExpand(), holder);
                } else {
                    Dir dirNode = (Dir) node.getContent();
                    String productGroup = dirNode.getDirName();
                    int productGroupID = getProductGroupID(productGroup);
                    selectProductEvent event = new selectProductEvent(productGroupID, productGroup);
                    EventBus.getDefault().postSticky(event);
                    if (!flag) {
                        getActivity().finish();
                    } else {
                        NavHostFragment.findNavController(ProductsFragment.this).navigate(R.id.invoiceFragment);
                    }
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

    private int getProductGroupID(String productGroup) {
        for (int i = 0; i < productGroupInfoList.size(); i++) {
            String payment_Subject = Converter.letterConverter(productGroupInfoList.get(i).getProductGroup());
            if (payment_Subject.equals(productGroup)) {
                return productGroupInfoList.get(i).getProductGroupID();
            }
        }
        for (int i = 0; i < productInfoList.size(); i++) {
            String productName = Converter.letterConverter(productInfoList.get(i).getProductName());
            if (productName.equals(productGroup)) {
                return productInfoList.get(i).getProductID();
            }
        }
        return 0;
    }
}