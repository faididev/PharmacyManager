package com.example.pharmacymanager.ui.order;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Customer;
import com.example.pharmacymanager.data.entities.Order;
import com.example.pharmacymanager.data.entities.OrderItem;
import com.example.pharmacymanager.data.repositories.CustomerRepository;
import com.example.pharmacymanager.data.repositories.OrderRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddOrderFragment extends Fragment {

    private TextInputLayout orderDateLayout;
    private TextInputLayout orderStatusLayout;
    
    private Spinner customerSpinner;
    private EditText orderDateEditText;
    private EditText orderStatusEditText;
    
    private Button createOrderBtn;
    private Button btnBack;
    private Button btnAddItem;
    
    private LinearLayout orderItemsContainer;
    private List<OrderItem> orderItems;
    private List<Customer> customers;
    private int itemCounter = 1;
    private int selectedCustomerId = -1;
    
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;

    public AddOrderFragment() {
        // Required empty public constructor
    }

    public static AddOrderFragment newInstance() {
        return new AddOrderFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_order, container, false);

        // Initialize views
        initializeViews(view);

        // Initialize repositories
        orderRepository = new OrderRepository(requireContext());
        customerRepository = new CustomerRepository(requireContext());

        // Initialize order items list
        orderItems = new ArrayList<>();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        try {
            orderDateLayout = view.findViewById(R.id.order_date);
            orderStatusLayout = view.findViewById(R.id.order_status);

            customerSpinner = view.findViewById(R.id.customer_spinner);
            if (orderDateLayout != null) {
                orderDateEditText = orderDateLayout.getEditText();
            }
            if (orderStatusLayout != null) {
                orderStatusEditText = orderStatusLayout.getEditText();
            }

            createOrderBtn = view.findViewById(R.id.create_order_btn);
            btnBack = view.findViewById(R.id.btnBack);
            btnAddItem = view.findViewById(R.id.btn_add_item);
            
            orderItemsContainer = view.findViewById(R.id.order_items_container);
            
            Log.d("AddOrderFragment", "Views initialized successfully");
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error initializing views: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error initializing form: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        try {
            if (createOrderBtn != null) {
                createOrderBtn.setOnClickListener(v -> createOrder());
            }
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                });
            }
            if (btnAddItem != null) {
                btnAddItem.setOnClickListener(v -> addOrderItem());
            }
            
            // Date picker
            if (orderDateEditText != null) {
                orderDateEditText.setOnClickListener(v -> showDatePicker());
            }
            
            // Customer spinner
            if (customerSpinner != null) {
                customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0 && customers != null && position <= customers.size()) {
                            selectedCustomerId = customers.get(position - 1).getId();
                        } else {
                            selectedCustomerId = -1;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedCustomerId = -1;
                    }
                });
            }
            
            // Load customers
            loadCustomers();
            
            Log.d("AddOrderFragment", "Click listeners setup successfully");
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error setting up click listeners: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error setting up form: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addOrderItem() {
        try {
            // Create a new order item view
            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.rv_order_item_template, orderItemsContainer, false);
            
            // Set item number
            TextView itemTitle = itemView.findViewById(R.id.tv_product_name);
            itemTitle.setText("Item " + itemCounter);
            
            // Set up remove button
            Button removeBtn = itemView.findViewById(R.id.btn_remove_item_1);
            removeBtn.setOnClickListener(v -> {
                orderItemsContainer.removeView(itemView);
                // Remove corresponding order item from list
                // Note: This is a simplified implementation
            });
            
            // Add to container
            orderItemsContainer.addView(itemView);
            itemCounter++;
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error adding order item: " + e.getMessage());
            Toast.makeText(requireContext(), "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createOrder() {
        try {
            Log.d("AddOrderFragment", "createOrder() called");
            
            if (!validateInputs()) {
                Log.d("AddOrderFragment", "Validation failed");
                return;
            }

            if (selectedCustomerId == -1) {
                Toast.makeText(requireContext(), "Please select a customer", Toast.LENGTH_SHORT).show();
                Log.d("AddOrderFragment", "No customer selected");
                return;
            }

            String orderDate = orderDateEditText.getText().toString().trim();
            String status = orderStatusEditText.getText().toString().trim();

            Log.d("AddOrderFragment", "Creating order - Customer ID: " + selectedCustomerId + ", Date: " + orderDate + ", Status: " + status);

            // Collect order items from the form
            List<OrderItem> items = collectOrderItems();
            
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), "Please add at least one item to the order", Toast.LENGTH_SHORT).show();
                Log.d("AddOrderFragment", "No items added");
                return;
            }

            Log.d("AddOrderFragment", "Items count: " + items.size());

            // Disable button to prevent multiple submissions
            createOrderBtn.setEnabled(false);
            createOrderBtn.setText("Creating...");

            orderRepository.createOrder(selectedCustomerId, orderDate, status, items,
                    new OrderRepository.OrderCallback() {
                        @Override
                        public void onSuccess(Order order) {
                            Log.d("AddOrderFragment", "Order created successfully");
                            createOrderBtn.setEnabled(true);
                            createOrderBtn.setText("Create Order");
                            
                            Toast.makeText(requireContext(), "Order created successfully", Toast.LENGTH_SHORT).show();
                            
                            // Navigate back to list
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }

                        @Override
                        public void onError(String message) {
                            Log.e("AddOrderFragment", "Error creating order: " + message);
                            createOrderBtn.setEnabled(true);
                            createOrderBtn.setText("Create Order");
                            
                            Toast.makeText(requireContext(), "Error creating order: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error in createOrder: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error creating order: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Re-enable button
            if (createOrderBtn != null) {
                createOrderBtn.setEnabled(true);
                createOrderBtn.setText("Create Order");
            }
        }
    }

    private List<OrderItem> collectOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        
        // For now, create sample items based on the template
        // In a real implementation, you would collect data from the dynamic form
        OrderItem item1 = new OrderItem(1, 2, 5.99, "Sample Product 1");
        items.add(item1);
        
        OrderItem item2 = new OrderItem(2, 1, 12.99, "Sample Product 2");
        items.add(item2);
        
        return items;
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Clear previous errors
        orderDateLayout.setError(null);
        orderStatusLayout.setError(null);

        // Validate customer selection
        if (selectedCustomerId == -1) {
            Toast.makeText(requireContext(), "Please select a customer", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Validate order date
        if (orderDateEditText.getText().toString().trim().isEmpty()) {
            orderDateLayout.setError("Order date is required");
            isValid = false;
        }

        // Validate status
        if (orderStatusEditText.getText().toString().trim().isEmpty()) {
            orderStatusLayout.setError("Status is required");
            isValid = false;
        }

        return isValid;
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    orderDateEditText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }
    
    private void loadCustomers() {
        try {
            if (customerRepository == null) {
                Log.e("AddOrderFragment", "CustomerRepository is null");
                Toast.makeText(requireContext(), "Error: Customer repository not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            customerRepository.getCustomers(new CustomerRepository.CustomerListCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        customers = parseCustomersFromResponse(response);
                        setupCustomerSpinner();
                        Log.d("AddOrderFragment", "Customers loaded successfully: " + customers.size());
                    } catch (JSONException e) {
                        Log.e("AddOrderFragment", "Error parsing customers: " + e.getMessage());
                        Toast.makeText(requireContext(), "Error loading customers", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e("AddOrderFragment", "Error loading customers: " + message);
                    Toast.makeText(requireContext(), "Error loading customers: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error in loadCustomers: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error loading customers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private List<Customer> parseCustomersFromResponse(JSONObject response) throws JSONException {
        List<Customer> customerList = new ArrayList<>();
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject customerJson = dataArray.getJSONObject(i);
                Customer customer = Customer.fromJson(customerJson);
                customerList.add(customer);
            }
        }
        
        return customerList;
    }
    
    private void setupCustomerSpinner() {
        if (customers == null || customers.isEmpty()) {
            return;
        }
        
        List<String> customerNames = new ArrayList<>();
        customerNames.add("Select Customer"); // Default option
        
        for (Customer customer : customers) {
            customerNames.add(customer.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, customerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerSpinner.setAdapter(adapter);
    }
}
