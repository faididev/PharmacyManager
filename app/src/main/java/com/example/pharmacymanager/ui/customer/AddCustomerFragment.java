package com.example.pharmacymanager.ui.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Customer;
import com.example.pharmacymanager.data.local.SessionManager;
import com.example.pharmacymanager.data.repositories.CustomerRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.EditText;

public class AddCustomerFragment extends Fragment {

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
    
    private Button createCustomerBtn;
    private Button btnBack;
    
    private CustomerRepository customerRepository;
    private SessionManager sessionManager;

    public AddCustomerFragment() {
        // Required empty public constructor
    }

    public static AddCustomerFragment newInstance(String param1, String param2) {
        AddCustomerFragment fragment = new AddCustomerFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_customer, container, false);

        // Initialize views
        initializeViews(view);

        // Initialize repository and session manager
        customerRepository = new CustomerRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

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

        createCustomerBtn = view.findViewById(R.id.create_customer_btn);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        createCustomerBtn.setOnClickListener(v -> createCustomer());
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void createCustomer() {
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
            Log.w("AddCustomerFragment", "No user ID found, using default user ID: " + userId);
        }

        // Disable button to prevent multiple submissions
        createCustomerBtn.setEnabled(false);
        createCustomerBtn.setText("Creating...");

        customerRepository.createCustomer(name, email, phone, address, userId, loyaltyPoints,
                new CustomerRepository.CustomerCallback() {
                    @Override
                    public void onSuccess(Customer customer) {
                        createCustomerBtn.setEnabled(true);
                        createCustomerBtn.setText("Create Customer");
                        
                        Toast.makeText(requireContext(), "Customer created successfully", Toast.LENGTH_SHORT).show();
                        
                        // Navigate back to list
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        createCustomerBtn.setEnabled(true);
                        createCustomerBtn.setText("Create Customer");
                        
                        Toast.makeText(requireContext(), "Error creating customer: " + message, Toast.LENGTH_LONG).show();
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
