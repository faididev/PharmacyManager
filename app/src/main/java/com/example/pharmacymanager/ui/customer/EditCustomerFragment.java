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
import com.example.pharmacymanager.data.local.SessionManager;
import com.example.pharmacymanager.data.repositories.CustomerRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.EditText;

public class EditCustomerFragment extends Fragment {

    private Customer customer;
    
    private TextInputLayout customerNameLayout;
    private TextInputLayout customerEmailLayout;
    private TextInputLayout customerPhoneLayout;
    private TextInputLayout customerAddressLayout;
    private TextInputLayout customerLoyaltyPointsLayout;
    
    private EditText customerNameEditText;
    private EditText customerEmailEditText;
    private EditText customerPhoneEditText;
    private EditText customerAddressEditText;
    private EditText customerLoyaltyPointsEditText;
    
    private MaterialButton updateCustomerBtn;
    private MaterialButton deleteCustomerBtn;
    private MaterialButton btnBack;
    
    private CustomerRepository customerRepository;
    private SessionManager sessionManager;

    public EditCustomerFragment() {
        // Required empty public constructor
    }

    public static EditCustomerFragment newInstance(Customer customer) {
        EditCustomerFragment fragment = new EditCustomerFragment();
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

        View view = inflater.inflate(R.layout.fragment_edit_customer, container, false);

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

        // Initialize repository and session manager
        customerRepository = new CustomerRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Populate fields with customer data
        populateFields();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        customerNameLayout = view.findViewById(R.id.customer_name);
        customerEmailLayout = view.findViewById(R.id.customer_email);
        customerPhoneLayout = view.findViewById(R.id.customer_phone);
        customerAddressLayout = view.findViewById(R.id.customer_address);
        customerLoyaltyPointsLayout = view.findViewById(R.id.customer_loyalty_points);

        customerNameEditText = customerNameLayout.getEditText();
        customerEmailEditText = customerEmailLayout.getEditText();
        customerPhoneEditText = customerPhoneLayout.getEditText();
        customerAddressEditText = customerAddressLayout.getEditText();
        customerLoyaltyPointsEditText = customerLoyaltyPointsLayout.getEditText();

        updateCustomerBtn = view.findViewById(R.id.update_customer_btn);
        deleteCustomerBtn = view.findViewById(R.id.delete_customer_btn);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void populateFields() {
        customerNameEditText.setText(customer.getName());
        customerEmailEditText.setText(customer.getEmail());
        customerPhoneEditText.setText(customer.getPhone());
        customerAddressEditText.setText(customer.getAddress());
        customerLoyaltyPointsEditText.setText(String.valueOf(customer.getLoyaltyPoints()));
    }

    private void setupClickListeners() {
        updateCustomerBtn.setOnClickListener(v -> updateCustomer());
        deleteCustomerBtn.setOnClickListener(v -> showDeleteConfirmation());
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void updateCustomer() {
        if (!validateInputs()) {
            return;
        }

        String name = customerNameEditText.getText().toString().trim();
        String email = customerEmailEditText.getText().toString().trim();
        String phone = customerPhoneEditText.getText().toString().trim();
        String address = customerAddressEditText.getText().toString().trim();
        int loyaltyPoints = 0;
        
        try {
            String loyaltyPointsStr = customerLoyaltyPointsEditText.getText().toString().trim();
            if (!loyaltyPointsStr.isEmpty()) {
                loyaltyPoints = Integer.parseInt(loyaltyPointsStr);
            }
        } catch (NumberFormatException e) {
            customerLoyaltyPointsLayout.setError("Please enter a valid number");
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            userId = 1; // Default user ID for testing
            android.util.Log.w("EditCustomerFragment", "No user ID found, using default user ID: " + userId);
        }

        // Disable button to prevent multiple submissions
        updateCustomerBtn.setEnabled(false);
        updateCustomerBtn.setText("Updating...");

        customerRepository.updateCustomer(customer.getId(), name, email, phone, address, userId, loyaltyPoints,
                new CustomerRepository.CustomerCallback() {
                    @Override
                    public void onSuccess(Customer updatedCustomer) {
                        updateCustomerBtn.setEnabled(true);
                        updateCustomerBtn.setText("Update Customer");
                        
                        Toast.makeText(requireContext(), "Customer updated successfully", Toast.LENGTH_SHORT).show();
                        
                        // Navigate back to list
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        updateCustomerBtn.setEnabled(true);
                        updateCustomerBtn.setText("Update Customer");
                        
                        Toast.makeText(requireContext(), "Error updating customer: " + message, Toast.LENGTH_LONG).show();
                    }
                });
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
        deleteCustomerBtn.setEnabled(false);
        deleteCustomerBtn.setText("Deleting...");

        customerRepository.deleteCustomer(customer.getId(), new CustomerRepository.CustomerCallback() {
            @Override
            public void onSuccess(Customer deletedCustomer) {
                deleteCustomerBtn.setEnabled(true);
                deleteCustomerBtn.setText("Delete Customer");
                
                Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
                
                // Navigate back to list
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onError(String message) {
                deleteCustomerBtn.setEnabled(true);
                deleteCustomerBtn.setText("Delete Customer");
                
                Toast.makeText(requireContext(), "Error deleting customer: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        customerNameLayout.setError(null);
        customerEmailLayout.setError(null);
        customerPhoneLayout.setError(null);
        customerAddressLayout.setError(null);
        customerLoyaltyPointsLayout.setError(null);

        // Validate name
        if (customerNameEditText.getText().toString().trim().isEmpty()) {
            customerNameLayout.setError("Customer name is required");
            isValid = false;
        }

        // Validate email
        String email = customerEmailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            customerEmailLayout.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            customerEmailLayout.setError("Please enter a valid email address");
            isValid = false;
        }

        // Validate phone
        if (customerPhoneEditText.getText().toString().trim().isEmpty()) {
            customerPhoneLayout.setError("Phone number is required");
            isValid = false;
        }

        return isValid;
    }
}
