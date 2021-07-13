package com.example.sipsupporterapp.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.adapter.PhotoGalleryAdapter;
import com.example.sipsupporterapp.databinding.FragmentPhotoGalleryBinding;
import com.example.sipsupporterapp.eventbus.DeleteEvent;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.FullScreenPhotoContainerActivity;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.AttachmentDialogFragment;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {
    private FragmentPhotoGalleryBinding binding;
    private AttachmentViewModel viewModel;

    private int customerSupportID, customerProductID, customerPaymentID, paymentID, index;
    private ServerData serverData;
    private PhotoGalleryAdapter adapter;
    private List<String> oldFilePathList = new ArrayList<>();
    private List<String> newFilePathList = new ArrayList<>();
    private List<Integer> attachIDList = new ArrayList<>();
    private String userLoginKey = "";

    private static final int SPAN_COUNT_PHONE = 3;
    private static final int SPAN_COUNT_TABLET = 4;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 0;
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;

    private static final String ARGS_CUSTOMER_SUPPORT_ID = "customerSupportID";
    private static final String ARGS_CUSTOMER_PRODUCT_ID = "customerProductID";
    private static final String ARGS_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String ARGS_PAYMENT_ID = "paymentID";

    public static final String TAG = PhotoGalleryFragment.class.getSimpleName();

    public static PhotoGalleryFragment newInstance(int customerSupportID, int customerProductID, int customerPaymentID, int paymentID) {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();

        args.putInt(ARGS_CUSTOMER_SUPPORT_ID, customerSupportID);
        args.putInt(ARGS_CUSTOMER_PRODUCT_ID, customerProductID);
        args.putInt(ARGS_CUSTOMER_PAYMENT_ID, customerPaymentID);
        args.putInt(ARGS_PAYMENT_ID, paymentID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());

        customerSupportID = getArguments().getInt(ARGS_CUSTOMER_SUPPORT_ID);
        customerProductID = getArguments().getInt(ARGS_CUSTOMER_PRODUCT_ID);
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        paymentID = getArguments().getInt(ARGS_PAYMENT_ID);

        createViewModel();

        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        serverData = viewModel.getServerData(centerName);

        if (hasWriteExternalStoragePermission()) {
            if (customerSupportID != 0) {
                fetchCustomerSupportAttachments();
            } else if (customerProductID != 0) {
                fetchCustomerProductAttachments();
            } else if (customerPaymentID != 0) {
                fetchCustomerPaymentAttachments();
            } else if (paymentID != 0) {
                fetchPaymentAttachments();
            }
        } else {
            requestWriteExternalStoragePermission();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.fragment_photo_gallery,
                null,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObserver();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true)
    public void getDeleteEvent(DeleteEvent event) {
        String filePath = "";
        File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.getName().equals(event.getAttachID() + ".jpg")) {
                    filePath = file.getPath();
                    file.delete();
                    break;
                }
            }
        }

        for (String fPath : newFilePathList) {
            if (!filePath.isEmpty()) {
                if (fPath.equals(filePath)) {
                    newFilePathList.remove(fPath);
                    break;
                }
            }
        }

        if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
            binding.progressBarLoading.setVisibility(View.INVISIBLE);
            binding.recyclerViewAttachmentFile.setVisibility(View.VISIBLE);
        }

        setupAdapter();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults == null || grantResults.length == 0) {
                    return;
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (customerSupportID != 0) {
                        fetchCustomerSupportAttachments();
                    } else if (customerProductID != 0) {
                        fetchCustomerProductAttachments();
                    } else if (customerPaymentID != 0) {
                        fetchCustomerPaymentAttachments();
                    } else if (paymentID != 0) {
                        fetchPaymentAttachments();
                    }
                } else {
                    handleError(getString(R.string.no_access_storage_permission_message));
                }
                break;
            case REQUEST_CODE_CAMERA_PERMISSION:
                if (grantResults == null || grantResults.length == 0) {
                    return;
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.getAllowCameraPermissionSingleLiveEvent().setValue(true);
                } else {
                    handleError(getString(R.string.no_access_camera_permission_message));
                }
                break;
        }
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initViews() {
        if (isTablet()) {
            binding.recyclerViewAttachmentFile.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT_TABLET));
        } else {
            binding.recyclerViewAttachmentFile.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT_PHONE));
        }
    }

    private void handleEvents() {
        binding.fabAddNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttachmentDialogFragment fragment = AttachmentDialogFragment.newInstance(customerSupportID, customerProductID, customerPaymentID, paymentID);
                fragment.show(getParentFragmentManager(), AttachmentDialogFragment.TAG);
            }
        });
    }

    private boolean isTablet() {
        boolean xlarge = ((getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    private boolean hasWriteExternalStoragePermission() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestWriteExternalStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
    }

    private void fetchCustomerSupportAttachments() {
        viewModel.getSipSupporterServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/List_ByCustomerSupport/";
        viewModel.fetchCustomerSupportAttachments(path, userLoginKey, customerSupportID, false);
    }

    private void fetchCustomerProductAttachments() {
        viewModel.getSipSupporterServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/List_ByCustomerProduct/";
        viewModel.fetchCustomerProductAttachments(path, userLoginKey, customerProductID, false);
    }

    private void fetchCustomerPaymentAttachments() {
        viewModel.getSipSupporterServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/List_ByCustomerPayment/";
        viewModel.fetchCustomerPaymentAttachments(path, userLoginKey, customerPaymentID, false);
    }

    private void fetchPaymentAttachments() {
        viewModel.getSipSupporterServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/List_ByPayment/";
        viewModel.fetchPaymentAttachments(path, userLoginKey, paymentID, false);
    }

    private void fetchAttachInfo(int attachID) {
        viewModel.getSipSupporterServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/Info/";
        viewModel.fetchAttachInfo(path, userLoginKey, attachID, true);
    }

    private String readFromStorage(int attachID) {
        File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files.length != 0) {
                for (File file : files) {
                    if (file.getName().equals(attachID + ".jpg")) {
                        if (adapter == null) {
                            oldFilePathList.add(file.getPath());
                            newFilePathList.addAll(oldFilePathList);
                        } else {
                            newFilePathList.add(file.getPath());
                        }
                        return file.getPath();
                    }
                }
            }
        }
        return "";
    }

    private String writeToExternalStorage(AttachResult.AttachInfo attachInfo) throws IOException {
        File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, attachInfo.getAttachID() + ".jpg");
        if (attachInfo.getFileData() != null) {
            byte[] bytes = Base64.decode(attachInfo.getFileData(), 0);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(bytes);
            fileOutputStream.flush();
            fileOutputStream.close();
            if (adapter == null) {
                oldFilePathList.add(file.getPath());
                newFilePathList.addAll(oldFilePathList);
            } else {
                newFilePathList.add(file.getPath());
            }
            return file.getPath();
        } else {
            return "";
        }
    }

    private void setupAdapter() {
        if (adapter == null) {
            adapter = new PhotoGalleryAdapter(getContext(), viewModel, oldFilePathList);
        } else {
            adapter.updateFilePathList(newFilePathList);
        }
        binding.recyclerViewAttachmentFile.setAdapter(adapter);
    }

    private void handleError(String message) {
        if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
            binding.progressBarLoading.setVisibility(View.INVISIBLE);
        }
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showAttachments(AttachResult attachResult) {
        if (attachResult.getAttachs().length == 0) {
            if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
                binding.progressBarLoading.setVisibility(View.INVISIBLE);
            }
        } else {
            for (AttachResult.AttachInfo attachInfo : attachResult.getAttachs()) {
                String filePath = readFromStorage(attachInfo.getAttachID());
                if (!filePath.isEmpty()) {
                    if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
                        binding.progressBarLoading.setVisibility(View.INVISIBLE);
                        binding.recyclerViewAttachmentFile.setVisibility(View.VISIBLE);
                    }
                    setupAdapter();
                } else {
                    attachIDList.add(attachInfo.getAttachID());
                }
            }

            if (attachIDList.size() != 0) {
                fetchAttachInfo(attachIDList.get(index));
            }
        }
    }

    private void setupObserver() {
        viewModel.getCustomerSupportAttachmentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult.getErrorCode().equals("0")) {
                    showAttachments(attachResult);
                } else if (attachResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getCustomerProductAttachmentsSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult.getErrorCode().equals("0")) {
                    showAttachments(attachResult);
                } else if (attachResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getCustomerPaymentAttachmentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult.getErrorCode().equals("0")) {
                    showAttachments(attachResult);
                } else if (attachResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getPaymentAttachmentsResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult.getErrorCode().equals("0")) {
                    showAttachments(attachResult);
                } else if (attachResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getAttachInfoResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult.getErrorCode().equals("0")) {
                    if (attachResult.getAttachs().length != 0) {
                        AttachResult.AttachInfo attachInfo = attachResult.getAttachs()[0];
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String filePath = writeToExternalStorage(attachInfo);
                                    viewModel.getFinishWriteToStorage().postValue(filePath);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }).start();
                    }
                } else if (attachResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getFinishWriteToStorage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String filePath) {
                if (!filePath.isEmpty()) {
                    binding.progressBarLoading.setVisibility(View.INVISIBLE);
                    binding.recyclerViewAttachmentFile.setVisibility(View.VISIBLE);
                    setupAdapter();
                }
                index++;
                if (index < attachIDList.size()) {
                    fetchAttachInfo(attachIDList.get(index));
                } else {
                    if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
                        binding.progressBarLoading.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        viewModel.getNoConnectionExceptionSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
                viewModel.getHideLoading().setValue(true);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleError(message);
                viewModel.getHideLoading().setValue(true);
            }
        });

        viewModel.getPhotoClicked().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String filePath) {
                File file = new File(filePath);
                String fileName = file.getName().replace(".jpg", "");
                int attachID = Integer.parseInt(fileName);
                Intent starter = FullScreenPhotoContainerActivity.start(getContext(), filePath, attachID);
                startActivity(starter);
            }
        });

        viewModel.getRequestCameraPermission().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean requestCameraPermission) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_PERMISSION);
            }
        });

        viewModel.getUpdatePhotoGallerySingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult.getAttachs().length != 0) {
                    int attachID = attachResult.getAttachs()[0].getAttachID();
                    fetchAttachInfo(attachID);
                }
            }
        });
    }

    private void ejectUser() {
        SipSupportSharedPreferences.setUserFullName(getContext(), null);
        SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
        SipSupportSharedPreferences.setCenterName(getContext(), null);
        SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
        SipSupportSharedPreferences.setCustomerName(getContext(), null);
        SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
        SipSupportSharedPreferences.setUserName(getContext(), null);
        SipSupportSharedPreferences.setCustomerTel(getContext(), null);
        SipSupportSharedPreferences.setDate(getContext(), null);
        SipSupportSharedPreferences.setFactor(getContext(), null);

        Intent intent = LoginContainerActivity.start(getContext());
        startActivity(intent);
        getActivity().finish();
    }
}