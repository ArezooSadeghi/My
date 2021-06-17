package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.ActivityMainBinding;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        handleEvents();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

        String userFullName = Converter.convert(SipSupportSharedPreferences.getUserFullName(this));

        View headerView = binding.navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.txt_user_name);
        navUsername.setText(userFullName);

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_logout:
                        SipSupportSharedPreferences.setUserLoginKey(MainActivity.this, null);
                        SipSupportSharedPreferences.setUserFullName(MainActivity.this, null);
                        SipSupportSharedPreferences.setCustomerUserId(MainActivity.this, 0);
                        SipSupportSharedPreferences.setCustomerName(MainActivity.this, null);
                        SipSupportSharedPreferences.setCustomerTel(MainActivity.this, null);
                        SipSupportSharedPreferences.setLastSearchQuery(MainActivity.this, null);
                        Intent intent = LoginContainerActivity.start(MainActivity.this);
                        startActivity(intent);
                        finish();
                        break;
                }
                NavigationUI.onNavDestinationSelected(menuItem, navController);
                binding.drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    private void handleEvents() {
        binding.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        binding.navigationView.setItemIconTintList(null);
    }

    public static Intent start(Context context) {
        return new Intent(context, MainActivity.class);
    }
}