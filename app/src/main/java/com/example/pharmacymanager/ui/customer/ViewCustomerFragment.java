package com.example.pharmacymanager.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Customer;
import com.example.pharmacymanager.data.repositories.CustomerRepository;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewCustomerFragment extends Fragment {

    private Customer customer;
    
    private MaterialButton btnEditCustomer;
    private MaterialButton btnDeleteCustomer;
    
    private CustomerRepository customerRepository;

    public ViewCustomerFragment() {
        // Required empty public constructor
    }

    public static ViewCustomerFragment newInstance(Customer customer) {
        ViewCustomerFragment fragment = new ViewCustomerFragment();
        Bundle args = new Bundle();
        args.putSerializable("customer", customer);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_customer, container, false);

        // Get customer from arguments
        if (getArguments() != null) {
            customer = (Customer) getArguments().getSerializable("customer");
        }

        if (customer == null) {
            Toast.makeText(requireContext(), "Customer not found", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return view;
        }

        // Initialize views
        initializeViews(view);

        // Initialize repository
        customerRepository = new CustomerRepository(requireContext());

        // Populate fields with customer data
        populateFields();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        btnEditCustomer = view.findViewById(R.id.btn_edit_customer);
        btnDeleteCustomer = view.findViewById(R.id.btn_delete_customer);
    }

    private void populateFields() {
        // Set customer name
        android.widget.TextView customerName = getView().findViewById(R.id.customer_name);
        customerName.setText(customer.getName());

        // Set customer email
        android.widget.TextView customerEmail = getView().findViewById(R.id.customer_email);
        customerEmail.setText(customer.getEmail());

        // Set customer phone
        android.widget.TextView customerPhone = getView().findViewById(R.id.customer_phone);
        customerPhone.setText(customer.getPhone());

        // Set customer address
        android.widget.TextView customerAddress = getView().findViewById(R.id.customer_address);
        String address = customer.getAddress();
        if (address == null || address.isEmpty()) {
            customerAddress.setText("No address provided");
        } else {
            customerAddress.setText(address);
        }

        // Set loyalty points
        android.widget.TextView customerLoyaltyPoints = getView().findViewById(R.id.customer_loyalty_points);
        customerLoyaltyPoints.setText(String.valueOf(customer.getLoyaltyPoints()));

        // Set created date
        android.widget.TextView customerCreatedAt = getView().findViewById(R.id.customer_created_at);
        String createdAt = formatDate(customer.getCreatedAt());
        customerCreatedAt.setText(createdAt);

        // Set updated date
        android.widget.TextView customerUpdatedAt = getView().findViewById(R.id.customer_updated_at);
        String updatedAt = formatDate(customer.getUpdatedAt());
        customerUpdatedAt.setText(updatedAt);
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Not available";
        }
        
        try {
            // Assuming the date comes in ISO format from the API
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            // If parsing fails, return the original string
            return dateString;
        }
    }

    private void setupClickListeners() {
        btnEditCustomer.setOnClickListener(v -> {

            if (getActivity() != null) {
                EditCustomerFragment editFragment = EditCustomerFragment.newInstance(customer);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragement_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnDeleteCustomer.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete " + customer.getName() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteCustomer())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCustomer() {
        // Disable button to prevent multiple submissions
        btnDeleteCustomer.setEnabled(false);
        btnDeleteCustomer.setText("Deleting...");

        customerRepository.deleteCustomer(customer.getId(), new CustomerRepository.CustomerCallback() {
            @Override
            public void onSuccess(Customer deletedCustomer) {
                btnDeleteCustomer.setEnabled(true);
                btnDeleteCustomer.setText("Delete Customer");
                
                Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
                
                // Navigate back to list
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onError(String message) {
                btnDeleteCustomer.setEnabled(true);
                btnDeleteCustomer.setText("Delete Customer");
                
                Toast.makeText(requireContext(), "Error deleting customer: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
