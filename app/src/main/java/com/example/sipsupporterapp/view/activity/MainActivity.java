package com.example.sipsupporterapp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.databinding.ActivityMainBinding;
import com.example.sipsupporterapp.utils.Converter;
import com.example.sipsupporterapp.utils.SipSupportSharedPreferences;
import com.example.sipsupporterapp.viewmodel.CaseViewModel;
import com.example.sipsupporterapp.viewmodel.CustomerViewModel;
import com.example.sipsupporterapp.viewmodel.PaymentViewModel;
import com.google.android.material.navigation.NavigationView;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CustomerViewModel customerViewModel;
    private CaseViewModel caseViewModel;
    private PaymentViewModel paymentViewModel;
    private int destinationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        caseViewModel = new ViewModelProvider(this).get(CaseViewModel.class);
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);

        handleEvents();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navigationView, navController);

        String userFullName = Converter.letterConverter(SipSupportSharedPreferences.getUserFullName(this));

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
                        SipSupportSharedPreferences.setCustomerName(MainActivity.this, null);
                        SipSupportSharedPreferences.setCustomerTel(MainActivity.this, null);
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

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                destinationID = destination.getId();
                if (destination.getId() == R.id.menu_search) {
                    binding.edTextSearch.setVisibility(View.VISIBLE);
                    binding.edTextSearch.setText("");
                    binding.btnSearch.setVisibility(View.VISIBLE);
                    binding.ivAdd.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.menu_tasks) {
                    binding.edTextSearch.setVisibility(View.VISIBLE);
                    binding.edTextSearch.setText("");
                    binding.btnSearch.setVisibility(View.VISIBLE);
                    binding.ivAdd.setVisibility(View.VISIBLE);
                } else if (destination.getId() == R.id.menu_payments) {
                    binding.edTextSearch.setVisibility(View.GONE);
                    binding.btnSearch.setVisibility(View.GONE);
                    binding.ivAdd.setVisibility(View.VISIBLE);
                } else {
                    binding.edTextSearch.setVisibility(View.GONE);
                    binding.btnSearch.setVisibility(View.GONE);
                    binding.ivAdd.setVisibility(View.GONE);
                }
            }
        });

        binding.edTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (destinationID == R.id.menu_search) {
                        customerViewModel.getSearchQuery().setValue(binding.edTextSearch.getText().toString());
                    } else if (destinationID == R.id.menu_tasks) {
                        caseViewModel.getCaseSearchQuery().setValue(binding.edTextSearch.getText().toString());
                    }
                    return true;
                }
                return false;
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

        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (destinationID == R.id.menu_search) {
                    customerViewModel.getSearchQuery().setValue(binding.edTextSearch.getText().toString());
                } else if (destinationID == R.id.menu_tasks) {
                    caseViewModel.getCaseSearchQuery().setValue(binding.edTextSearch.getText().toString());
                }
            }
        });

        binding.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PowerMenu powerMenu = null;
                if (destinationID == R.id.menu_payments) {
                    powerMenu = new PowerMenu.Builder(MainActivity.this)
                            .addItem(new PowerMenuItem("افزودن هزینه"))
                            .setTextColor(Color.parseColor("#000000"))
                            .setTextSize(14)
                            .setTextGravity(Gravity.RIGHT)
                            .build();
                } else if (destinationID == R.id.menu_tasks) {
                    powerMenu = new PowerMenu.Builder(MainActivity.this)
                            .addItem(new PowerMenuItem("افزودن case"))
                            .setTextColor(Color.parseColor("#000000"))
                            .setTextSize(14)
                            .setTextGravity(Gravity.RIGHT)
                            .build();
                }

                PowerMenu finalPowerMenu = powerMenu;
                powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                    @Override
                    public void onItemClick(int i, PowerMenuItem item) {
                        switch (i) {
                            case 0:
                                if (destinationID == R.id.menu_payments) {
                                    paymentViewModel.getAddNewPaymentClicked().setValue(true);

                                } else if (destinationID == R.id.menu_tasks) {
                                    caseViewModel.getAddNewCaseClicked().setValue(true);
                                }
                                finalPowerMenu.dismiss();
                                break;
                        }
                    }
                });
                powerMenu.showAsDropDown(view);
            }
        });
    }

    public static Intent start(Context context) {
        return new Intent(context, MainActivity.class);
    }
}