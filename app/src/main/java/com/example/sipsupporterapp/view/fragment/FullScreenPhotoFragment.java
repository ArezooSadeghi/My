package com.example.sipsupporterapp.view.fragment;

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
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.view.dialog.ErrorDialogFragment;
import com.example.sipsupporterapp.view.dialog.QuestionDeletePhotoDialogFragment;
import com.example.sipsupporterapp.view.dialog.SuccessDeletePhotoDialogFragment;
import com.example.sipsupporterapp.viewmodel.AttachmentViewModel;

import org.greenrobot.eventbus.EventBus;

public class FullScreenPhotoFragment extends Fragment {
    private FragmentFullScreenPhotoBinding binding;
    private AttachmentViewModel viewModel;

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

    private void createViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AttachmentViewModel.class);
    }

    private void initViews() {
        String filePath = getArguments().getString(ARGS_FILE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap != null) {
            binding.imgViewFullScreen.setImage(ImageSource.bitmap(bitmap));
        }
    }

    private void handleEvents() {
        binding.imgViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionDeletePhotoDialogFragment fragment = QuestionDeletePhotoDialogFragment.newInstance(getString(R.string.question_delete_photo_message));
                fragment.show(getParentFragmentManager(), QuestionDeletePhotoDialogFragment.TAG);
            }
        });
    }

    private void showErrorDialog(String message) {
        if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
            binding.progressBarLoading.setVisibility(View.INVISIBLE);
        }
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(message);
        fragment.show(getParentFragmentManager(), ErrorDialogFragment.TAG);
    }

    private void deleteAttachment() {
        String centerName = SipSupportSharedPreferences.getCenterName(getContext());
        String userLoginKey = SipSupportSharedPreferences.getUserLoginKey(getContext());
        int attachID = getArguments().getInt(ARGS_ATTACH_ID);
        ServerData serverData = viewModel.getServerData(centerName);
        viewModel.getSipSupporterServiceForDeleteAttachment(serverData.getIpAddress() + ":" + serverData.getPort());
        String path = "/api/v1/attach/Delete/";
        viewModel.deleteAttachment(path, userLoginKey, attachID);
    }

    private void setupObserver() {
        viewModel.getYesDelete().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean yesDelete) {
                if (binding.progressBarLoading.getVisibility() == View.INVISIBLE) {
                    binding.progressBarLoading.setVisibility(View.VISIBLE);
                }
                deleteAttachment();
            }
        });

        viewModel.getDeleteAttachResultSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<AttachResult>() {
            @Override
            public void onChanged(AttachResult attachResult) {
                if (binding.progressBarLoading.getVisibility() == View.VISIBLE) {
                    binding.progressBarLoading.setVisibility(View.INVISIBLE);
                }

                if (attachResult.getErrorCode() == "0") {

                    if (attachResult.getAttachs().length != 0) {
                        int attachID = attachResult.getAttachs()[0].getAttachID();
                        EventBus.getDefault().postSticky(new DeleteEvent(attachID));

                        SuccessDeletePhotoDialogFragment fragment = SuccessDeletePhotoDialogFragment.newInstance(getString(R.string.success_delete_photo_message));
                        fragment.show(getParentFragmentManager(), SuccessDeletePhotoDialogFragment.TAG);
                    }
                } else {
                    showErrorDialog(attachResult.getError());
                }
            }
        });

        viewModel.getNoConnectionExceptionSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                showErrorDialog(message);
            }
        });

        viewModel.getTimeoutExceptionHappenSingleLiveEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                showErrorDialog(message);
            }
        });
    }
}