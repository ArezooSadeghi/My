package com.example.sipsupporterapp.view.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.ScaleBitmap;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AttachmentDialogFragment extends DialogFragment implements View.OnClickListener {
    private FragmentAttachmentDialogBinding binding;
    private AttachmentViewModel viewModel;

    private ServerData serverData;
    private String centerName, userLoginKey;

    private int customerSupportID, customerProductID, customerPaymentID, paymentID, numberOfRotate;
    private Uri photoUri;
    private Bitmap bitmap;

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

        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());

        customerSupportID = getArguments().getInt(ARGS_CUSTOMER_SUPPORT_ID);
        customerProductID = getArguments().getInt(ARGS_CUSTOMER_PRODUCT_ID);
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        paymentID = getArguments().getInt(ARGS_PAYMENT_ID);

        createViewModel();

        serverData = viewModel.getServerData(centerName);

        setupObserver();
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
                .Builder(getContext())
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
                    if (photoUri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);

                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                Matrix matrixOne = new Matrix();
                                matrixOne.postRotate(-90);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixOne, true);
                                Glide.with(getContext()).load(bitmap).into(binding.img);
                            } else {
                                Glide.with(getContext()).load(bitmap).into(binding.img);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        } finally {
                            getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                    }
                    break;
                case REQUEST_CODE_PICK_PHOTO:
                    photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                            Glide.with(getContext()).load(bitmap).into(binding.img);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }


    private void handleEvents() {
        binding.imgMore.setOnClickListener(this);
        binding.imgRotate.setOnClickListener(this);
        binding.imgCamera.setOnClickListener(this);
        binding.imgSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_more:
                Intent starter = new Intent();
                starter.setType("image/*");
                starter.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(starter, getString(R.string.select_photo_title)), REQUEST_CODE_PICK_PHOTO);
                break;
            case R.id.img_rotate:
                if (photoUri == null) {
                    handleError(getString(R.string.no_choose_any_file));
                } else {
                    switch (numberOfRotate) {
                        case 0:
                            Matrix matrixOne = new Matrix();
                            matrixOne.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixOne, true);
                            Glide.with(getContext()).load(bitmap).into(binding.img);
                            numberOfRotate++;
                            break;
                        case 1:
                            Matrix matrixTwo = new Matrix();
                            matrixTwo.postRotate(180);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixTwo, true);
                            Glide.with(getContext()).load(bitmap).into(binding.img);
                            numberOfRotate++;
                            break;
                        case 2:
                            Matrix matrixThree = new Matrix();
                            matrixThree.postRotate(270);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixThree, true);
                            Glide.with(getContext()).load(bitmap).into(binding.img);
                            numberOfRotate = 0;
                            break;
                    }
                }
                break;
            case R.id.img_camera:
                if (hasCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
                break;
            case R.id.img_send:
                if (photoUri == null) {
                    handleError(getString(R.string.no_choose_any_file));
                } else {
                    binding.progressBarLoading.setVisibility(View.VISIBLE);
                    binding.imgMore.setEnabled(false);
                    binding.imgRotate.setEnabled(false);
                    binding.imgCamera.setEnabled(false);
                    binding.imgSend.setEnabled(false);
                    binding.imgClose.setEnabled(false);
                    binding.edTextDescription.setEnabled(false);

                    String base64 = convertBitmapToBase64();

                    AttachResult.AttachInfo attachInfo = new AttachResult().new AttachInfo();

                    attachInfo.setCustomerSupportID(customerSupportID);
                    attachInfo.setCustomerProductID(customerProductID);
                    attachInfo.setCustomerPaymentID(customerPaymentID);
                    attachInfo.setPaymentID(paymentID);
                    attachInfo.setFileData(base64);

                    File file = new File(photoUri.getPath());
                    String fileName = file.getName();
                    attachInfo.setFileName(fileName);

                    attachInfo.setDescription(binding.edTextDescription.getText().toString());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            addAttachment(attachInfo);
                        }
                    }).start();
                }
                break;
            case R.id.img_close:
                dismiss();
                break;
        }
    }

    private void addAttachment(AttachResult.AttachInfo attachInfo) {
        viewModel.getSipSupporterServiceForAddAttachment(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/Add/";
        viewModel.addAttachment(path, userLoginKey, attachInfo);
    }

    private String convertBitmapToBase64() {
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

    private void handleError(String message) {
        binding.progressBarLoading.setVisibility(View.GONE);
        binding.imgMore.setEnabled(true);
        binding.imgRotate.setEnabled(true);
        binding.imgCamera.setEnabled(true);
        binding.imgSend.setEnabled(true);
        binding.imgClose.setEnabled(true);
        binding.edTextDescription.setEnabled(true);

        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private boolean hasCameraPermission() {
        return (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission() {
        viewModel.getRequestCameraPermission().setValue(true);
    }

    private void openCamera() {
        Intent starterCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (starterCamera.resolveActivity(getActivity().getPackageManager()) != null) {
            File dir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Attachments");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String name = "img_" + new Date().getTime() + ".jpg";
            File photoFile = new File(dir, name);
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(starterCamera, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : activities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                starterCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(starterCamera, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    private void setupObserver() {
        viewModel.getAllowCameraPermissionSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean allowCameraPermission) {
                openCamera();
            }
        });

        viewModel.getAttachResultSingleLiveEvent().observe(this, new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (attachResult.getErrorCode().equals("0")) {
                    viewModel.getUpdatePhotoGallerySingleLiveEvent().setValue(attachResult);

                    binding.progressBarLoading.setVisibility(View.INVISIBLE);
                    binding.imgClose.setEnabled(true);
                    binding.imgSend.setEnabled(true);
                    binding.imgCamera.setEnabled(true);
                    binding.edTextDescription.setEnabled(true);
                    binding.imgRotate.setEnabled(true);
                    binding.imgMore.setEnabled(true);

                    SuccessAttachDialogFragment fragment = SuccessAttachDialogFragment.newInstance(getString(R.string.success_attach_message));
                    fragment.show(getParentFragmentManager(), SuccessAttachDialogFragment.TAG);
                } else if (attachResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(attachResult.getError());
                }
            }
        });

        viewModel.getHideLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean hideLoading) {
                binding.progressBarLoading.setVisibility(View.INVISIBLE);
                binding.imgClose.setEnabled(true);
                binding.imgSend.setEnabled(true);
                binding.imgCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.imgRotate.setEnabled(true);
                binding.imgMore.setEnabled(true);
            }
        });
        viewModel.getShowAttachAgainDialog().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean showAttachAgainDialog) {
                AttachAgainDialogFragment fragment = AttachAgainDialogFragment.newInstance(getString(R.string.question_attach_again));
                fragment.show(getParentFragmentManager(), AttachAgainDialogFragment.TAG);
            }
        });

        viewModel.getNoAttachAgain().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean noAttachAgain) {
                dismiss();
            }
        });

        viewModel.getYesAttachAgain().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesAttachAgain) {
                photoUri = null;
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
                System.gc();
                binding.img.setImageResource(0);
                binding.edTextDescription.setText("");
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