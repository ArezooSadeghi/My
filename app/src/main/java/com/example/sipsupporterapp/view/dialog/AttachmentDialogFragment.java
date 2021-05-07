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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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
import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AttachmentDialogFragment extends DialogFragment {
    private FragmentAttachmentDialogBinding binding;
    private AttachmentViewModel viewModel;

    private File photoFile;
    private Uri photoUri;
    private Bitmap photoBitmap;
    private int numberOfRotate;
    private BitmapFactory.Options options;

    private int customerID, customerSupportID, customerProductID, customerPaymentID, paymentID;

    private static final int REQUEST_CODE_PICK_PHOTO = 0;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;

    private static final String ARGS_CUSTOMER_ID = "customerID";
    private static final String ARGS_CUSTOMER_SUPPORT_ID = "customerSupportID";
    private static final String ARGS_CUSTOMER_PRODUCT_ID = "customerProductID";
    private static final String ARGS_CUSTOMER_PAYMENT_ID = "customerPaymentID";
    private static final String ARGS_PAYMENT_ID = "paymentID";

    private static final String AUTHORITY = "com.example.sipsupporterapp.fileProvider";

    public static final String TAG = AttachmentDialogFragment.class.getSimpleName();

    public static AttachmentDialogFragment newInstance(int customerID, int customerSupportID, int customerProductID, int customerPaymentID, int paymentID) {
        AttachmentDialogFragment fragment = new AttachmentDialogFragment();
        Bundle args = new Bundle();

        args.putInt(ARGS_CUSTOMER_ID, customerID);
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

        customerID = getArguments().getInt(ARGS_CUSTOMER_ID);
        customerSupportID = getArguments().getInt(ARGS_CUSTOMER_SUPPORT_ID);
        customerProductID = getArguments().getInt(ARGS_CUSTOMER_PRODUCT_ID);
        customerPaymentID = getArguments().getInt(ARGS_CUSTOMER_PAYMENT_ID);
        paymentID = getArguments().getInt(ARGS_PAYMENT_ID);

        createViewModel();
        setupObserver();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_PHOTO) {
                photoUri = intent.getData();
                if (photoUri != null) {
                    try {
                        if (photoBitmap != null) {
                            photoBitmap.recycle();
                            photoBitmap = null;
                        }
                        System.gc();
                        photoBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                        if (photoBitmap != null) {
                            Glide.with(getContext()).load(photoBitmap).into(binding.img);
                        }
                    } catch (IOException exception) {
                        Log.e(TAG, exception.getMessage());
                    }
                }
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                try {
                    photoUri = FileProvider.getUriForFile(
                            getActivity(), AUTHORITY, photoFile);

                    if (photoUri != null) {
                        try {
                            if (photoBitmap != null) {
                                photoBitmap.recycle();
                                photoBitmap = null;
                            }
                            System.gc();
                            photoBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                            if (photoBitmap != null) {

                                options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        viewModel.getDecodeBitmap().postValue(BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options));
                                    }
                                }).start();
                            }
                        } catch (IOException exception) {
                            Log.e(TAG, exception.getMessage());
                        }
                    }
                } catch (Exception exception) {
                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    getActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void setupObserver() {
        viewModel.getAllowCameraPermissionSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAllowPermission) {
                openCamera();
            }
        });

        viewModel.getAttachResultSingleLiveEvent().observe(this, new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                viewModel.getUpdateImageListSingleLiveEvent().setValue(attachResult);

                binding.progressBarLoading.setVisibility(View.GONE);
                binding.imgClose.setEnabled(true);
                binding.imgSend.setEnabled(true);
                binding.imgCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.imgRotate.setEnabled(true);
                binding.imgMore.setEnabled(true);

                SuccessAttachDialogFragment fragment = SuccessAttachDialogFragment.newInstance(getString(R.string.success_attach_message));
                fragment.show(getParentFragmentManager(), SuccessAttachDialogFragment.TAG);
            }
        });

        viewModel.getErrorAttachResultSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.imgClose.setEnabled(true);
                binding.imgSend.setEnabled(true);
                binding.imgCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.imgRotate.setEnabled(true);
                binding.imgMore.setEnabled(true);
                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(error);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getNoConnectionSingleLiveEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.imgClose.setEnabled(true);
                binding.imgSend.setEnabled(true);
                binding.imgCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.imgRotate.setEnabled(true);
                binding.imgMore.setEnabled(true);

                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getTimeOutExceptionHappenSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isTimeOutExceptionHappen) {
                binding.progressBarLoading.setVisibility(View.GONE);
                binding.imgClose.setEnabled(true);
                binding.imgSend.setEnabled(true);
                binding.imgCamera.setEnabled(true);
                binding.edTextDescription.setEnabled(true);
                binding.imgRotate.setEnabled(true);
                binding.imgMore.setEnabled(true);

                ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.timeout_exception_happen_message));
                fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
            }
        });

        viewModel.getDangerousUserSingleLiveEvent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDangerousUser) {
                SipSupportSharedPreferences.setUserLoginKey(getContext(), null);
                SipSupportSharedPreferences.setUserFullName(getContext(), null);
                SipSupportSharedPreferences.setCustomerUserId(getContext(), 0);
                SipSupportSharedPreferences.setCustomerName(getContext(), null);
                SipSupportSharedPreferences.setCustomerTel(getContext(), null);
                SipSupportSharedPreferences.setLastSearchQuery(getContext(), null);
                Intent intent = LoginContainerActivity.start(getContext());
                startActivity(intent);
                getActivity().finish();
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
            public void onChanged(Boolean isNoAgain) {
                dismiss();
            }
        });

        viewModel.getYesAgain().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isYesAgain) {
                photoUri = null;
                if (photoBitmap != null) {
                    photoBitmap.recycle();
                    photoBitmap = null;
                }
                System.gc();
                binding.img.setImageResource(0);
                binding.edTextDescription.setText("");
            }
        });

        viewModel.getDecodeBitmap().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                options.inJustDecodeBounds = false;

                if (imageWidth > imageHeight) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, photoBitmap.getWidth(), photoBitmap.getHeight(), matrix, true);
                    Glide.with(getContext()).load(photoBitmap).into(binding.img);
                } else {
                    Glide.with(getContext()).load(photoBitmap).into(binding.img);
                }
            }
        });
    }

    private void handleEvents() {
        binding.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasCameraPermission()) {
                    openCamera();
                } else {
                    viewModel.getRequestCameraPermission().setValue(true);
                }
            }
        });

        binding.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent starter = new Intent();
                starter.setType("image/*");
                starter.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(starter, getString(R.string.select_photo_title)), REQUEST_CODE_PICK_PHOTO);
            }
        });

        binding.imgRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoUri == null) {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.no_choose_any_file));
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else {
                    switch (numberOfRotate) {
                        case 0:
                            Matrix matrixOne = new Matrix();
                            matrixOne.postRotate(90);
                            photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, photoBitmap.getWidth(), photoBitmap.getHeight(), matrixOne, true);
                            Glide.with(getContext()).load(photoBitmap).into(binding.img);
                            numberOfRotate++;
                            break;
                        case 1:
                            Matrix matrixTwo = new Matrix();
                            matrixTwo.postRotate(180);
                            photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, photoBitmap.getWidth(), photoBitmap.getHeight(), matrixTwo, true);
                            Glide.with(getContext()).load(photoBitmap).into(binding.img);
                            numberOfRotate++;
                            break;
                        case 2:
                            Matrix matrixThree = new Matrix();
                            matrixThree.postRotate(270);
                            photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, photoBitmap.getWidth(), photoBitmap.getHeight(), matrixThree, true);
                            Glide.with(getContext()).load(photoBitmap).into(binding.img);
                            numberOfRotate = 0;
                            break;
                    }
                }
            }
        });

        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoUri == null) {
                    ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.no_choose_any_file));
                    fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
                } else {
                    AttachmentAsyncTask attachmentAsyncTask = new AttachmentAsyncTask();
                    attachmentAsyncTask.execute(photoBitmap);
                }
            }
        });
    }

    private boolean hasCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void openCamera() {
        Intent starter = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (starter.resolveActivity(getActivity().getPackageManager()) != null) {
            Log.d("Arezoo", "hi dear");
            File filesDir = getActivity().getFilesDir();
            String fileName = "img_" + new Date().getTime() + ".jpg";
            photoFile = new File(filesDir, fileName);

            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(getContext(),
                        AUTHORITY,
                        photoFile);

                List<ResolveInfo> activities =
                        getActivity().getPackageManager()
                                .queryIntentActivities(
                                        starter,
                                        PackageManager.MATCH_DEFAULT_ONLY
                                );

                for (ResolveInfo activity : activities) {
                    getActivity().grantUriPermission(
                            activity.activityInfo.packageName,
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                }

                starter.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(starter, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    private String convertBitmapToStringBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        System.gc();

        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    public class AttachmentAsyncTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected void onPreExecute() {
            binding.progressBarLoading.setVisibility(View.VISIBLE);
            binding.imgClose.setEnabled(false);
            binding.imgSend.setEnabled(false);
            binding.imgCamera.setEnabled(false);
            binding.edTextDescription.setEnabled(false);
            binding.imgRotate.setEnabled(false);
            binding.imgMore.setEnabled(false);
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            String stringBase64 = convertBitmapToStringBase64(photoBitmap);
            return stringBase64;
        }

        @Override
        protected void onPostExecute(String stringBase64) {
            AttachInfo attachInfo = new AttachInfo();

            attachInfo.setCustomerID(customerID);
            attachInfo.setCustomerSupportID(customerSupportID);
            attachInfo.setCustomerProductID(customerProductID);
            attachInfo.setCustomerPaymentID(customerPaymentID);
            attachInfo.setPaymentID(paymentID);

            attachInfo.setFileData(stringBase64);

            File file = new File(photoUri.getPath());
            String fileName = file.getName();
            attachInfo.setFileName(fileName);

            attachInfo.setDescription(binding.edTextDescription.getText().toString());

            attach(attachInfo);
        }
    }

    private void attach(AttachInfo attachInfo) {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupportServiceAttach(serverData.getIpAddress() + ":" + serverData.getPort());
        viewModel.attach(userLoginKey, attachInfo);
    }
}
