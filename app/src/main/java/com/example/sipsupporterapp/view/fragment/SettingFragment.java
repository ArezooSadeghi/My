package com.example.sipsupporterapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.FragmentSettingBinding;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_setting,
                container,
                false);

        initViews();
        handleEvents();

        return binding.getRoot();
    }

    private void initViews() {
        List<String> factorList = new ArrayList<String>() {{
            add("1");
            add("1.5");
            add("2");
            add("3");
        }};
        String userFullName = Converter.letterConverter(SipSupportSharedPreferences.getUserFullName(getContext()));
        binding.txtUserFullName.setText(userFullName);

        if (SipSupportSharedPreferences.getFactor(getContext()) == null) {
            binding.spinner.setItems(factorList);
        } else {
            String factor = SipSupportSharedPreferences.getFactor(getContext());
            for (int i = 0; i < factorList.size(); i++) {
                if (factorList.get(i).equals(factor)) {
                    factorList.remove(i);
                    break;
                }
            }
            factorList.add(0, factor);
            binding.spinner.setItems(factorList);
        }
    }

    private void handleEvents() {
        binding.spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String factor = (String) item;
                SipSupportSharedPreferences.setFactor(getContext(), factor);
            }
        });
    }
}