package com.example.pharmacymanager.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.ui.category.AddCategoryFragment;
import com.example.pharmacymanager.ui.category.ListCategoryFragment;
import com.example.pharmacymanager.ui.customer.AddCustomerFragment;
import com.example.pharmacymanager.ui.customer.ListCustomerFragment;
import com.example.pharmacymanager.ui.home.HomeFragment;
import com.example.pharmacymanager.ui.order.AddOrderFragment;
import com.example.pharmacymanager.ui.order.ListOrderFragment;
import com.example.pharmacymanager.ui.product.AddProductFragment;
import com.example.pharmacymanager.ui.product.ListProductFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    MainActivity.super.onBackPressed();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MainActivity", "onRestart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume called");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragement_container, new HomeFragment()).commit();
        } else if (id == R.id.nav_category) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragement_container, new ListCategoryFragment()).commit();
        }else if (id == R.id.nav_product) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragement_container, new ListProductFragment()).commit();
        } else if (id == R.id.nav_customer) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragement_container, new ListCustomerFragment()).commit();
        } else if (id == R.id.nav_order) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragement_container, new ListOrderFragment()).commit();
        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragement_container, new HomeFragment()).commit();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Method to navigate to AddCategoryFragment (can be called from other fragments)
    public void navigateToAddCategory() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragement_container, new AddCategoryFragment())
                .addToBackStack(null)
                .commit();
    }

    // Method to navigate to AddProductFragment (can be called from other fragments)
    public void navigateToAddProduct() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragement_container, new AddProductFragment())
                .addToBackStack(null)
                .commit();
    }

    // Method to navigate to AddCustomerFragment (can be called from other fragments)
    public void navigateToAddCustomer() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragement_container, new AddCustomerFragment())
                .addToBackStack(null)
                .commit();
    }

    // Method to navigate to AddOrderFragment (can be called from other fragments)
    public void navigateToAddOrder() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragement_container, AddOrderFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    /*@Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }*/
}