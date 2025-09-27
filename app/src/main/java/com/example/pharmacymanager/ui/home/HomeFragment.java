package com.example.pharmacymanager.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.ui.MainActivity;

public class HomeFragment extends Fragment {

    private TextView totalRevenue;
    private TextView totalOrders;
    private TextView totalProducts;
    private TextView totalCustomers;
    private TextView totalCategories;
    private TextView todaysSales;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize views
        initializeViews(view);
        
        // Set up click listeners for quick actions
        setupQuickActionListeners(view);
        
        // Load static data
        loadStaticData();
        
        return view;
    }

    private void initializeViews(View view) {
        totalRevenue = view.findViewById(R.id.total_revenue);
        totalOrders = view.findViewById(R.id.total_orders);
        totalProducts = view.findViewById(R.id.total_products);
        totalCustomers = view.findViewById(R.id.total_customers);
        totalCategories = view.findViewById(R.id.total_categories);
        todaysSales = view.findViewById(R.id.todays_sales);
    }

    private void setupQuickActionListeners(View view) {
        // Add Product button
        view.findViewById(R.id.add_product_btn).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddProduct();
            }
        });

        // Add Order button
        view.findViewById(R.id.add_order_btn).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddOrder();
            }
        });

        // Add Customer button
        view.findViewById(R.id.add_customer_btn).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddCustomer();
            }
        });

        // Add Category button
        view.findViewById(R.id.add_category_btn).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddCategory();
            }
        });
    }

    private void loadStaticData() {
        totalRevenue.setText("$12,450.00");
        totalOrders.setText("156");
        totalProducts.setText("89");
        totalCustomers.setText("234");
        totalCategories.setText("12");
        todaysSales.setText("$1,250.00");
    }
}