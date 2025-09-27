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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Customer;
import com.example.pharmacymanager.data.entities.Order;
import com.example.pharmacymanager.data.entities.OrderItem;
import com.example.pharmacymanager.data.entities.Product;
import com.example.pharmacymanager.data.repositories.CustomerRepository;
import com.example.pharmacymanager.data.repositories.OrderRepository;
import com.example.pharmacymanager.data.repositories.ProductRepository;
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

    private Spinner customerSpinner;
    private Spinner statusSpinner;
    
    private Button createOrderBtn;
    private Button btnBack;
    private Button btnRefreshCustomers;
    
    private LinearLayout orderItemsContainer;
    private TextView totalAmountText;
    private RecyclerView rvProducts;
    private TextInputEditText etSearchProducts;
    
    private List<OrderItem> orderItems;
    private List<Customer> customers;
    private List<Product> products;
    private InlineProductAdapter productAdapter;
    private int itemCounter = 1;
    private int selectedCustomerId = -1;
    
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

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
        Log.d("AddOrderFragment", "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_add_order, container, false);

        // Initialize views
        initializeViews(view);

        // Initialize repositories
        orderRepository = new OrderRepository(requireContext());
        customerRepository = new CustomerRepository(requireContext());
        productRepository = new ProductRepository(requireContext());
        
        Log.d("AddOrderFragment", "Repositories initialized");

        // Initialize lists
        orderItems = new ArrayList<>();
        customers = new ArrayList<>();
        products = new ArrayList<>();
        
        Log.d("AddOrderFragment", "Lists initialized");

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        try {
            customerSpinner = view.findViewById(R.id.customer_spinner);
            statusSpinner = view.findViewById(R.id.status_spinner);
            
            createOrderBtn = view.findViewById(R.id.create_order_btn);
            btnBack = view.findViewById(R.id.btnBack);
            btnRefreshCustomers = view.findViewById(R.id.btn_refresh_customers);
            
            orderItemsContainer = view.findViewById(R.id.order_items_container);
            totalAmountText = view.findViewById(R.id.total_amount_text);
            rvProducts = view.findViewById(R.id.rv_products);
            etSearchProducts = view.findViewById(R.id.et_search_products);
            
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
            
            if (btnRefreshCustomers != null) {
                btnRefreshCustomers.setOnClickListener(v -> {
                    Toast.makeText(requireContext(), "Refreshing customers...", Toast.LENGTH_SHORT).show();
                    loadCustomers();
                });
            }
            
            // Customer spinner
            if (customerSpinner != null) {
                customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0 && customers != null && position <= customers.size()) {
                            selectedCustomerId = customers.get(position - 1).getId();
                            Log.d("AddOrderFragment", "Customer selected: " + customers.get(position - 1).getName());
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
            
            // Status spinner
            if (statusSpinner != null) {
                setupStatusSpinner();
            }
            
            // Setup product search
            setupProductSearch();
            
            // Setup product recycler view
            setupProductRecyclerView();
            
            // Load customers and products
            loadCustomers();
            loadProducts();
            
            Log.d("AddOrderFragment", "Click listeners setup successfully");
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error setting up click listeners: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error setting up form: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupProductSearch() {
        if (etSearchProducts != null) {
            etSearchProducts.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (productAdapter != null) {
                        productAdapter.filterProducts(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
    }
    
    private void setupProductRecyclerView() {
        Log.d("AddOrderFragment", "=== SETUP PRODUCT RECYCLER VIEW ===");
        if (rvProducts != null) {
            Log.d("AddOrderFragment", "RecyclerView is not null, creating adapter");
            productAdapter = new InlineProductAdapter(new ArrayList<>(), new InlineProductAdapter.OnProductActionListener() {
                @Override
                public void onAddProduct(Product product, int quantity) {
                    Log.d("AddOrderFragment", "=== ADDING PRODUCT INLINE ===");
                    Log.d("AddOrderFragment", "Product: " + product.getName() + ", Quantity: " + quantity);
                    addOrderItem(product, quantity);
                }

                @Override
                public void onUpdateQuantity(Product product, int quantity) {
                    // This can be used for future enhancements
                }

                @Override
                public void onRemoveProduct(Product product) {
                    // This can be used for future enhancements
                }
            });
            
            rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvProducts.setAdapter(productAdapter);
            Log.d("AddOrderFragment", "RecyclerView setup completed");
        } else {
            Log.e("AddOrderFragment", "RecyclerView is null!");
        }
    }

    private void addOrderItem(Product product, int quantity) {
        try {
            Log.d("AddOrderFragment", "=== ADDING ORDER ITEM ===");
            Log.d("AddOrderFragment", "Product: " + product.getName());
            Log.d("AddOrderFragment", "Quantity: " + quantity);
            Log.d("AddOrderFragment", "Price: $" + product.getPrice());
            Log.d("AddOrderFragment", "ID: " + product.getId() + ", UUID: " + product.getUuid());
            
            // Create order item
            OrderItem orderItem = new OrderItem(
                product.getId(),
                quantity,
                product.getPrice(),
                product.getName()
            );
            
            Log.d("AddOrderFragment", "OrderItem created: " + orderItem.toString());
            Log.d("AddOrderFragment", "Total price: $" + orderItem.getTotalPrice());
            
            // Add to list
            orderItems.add(orderItem);
            Log.d("AddOrderFragment", "Order items count: " + orderItems.size());
            
            // Create view for the order item
            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.rv_order_item_template, orderItemsContainer, false);
            Log.d("AddOrderFragment", "Item view created successfully");
            
            // Set product information
            TextView productName = itemView.findViewById(R.id.tv_product_name);
            TextView productId = itemView.findViewById(R.id.tv_product_id);
            TextView unitPrice = itemView.findViewById(R.id.tv_unit_price);
            TextView quantityText = itemView.findViewById(R.id.tv_quantity);
            TextView itemTotal = itemView.findViewById(R.id.tv_item_total);
            
            productName.setText(product.getName());
            productId.setText("Product ID: " + product.getId());
            unitPrice.setText("Price: $" + String.format("%.2f", product.getPrice()));
            quantityText.setText(String.valueOf(quantity));
            itemTotal.setText("$" + String.format("%.2f", orderItem.getTotalPrice()));
            
            Log.d("AddOrderFragment", "Product information set in view");
            
            // Set up remove button
            Button removeBtn = itemView.findViewById(R.id.btn_remove_item_1);
            removeBtn.setOnClickListener(v -> {
                Log.d("AddOrderFragment", "Removing order item: " + product.getName());
                orderItemsContainer.removeView(itemView);
                orderItems.remove(orderItem);
                updateOrderTotal();
            });
            
            // Set up quantity controls
            Button decreaseBtn = itemView.findViewById(R.id.btn_decrease_qty);
            Button increaseBtn = itemView.findViewById(R.id.btn_increase_qty);
            
            decreaseBtn.setOnClickListener(v -> {
                int currentQty = orderItem.getQuantity();
                if (currentQty > 1) {
                    orderItem.setQuantity(currentQty - 1);
                    quantityText.setText(String.valueOf(orderItem.getQuantity()));
                    itemTotal.setText("$" + String.format("%.2f", orderItem.getTotalPrice()));
                    updateOrderTotal();
                }
            });
            
            increaseBtn.setOnClickListener(v -> {
                int currentQty = orderItem.getQuantity();
                orderItem.setQuantity(currentQty + 1);
                quantityText.setText(String.valueOf(orderItem.getQuantity()));
                itemTotal.setText("$" + String.format("%.2f", orderItem.getTotalPrice()));
                updateOrderTotal();
            });
            
            // Add to container
            orderItemsContainer.addView(itemView);
            Log.d("AddOrderFragment", "Item view added to container");
            
            // Update order total
            updateOrderTotal();
            
            Log.d("AddOrderFragment", "Successfully added order item: " + product.getName() + " x" + quantity);
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error adding order item: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOrderTotal() {
        Log.d("AddOrderFragment", "=== UPDATING ORDER TOTAL ===");
        Log.d("AddOrderFragment", "Order items count: " + orderItems.size());
        
        double total = 0.0;
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            double itemTotal = item.getTotalPrice();
            total += itemTotal;
            Log.d("AddOrderFragment", "Item " + i + ": " + item.getProductName() + 
                  " x" + item.getQuantity() + " = $" + String.format("%.2f", itemTotal));
        }
        
        Log.d("AddOrderFragment", "Calculated total: $" + String.format("%.2f", total));
        
        // Update total display
        if (totalAmountText != null) {
            totalAmountText.setText("$" + String.format("%.2f", total));
            Log.d("AddOrderFragment", "Total display updated");
        } else {
            Log.e("AddOrderFragment", "totalAmountText is null!");
        }
        
        Log.d("AddOrderFragment", "Order total updated: $" + String.format("%.2f", total));
    }

    private void createOrder() {
        try {
            Log.d("AddOrderFragment", "createOrder() called");
            
            // Validate customer selection
            if (selectedCustomerId == -1) {
                Toast.makeText(requireContext(), "Please select a customer", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validate items
            if (orderItems.isEmpty()) {
                Toast.makeText(requireContext(), "Please add at least one item to the order", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get selected status
            String status = (String) statusSpinner.getSelectedItem();
            if (status == null) {
                status = "pending";
            }
            
            // Use current date
            String orderDate = java.text.DateFormat.getDateInstance().format(new java.util.Date());
            
            Log.d("AddOrderFragment", "Creating order - Customer ID: " + selectedCustomerId + ", Status: " + status + ", Items: " + orderItems.size());
            
            // Disable button to prevent multiple submissions
            createOrderBtn.setEnabled(false);
            createOrderBtn.setText("Creating...");
            
            orderRepository.createOrder(selectedCustomerId, orderDate, status, orderItems,
                    new OrderRepository.OrderCallback() {
                        @Override
                        public void onSuccess(Order order) {
                            Log.d("AddOrderFragment", "Order created successfully");
                            createOrderBtn.setEnabled(true);
                            createOrderBtn.setText("Create Order");
                            
                            Toast.makeText(requireContext(), "Order created successfully", Toast.LENGTH_SHORT).show();
                            
                            // Navigate back to orders list
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }

                        @Override
                        public void onError(String message) {
                            Log.e("AddOrderFragment", "Error creating order: " + message);
                            createOrderBtn.setEnabled(true);
                            createOrderBtn.setText("Create Order");
                            Toast.makeText(requireContext(), "Error creating order: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error in createOrder: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error creating order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Re-enable button
            if (createOrderBtn != null) {
                createOrderBtn.setEnabled(true);
                createOrderBtn.setText("Create Order");
            }
        }
    }

    private void loadProducts() {
        try {
            if (productRepository == null) {
                Log.e("AddOrderFragment", "ProductRepository is null");
                Toast.makeText(requireContext(), "Error: Product repository not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d("AddOrderFragment", "Loading products...");
            productRepository.getProducts(new ProductRepository.ProductListCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        Log.d("AddOrderFragment", "=== PRODUCT API RESPONSE ===");
                        Log.d("AddOrderFragment", "Full response: " + response.toString());
                        
                        products = parseProductsFromResponse(response);
                        Log.d("AddOrderFragment", "Products loaded successfully: " + products.size());
                        
                        // Update the product adapter
                        if (productAdapter != null) {
                            Log.d("AddOrderFragment", "Updating product adapter with " + products.size() + " products");
                            productAdapter.updateProducts(products);
                            Log.d("AddOrderFragment", "Product adapter updated successfully");
                        } else {
                            Log.e("AddOrderFragment", "Product adapter is null! Cannot update products.");
                        }
                        
                        if (products.isEmpty()) {
                            Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show();
                        } else {
                            // Log first few products for debugging
                            for (int i = 0; i < Math.min(3, products.size()); i++) {
                                Product product = products.get(i);
                                Log.d("AddOrderFragment", "Product " + i + ": " + product.getName() + 
                                      " (ID: " + product.getId() + ", UUID: " + product.getUuid() + ", Price: $" + product.getPrice() + ")");
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("AddOrderFragment", "Error parsing products: " + e.getMessage(), e);
                        Toast.makeText(requireContext(), "Error parsing products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e("AddOrderFragment", "Error loading products: " + message);
                    Toast.makeText(requireContext(), "Error loading products: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("AddOrderFragment", "Error in loadProducts: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    
    private void loadCustomers() {
        try {
            if (customerRepository == null) {
                Log.e("AddOrderFragment", "CustomerRepository is null");
                Toast.makeText(requireContext(), "Error: Customer repository not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d("AddOrderFragment", "Loading customers...");
            customerRepository.getCustomers(new CustomerRepository.CustomerListCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        Log.d("AddOrderFragment", "=== CUSTOMER API RESPONSE ===");
                        Log.d("AddOrderFragment", "Full response: " + response.toString());
                        Log.d("AddOrderFragment", "Response keys: " + response.keys().next());
                        
                        // Check if response has data array
                        if (response.has("data")) {
                            Log.d("AddOrderFragment", "Response has 'data' key");
                            if (response.get("data") instanceof org.json.JSONArray) {
                                org.json.JSONArray dataArray = response.getJSONArray("data");
                                Log.d("AddOrderFragment", "Data is JSONArray with " + dataArray.length() + " items");
                                
                                // Log first item structure
                                if (dataArray.length() > 0) {
                                    Log.d("AddOrderFragment", "First customer item: " + dataArray.getJSONObject(0).toString());
                                }
                            } else {
                                Log.d("AddOrderFragment", "Data is not JSONArray: " + response.get("data").getClass().getSimpleName());
                            }
                        } else {
                            Log.d("AddOrderFragment", "Response does not have 'data' key");
                        }
                        
                        customers = parseCustomersFromResponse(response);
                        setupCustomerSpinner();
                        Log.d("AddOrderFragment", "Customers loaded successfully: " + customers.size());
                        if (customers.isEmpty()) {
                            Toast.makeText(requireContext(), "No customers found", Toast.LENGTH_SHORT).show();
                        } else {
                            // Log parsed customer details
                            for (int i = 0; i < customers.size(); i++) {
                                Customer customer = customers.get(i);
                                Log.d("AddOrderFragment", "Customer " + i + ": ID=" + customer.getId() + 
                                      ", Name=" + customer.getName() + ", Email=" + customer.getEmail());
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("AddOrderFragment", "Error parsing customers: " + e.getMessage(), e);
                        Toast.makeText(requireContext(), "Error parsing customers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        
        Log.d("AddOrderFragment", "=== PARSING CUSTOMERS ===");
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            Log.d("AddOrderFragment", "Found " + dataArray.length() + " customers in data array");
            
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    JSONObject customerJson = dataArray.getJSONObject(i);
                    Log.d("AddOrderFragment", "Parsing customer " + i + ": " + customerJson.toString());
                    
                    Customer customer = Customer.fromJson(customerJson);
                    customerList.add(customer);
                    Log.d("AddOrderFragment", "Successfully parsed customer: " + customer.getName());
                } catch (JSONException e) {
                    Log.e("AddOrderFragment", "Error parsing customer " + i + ": " + e.getMessage(), e);
                    // Try safe parsing as fallback
                    try {
                        JSONObject customerJson = dataArray.getJSONObject(i);
                        Customer customer = Customer.safeFromJson(customerJson);
                        customerList.add(customer);
                        Log.d("AddOrderFragment", "Successfully parsed customer with safe method: " + customer.getName());
                    } catch (Exception ex) {
                        Log.e("AddOrderFragment", "Both parsing methods failed for customer " + i + ": " + ex.getMessage(), ex);
                    }
                }
            }
        } else {
            Log.d("AddOrderFragment", "No 'data' key found in response");
        }
        
        Log.d("AddOrderFragment", "Final customer list size: " + customerList.size());
        return customerList;
    }

    private List<Product> parseProductsFromResponse(JSONObject response) throws JSONException {
        List<Product> productList = new ArrayList<>();
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject productJson = dataArray.getJSONObject(i);
                Product product = Product.fromJson(productJson);
                productList.add(product);
            }
        }
        
        return productList;
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
    
    private void setupStatusSpinner() {
        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("pending");
        statusOptions.add("paid");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        
        // Set default to "pending"
        statusSpinner.setSelection(0);
    }
}
