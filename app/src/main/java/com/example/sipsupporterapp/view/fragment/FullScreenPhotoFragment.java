package com.example.sipsupporterapp.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentFullScreenPhotoBinding;
import com.example.sipsupporterapp.eventbus.DeleteEvent;
import com.example.sipsupporterapp.eventbus.YesDeleteEvent;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.activity.LoginContainerActivity;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDeletePhotoDialogFragment;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class FullScreenPhotoFragment extends Fragment {
    private FragmentFullScreenPhotoBinding binding;
    private AttachmentViewModel viewModel;
    private ServerData serverData;
    private String centerName, userLoginKey;

    private static final String ARGS_FILE_PATH = "filePath";
    private static final String ARGS_ATTACH_ID = "attachID";

    public static FullScreenPhotoFragment newInstance(String filePath, int attachID) {
        FullScreenPhotoFragment fragment = new FullScreenPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_FILE_PATH, filePath);
        args.putInt(ARGS_ATTACH_ID, attachID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createViewModel();
        initVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_full_screen_photo,
                container,
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

    @Subscribe
    public void getDeleteEvent(YesDeleteEvent event) {
        if (binding.progressBarLoading.getVisibility() == View.INVISIBLE) {
            binding.progressBarLoading.setVisibility(View.VISIBLE);
        }
        deleteAttachment();
    }

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initVariables() {
        centerName = SipSupportSharedPreferences.getCenterName(getContext());
        userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        serverData = viewModel.getServerData(centerName);
    }

    private void initViews() {
        String filePath = getArguments().getString(ARGS_FILE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap != null) {
            binding.imgViewFullScreen.setImage(ImageSource.bitmap(bitmap));
        }
    }

    private void showSuccessDialog(String message) {
        SuccessDeletePhotoDialogFragment fragment = SuccessDeletePhotoDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), SuccessDeletePhotoDialogFragment.TAG);
    }

    private void handleError(String message) {
        if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
            binding.progressBarLoading.setVisibility(View.INVISIBLE);
        }
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
        Intent starter = LoginContainerActivity.start(getContext());
        startActivity(starter);
        getActivity().finish();
    }

    private void deleteAttachment() {
        viewModel.getSipSupporterServiceAttachResult(serverData.getIpAddress() + ":" + serverData.getPort());
        int attachID = getArguments().getInt(ARGS_ATTACH_ID);
        String path = "/api/v1/attach/Delete/";
        viewModel.deleteAttachment(path, userLoginKey, attachID);
    }

    private void handleEvents() {
        binding.imgViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionDialogFragment fragment = QuestionDialogFragment.newInstance(getString(R.string.question_delete_photo_message));
                fragment.show(getParentFragmentManager(), QuestionDialogFragment.TAG);
            }
        });
    }

    private void setupObserver() {
        viewModel.getDeleteAttachResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
                    binding.progressBarLoading.setVisibility(View.INVISIBLE);
                }

                if (attachResult.getErrorCode().equals("0")) {
                    if (attachResult.getAttachs().length != 0) {
                        int attachID = attachResult.getAttachs()[0].getAttachID();
                        EventBus.getDefault().postSticky(new DeleteEvent(attachID));
                        showSuccessDialog(getString(R.string.success_delete_photo_message));
                    }
                } else if (attachResult.getErrorCode().equals("-9001")) {
                    ejectUser();
                } else {
                    handleError(attachResult.getError());
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
}