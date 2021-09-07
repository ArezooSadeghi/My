package com.example.sipsupporterapp.view.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentAttachmentDialogBinding;
import com.example.sipsupporterapp.eventbus.SuccessEvent;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.ScaleBitmap;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AttachmentDialogFragment extends DialogFragment {
    private FragmentAttachmentDialogBinding binding;
    private AttachmentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;
    private Uri photoUri;
    private File photoFile;
    private Bitmap bitmap;
    private Matrix matrix;
    private int customerSupportID, customerProductID, customerPaymentID, paymentID, numberOfRotate;

    private static final int REQUEST_CODE_TAKE_PHOTO = 0;
    private static final int REQUEST_CODE_PICK_PHOTO = 1;

    private static final String ARGS_CUSTOMER_SUPPORT_ID = "customerSupportID";
    private static final String ARGS_CUSTOMER_PRODUCT_ID = "customerProductID";
    private static final String ARGS_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String ARGS_PAYMENT_ID = "paymentID";
    private static final String PHOTO_URI = "photoUri";
    private static final String AUTHORITY = "com.example.sipsupporterapp.fileProvider";

    public static final String TAG = AttachmentDialogFragment.class.getSimpleName();

    public static AttachmentDialogFragment newInstance(int customerSupportID, int customerProductID, int customerPaymentID, int paymentID) {
        AttachmentDialogFragment fragment = new AttachmentDialogFragment();
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

        if (savedInstanceState != null) {
            photoUri = savedInstanceState.getParcelable(PHOTO_URI);
        }

        createViewModel();
        setupObserver();
        initVariables();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(
                getContext()),
                R.layout.fragment_attachment_dialog,
                null,
                false);

        handleEvents();

        AlertDialog dialog = new AlertDialog
                .Builder(getContext(), R.style.CustomAlertDialog)
                .setView(binding.getRoot())
                .create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PHOTO_URI, photoUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PHOTO:
                    if (photoFile.length() != 0) {
                        try {
                            photoUri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);

                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                matrix = new Matrix();
                                matrix.postRotate(90);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            }

                            binding.ivEmptyGallery.setVisibility(View.GONE);
                            binding.ivPhoto.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    break;
                case REQUEST_CODE_PICK_PHOTO:
                    photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                            binding.ivEmptyGallery.setVisibility(View.GONE);
                            binding.ivPhoto.setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Subscribe
    public void getSuccessEvent(SuccessEvent event) {
        AttachAgainDialogFragment fragment = AttachAgainDialogFragment.newInstance("آیا می خواهید فایل دیگری را اضافه کنید؟");
        fragment.show(getParentFragmentManager(), AttachAgainDialogFragment.TAG);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void setupObserver() {
        viewModel.getAllowPermission().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean allowCameraPermission) {
                openCamera();
            }
        });

        viewModel.getAttachResultSingleLiveEvent().observe(this, new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult != null) {
                    if (attachResult.getErrorCode().equals("0")) {
                        viewModel.getRefresh().setValue(attachResult);
                        binding.progressBarLoading.setVisibility(View.GONE);
                        binding.ivSend.setEnabled(true);
                        binding.fabRotate.setEnabled(true);
                        binding.fabChoseFromFile.setEnabled(true);
                        binding.fabCamera.setEnabled(true);
                        showSuccessDialog("فایل با موفقیت ثبت شد");
                    } else if (attachResult.getErrorCode().equals("-9001")) {
                        ejectUser();
                    } else {
                        handleError(attachResult.getError());
                    }
                }
            }
        });

        viewModel.getYesAttachAgainClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesAttachAgain) {
                photoUri = null;
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                System.gc();
                binding.ivPhoto.setImageResource(0);
                binding.ivPhoto.setVisibility(View.GONE);
                binding.ivEmptyGallery.setVisibility(View.VISIBLE);
                binding.edTxtDescription.setText("");
            }
        });

        viewModel.getNoAttachAgainClicked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean noAttachAgain) {
                dismiss();
            }
        });

        viewModel.getHideLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean hideLoading) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.ivSend.setEnabled(true);
                binding.fabRotate.setEnabled(true);
                binding.fabChoseFromFile.setEnabled(true);
                binding.fabCamera.setEnabled(true);
            }
        });
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
        customerSupportID = getArguments().getInt(ARGS_CUSTOMER_SUPPORT_ID);
        customerProductID = getArguments().getInt(ARGS_CUSTOMER_PRODUCT_ID);
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        paymentID = getArguments().getInt(ARGS_PAYMENT_ID);
        matrix = new Matrix();
    }

    private void handleEvents() {
        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });

        binding.fabChoseFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent starter = new Intent();
                starter.setType("image/*");
                starter.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(starter, "انتخاب تصویر"), REQUEST_CODE_PICK_PHOTO);
            }
        });

        binding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoUri != null) {
                    binding.progressBarLoading.setVisibility(View.VISIBLE);
                    binding.ivSend.setEnabled(false);
                    binding.fabRotate.setEnabled(false);
                    binding.fabChoseFromFile.setEnabled(false);
                    binding.fabCamera.setEnabled(false);

                    AttachResult.AttachInfo attachInfo = new AttachResult().new AttachInfo();
                    String fileData = convertBitmapToBase64(bitmap);
                    attachInfo.setFileData(fileData);
                    File file = new File(photoUri.getPath());
                    String fileName = file.getName();
                    attachInfo.setFileName(fileName);
                    String description = binding.edTxtDescription.getText().toString();
                    attachInfo.setDescription(description);
                    attachInfo.setCustomerSupportID(customerSupportID);
                    attachInfo.setCustomerProductID(customerProductID);
                    attachInfo.setCustomerPaymentID(customerPaymentID);
                    attachInfo.setPaymentID(paymentID);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            attach(attachInfo);
                        }
                    }).start();
                } else {
                    handleError("هنوز فایلی انتخاب نشده است");
                }
            }
        });

        binding.fabRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoUri != null) {
                    switch (numberOfRotate) {
                        case 0:
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                            numberOfRotate++;
                            break;
                        case 1:
                            matrix.postRotate(180);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                            numberOfRotate++;
                            break;
                        case 2:
                            matrix.postRotate(270);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            Glide.with(getContext()).load(bitmap).into(binding.ivPhoto);
                            numberOfRotate = 0;
                            break;
                    }

                } else {
                    handleError("هنوز فایلی انتخاب نشده است");
                }
            }
        });
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        viewModel.getRequestPermission().setValue(true);
    }

    private void openCamera() {
        Intent starterCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (starterCamera.resolveActivity(getActivity().getPackageManager()) != null) {
            File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String name = "img_" + new Date().getTime() + ".jpg";
            photoFile = new File(dir, name);
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(starterCamera, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : activities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                starterCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(starterCamera, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    private void handleError(String message) {
        binding.progressBarLoading.setVisibility(View.GONE);
        binding.ivSend.setEnabled(true);
        binding.fabRotate.setEnabled(true);
        binding.fabChoseFromFile.setEnabled(true);
        binding.fabCamera.setEnabled(true);
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void showSuccessDialog(String message) {
        SuccessDialogFragment fragment = SuccessDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDialogFragment.TAG);
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
        SipSupportSharedPreferences.setCaseID(getContext(), 0);
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void attach(AttachResult.AttachInfo attachInfo) {
        viewModel.getSipSupporterServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/Add/";
        viewModel.attach(path, userLoginKey, attachInfo);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        if (SipSupportSharedPreferences.getFactor(getContext()) == null) {
            Bitmap scaledBitmap = ScaleBitmap.getScaledDownBitmap(bitmap, 2245);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            System.gc();
            return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        } else {
            String factor = SipSupportSharedPreferences.getFactor(getContext());
            Bitmap scaledBitmap = ScaleBitmap.getScaledDownBitmap(bitmap, Math.round(Double.valueOf(factor) * 2245));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            System.gc();
            return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
        }
    }
}