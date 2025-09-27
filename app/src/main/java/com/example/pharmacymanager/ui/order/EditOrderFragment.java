package com.example.pharmacymanager.ui.order;

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
import com.example.pharmacymanager.data.entities.Product;
import com.example.pharmacymanager.data.repositories.CustomerRepository;
import com.example.pharmacymanager.data.repositories.OrderRepository;
import com.example.pharmacymanager.data.repositories.ProductRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditOrderFragment extends Fragment {

    private Order order;
    private Spinner customerSpinner;
    private Spinner statusSpinner;
    
    private Button updateOrderBtn;
    private Button btnBack;
    // btnAddItem removed - using inline product selection now
    private Button btnRefreshCustomers;
    
    private LinearLayout orderItemsContainer;
    private TextView totalAmountText;
    private List<OrderItem> orderItems;
    private List<Customer> customers;
    private List<Product> products;
    private int selectedCustomerId = -1;
    
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

    public EditOrderFragment() {
        // Required empty public constructor
    }

    public static EditOrderFragment newInstance(Order order) {
        EditOrderFragment fragment = new EditOrderFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("EditOrderFragment", "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_add_order, container, false);

        // Initialize views
        initializeViews(view);

        // Initialize repositories
        orderRepository = new OrderRepository(requireContext());
        customerRepository = new CustomerRepository(requireContext());
        productRepository = new ProductRepository(requireContext());
        
        Log.d("EditOrderFragment", "Repositories initialized");

        // Initialize lists
        orderItems = new ArrayList<>();
        customers = new ArrayList<>();
        products = new ArrayList<>();
        
        Log.d("EditOrderFragment", "Lists initialized");

        // Setup click listeners
        setupClickListeners();
        
        // Load existing order data
        loadOrderData();

        return view;
    }

    private void initializeViews(View view) {
        try {
            customerSpinner = view.findViewById(R.id.customer_spinner);
            statusSpinner = view.findViewById(R.id.status_spinner);
            
            updateOrderBtn = view.findViewById(R.id.create_order_btn);
            btnBack = view.findViewById(R.id.btnBack);
            // btnAddItem removed - using inline product selection now
            btnRefreshCustomers = view.findViewById(R.id.btn_refresh_customers);
            
            orderItemsContainer = view.findViewById(R.id.order_items_container);
            totalAmountText = view.findViewById(R.id.total_amount_text);
            
            // Change button text for editing
            updateOrderBtn.setText("Update Order");
            
            Log.d("EditOrderFragment", "Views initialized successfully");
        } catch (Exception e) {
            Log.e("EditOrderFragment", "Error initializing views: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error initializing form: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        try {
            if (updateOrderBtn != null) {
                updateOrderBtn.setOnClickListener(v -> updateOrder());
            }
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                });
            }
            // btnAddItem click listener removed - using inline product selection now
            
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
                            Log.d("EditOrderFragment", "Customer selected: " + customers.get(position - 1).getName());
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
            
            // Load customers and products
            loadCustomers();
            loadProducts();
            
            Log.d("EditOrderFragment", "Click listeners setup successfully");
        } catch (Exception e) {
            Log.e("EditOrderFragment", "Error setting up click listeners: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error setting up form: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadOrderData() {
        if (order == null) {
            Toast.makeText(requireContext(), "No order data to load", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Set customer and status
        selectedCustomerId = order.getCustomerId();
        
        // Load order items
        if (order.getItems() != null) {
            orderItems.addAll(order.getItems());
            displayOrderItems();
            updateOrderTotal();
        }
    }

    private void displayOrderItems() {
        orderItemsContainer.removeAllViews();
        
        for (OrderItem item : orderItems) {
            View itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.rv_order_item_template, orderItemsContainer, false);
            
            TextView productName = itemView.findViewById(R.id.tv_product_name);
            TextView productId = itemView.findViewById(R.id.tv_product_id);
            TextView unitPrice = itemView.findViewById(R.id.tv_unit_price);
            TextView quantityText = itemView.findViewById(R.id.tv_quantity);
            TextView itemTotal = itemView.findViewById(R.id.tv_item_total);
            
            productName.setText(item.getProductName());
            productId.setText("Product ID: " + item.getProductId());
            unitPrice.setText("Price: $" + String.format("%.2f", item.getPrice()));
            quantityText.setText(String.valueOf(item.getQuantity()));
            itemTotal.setText("$" + String.format("%.2f", item.getTotalPrice()));
            
            // Set up remove button
            Button removeBtn = itemView.findViewById(R.id.btn_remove_item_1);
            removeBtn.setOnClickListener(v -> {
                orderItemsContainer.removeView(itemView);
                orderItems.remove(item);
                updateOrderTotal();
            });
            
            // Set up quantity controls
            Button decreaseBtn = itemView.findViewById(R.id.btn_decrease_qty);
            Button increaseBtn = itemView.findViewById(R.id.btn_increase_qty);
            
            decreaseBtn.setOnClickListener(v -> {
                int currentQty = item.getQuantity();
                if (currentQty > 1) {
                    item.setQuantity(currentQty - 1);
                    quantityText.setText(String.valueOf(item.getQuantity()));
                    itemTotal.setText("$" + String.format("%.2f", item.getTotalPrice()));
                    updateOrderTotal();
                }
            });
            
            increaseBtn.setOnClickListener(v -> {
                int currentQty = item.getQuantity();
                item.setQuantity(currentQty + 1);
                quantityText.setText(String.valueOf(item.getQuantity()));
                itemTotal.setText("$" + String.format("%.2f", item.getTotalPrice()));
                updateOrderTotal();
            });
            
            orderItemsContainer.addView(itemView);
        }
    }

    // showProductSelectionDialog method removed - using inline product selection now

    private void addOrderItem(Product product, int quantity) {
        try {
            Log.d("EditOrderFragment", "Adding order item: " + product.getName() + " x" + quantity);
            
            // Create order item
            OrderItem orderItem = new OrderItem(
                product.getUuid(),
                quantity,
                product.getPrice(),
                product.getName()
            );
            
            // Add to list
            orderItems.add(orderItem);
            
            // Create view for the order item
            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.rv_order_item_template, orderItemsContainer, false);
            
            // Set product information
            TextView productName = itemView.findViewById(R.id.tv_product_name);
            TextView productId = itemView.findViewById(R.id.tv_product_id);
            TextView unitPrice = itemView.findViewById(R.id.tv_unit_price);
            TextView quantityText = itemView.findViewById(R.id.tv_quantity);
            TextView itemTotal = itemView.findViewById(R.id.tv_item_total);
            
            productName.setText(product.getName());
            productId.setText("Product ID: " + product.getUuid());
            unitPrice.setText("Price: $" + String.format("%.2f", product.getPrice()));
            quantityText.setText(String.valueOf(quantity));
            itemTotal.setText("$" + String.format("%.2f", orderItem.getTotalPrice()));
            
            // Set up remove button
            Button removeBtn = itemView.findViewById(R.id.btn_remove_item_1);
            removeBtn.setOnClickListener(v -> {
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
            
            // Update order total
            updateOrderTotal();
            
            Log.d("EditOrderFragment", "Added order item: " + product.getName() + " x" + quantity);
        } catch (Exception e) {
            Log.e("EditOrderFragment", "Error adding order item: " + e.getMessage());
            Toast.makeText(requireContext(), "Error adding item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOrderTotal() {
        double total = 0.0;
        for (OrderItem item : orderItems) {
            total += item.getTotalPrice();
        }
        
        // Update total display
        if (totalAmountText != null) {
            totalAmountText.setText("$" + String.format("%.2f", total));
        }
        
        Log.d("EditOrderFragment", "Order total updated: $" + String.format("%.2f", total));
    }

    private void updateOrder() {
        try {
            Log.d("EditOrderFragment", "updateOrder() called");
            
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
            
            Log.d("EditOrderFragment", "Updating order - ID: " + order.getId() + ", Customer ID: " + selectedCustomerId + ", Status: " + status + ", Items: " + orderItems.size());
            
            // Disable button to prevent multiple submissions
            updateOrderBtn.setEnabled(false);
            updateOrderBtn.setText("Updating...");
            
            orderRepository.updateOrder(order.getId(), selectedCustomerId, orderDate, status, orderItems,
                    new OrderRepository.OrderCallback() {
                        @Override
                        public void onSuccess(Order updatedOrder) {
                            Log.d("EditOrderFragment", "Order updated successfully");
                            updateOrderBtn.setEnabled(true);
                            updateOrderBtn.setText("Update Order");
                            
                            Toast.makeText(requireContext(), "Order updated successfully", Toast.LENGTH_SHORT).show();
                            
                            // Navigate back to orders list
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }

                        @Override
                        public void onError(String message) {
                            Log.e("EditOrderFragment", "Error updating order: " + message);
                            updateOrderBtn.setEnabled(true);
                            updateOrderBtn.setText("Update Order");
                            Toast.makeText(requireContext(), "Error updating order: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("EditOrderFragment", "Error in updateOrder: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error updating order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Re-enable button
            if (updateOrderBtn != null) {
                updateOrderBtn.setEnabled(true);
                updateOrderBtn.setText("Update Order");
            }
        }
    }

    private void loadCustomers() {
        try {
            if (customerRepository == null) {
                Log.e("EditOrderFragment", "CustomerRepository is null");
                Toast.makeText(requireContext(), "Error: Customer repository not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d("EditOrderFragment", "Loading customers...");
            customerRepository.getCustomers(new CustomerRepository.CustomerListCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        Log.d("EditOrderFragment", "Customer response: " + response.toString());
                        customers = parseCustomersFromResponse(response);
                        setupCustomerSpinner();
                        Log.d("EditOrderFragment", "Customers loaded successfully: " + customers.size());
                        if (customers.isEmpty()) {
                            Toast.makeText(requireContext(), "No customers found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("EditOrderFragment", "Error parsing customers: " + e.getMessage(), e);
                        Toast.makeText(requireContext(), "Error parsing customers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e("EditOrderFragment", "Error loading customers: " + message);
                    Toast.makeText(requireContext(), "Error loading customers: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("EditOrderFragment", "Error in loadCustomers: " + e.getMessage(), e);
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
        
        int selectedIndex = 0;
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            customerNames.add(customer.getName());
            if (customer.getId() == selectedCustomerId) {
                selectedIndex = i + 1; // +1 because of the "Select Customer" option
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, customerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerSpinner.setAdapter(adapter);
        
        // Set the selected customer
        if (selectedIndex > 0) {
            customerSpinner.setSelection(selectedIndex);
        }
    }

    private void loadProducts() {
        try {
            if (productRepository == null) {
                Log.e("EditOrderFragment", "ProductRepository is null");
                Toast.makeText(requireContext(), "Error: Product repository not initialized", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d("EditOrderFragment", "Loading products...");
            productRepository.getProducts(new ProductRepository.ProductListCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        Log.d("EditOrderFragment", "Product response: " + response.toString());
                        products = parseProductsFromResponse(response);
                        Log.d("EditOrderFragment", "Products loaded successfully: " + products.size());
                        if (products.isEmpty()) {
                            Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("EditOrderFragment", "Error parsing products: " + e.getMessage(), e);
                        Toast.makeText(requireContext(), "Error parsing products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e("EditOrderFragment", "Error loading products: " + message);
                    Toast.makeText(requireContext(), "Error loading products: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("EditOrderFragment", "Error in loadProducts: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    
    private void setupStatusSpinner() {
        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("pending");
        statusOptions.add("paid");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        
        // Set current status
        String currentStatus = order.getStatus();
        if (currentStatus != null) {
            int index = statusOptions.indexOf(currentStatus);
            if (index >= 0) {
                statusSpinner.setSelection(index);
            } else {
                statusSpinner.setSelection(0); // Default to "pending"
            }
        } else {
            statusSpinner.setSelection(0); // Default to "pending"
        }
    }
}
