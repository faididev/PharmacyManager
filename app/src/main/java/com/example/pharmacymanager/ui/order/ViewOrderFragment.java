package com.example.pharmacymanager.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Order;
import com.example.pharmacymanager.data.entities.OrderItem;
import com.example.pharmacymanager.data.repositories.OrderRepository;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewOrderFragment extends Fragment {

    private Order order;
    
    private MaterialButton btnEditOrder;
    private MaterialButton btnDeleteOrder;
    private RecyclerView orderItemsRecycler;
    private OrderItemAdapter orderItemAdapter;
    
    private OrderRepository orderRepository;

    public ViewOrderFragment() {
        // Required empty public constructor
    }

    public static ViewOrderFragment newInstance(Order order) {
        ViewOrderFragment fragment = new ViewOrderFragment();
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_order, container, false);

        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable("order");
        }

        if (order == null) {
            Toast.makeText(requireContext(), "Order not found", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return view;
        }

        // Initialize views
        initializeViews(view);

        // Initialize repository
        orderRepository = new OrderRepository(requireContext());

        // Populate fields with order data
        populateFields();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        btnEditOrder = view.findViewById(R.id.btn_edit_order);
        btnDeleteOrder = view.findViewById(R.id.btn_delete_order);
        orderItemsRecycler = view.findViewById(R.id.order_items_recycler);
        
        // Setup order items recycler
        orderItemAdapter = new OrderItemAdapter();
        orderItemsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderItemsRecycler.setAdapter(orderItemAdapter);
    }

    private void populateFields() {
        try {
            android.util.Log.d("ViewOrderFragment", "=== POPULATING FIELDS ===");
            android.util.Log.d("ViewOrderFragment", "Order: " + order.toString());
            
            View view = getView();
            if (view == null) {
                android.util.Log.e("ViewOrderFragment", "View is null in populateFields!");
                return;
            }
            
            // Set order ID
            android.widget.TextView orderId = view.findViewById(R.id.order_id);
            if (orderId != null) {
                orderId.setText("Order #" + order.getId());
                android.util.Log.d("ViewOrderFragment", "Order ID set: " + order.getId());
            } else {
                android.util.Log.e("ViewOrderFragment", "order_id TextView not found!");
            }

            // Set customer name
            android.widget.TextView customerName = view.findViewById(R.id.order_customer_name);
            if (customerName != null) {
                String customerNameStr = order.getCustomerName();
                if (customerNameStr != null && !customerNameStr.isEmpty()) {
                    customerName.setText(customerNameStr);
                    android.util.Log.d("ViewOrderFragment", "Customer name set: " + customerNameStr);
                } else {
                    customerName.setText("Customer #" + order.getCustomerId());
                    android.util.Log.d("ViewOrderFragment", "Customer ID set: " + order.getCustomerId());
                }
            } else {
                android.util.Log.e("ViewOrderFragment", "order_customer_name TextView not found!");
            }

            // Set order date
            android.widget.TextView orderDate = view.findViewById(R.id.order_date);
            if (orderDate != null) {
                String formattedDate = formatDate(order.getOrderDate());
                orderDate.setText(formattedDate);
                android.util.Log.d("ViewOrderFragment", "Order date set: " + formattedDate);
            } else {
                android.util.Log.e("ViewOrderFragment", "order_date TextView not found!");
            }

            // Set order status
            android.widget.TextView orderStatus = view.findViewById(R.id.order_status);
            if (orderStatus != null) {
                String status = order.getStatus();
                if (status != null) {
                    orderStatus.setText(status.toUpperCase());
                    android.util.Log.d("ViewOrderFragment", "Order status set: " + status);
                } else {
                    orderStatus.setText("UNKNOWN");
                    android.util.Log.w("ViewOrderFragment", "Order status is null!");
                }
            } else {
                android.util.Log.e("ViewOrderFragment", "order_status TextView not found!");
            }

            // Set total amount
            android.widget.TextView totalAmount = view.findViewById(R.id.order_total_amount);
            if (totalAmount != null) {
                totalAmount.setText(String.format("$%.2f", order.getTotalAmount()));
                android.util.Log.d("ViewOrderFragment", "Total amount set: $" + order.getTotalAmount());
            } else {
                android.util.Log.e("ViewOrderFragment", "order_total_amount TextView not found!");
            }

            // Set order items
            if (orderItemAdapter != null) {
                java.util.List<OrderItem> items = order.getItems();
                android.util.Log.d("ViewOrderFragment", "=== ORDER ITEMS DEBUG ===");
                android.util.Log.d("ViewOrderFragment", "Order items from order: " + (items != null ? items.size() : "null"));
                if (items != null) {
                    for (int i = 0; i < items.size(); i++) {
                        OrderItem item = items.get(i);
                        android.util.Log.d("ViewOrderFragment", "Item " + i + ": " + item.toString());
                    }
                    orderItemAdapter.setOrderItems(items);
                    android.util.Log.d("ViewOrderFragment", "Order items set: " + items.size() + " items");
                } else {
                    orderItemAdapter.setOrderItems(new java.util.ArrayList<>());
                    android.util.Log.w("ViewOrderFragment", "Order items is null, setting empty list");
                }
            } else {
                android.util.Log.e("ViewOrderFragment", "OrderItemAdapter is null!");
            }
            
            android.util.Log.d("ViewOrderFragment", "Fields populated successfully");
        } catch (Exception e) {
            android.util.Log.e("ViewOrderFragment", "Error populating fields: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error loading order details: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "No date";
        }
        
        try {
            // Assuming the date comes in ISO format from the API
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }

    private void setupClickListeners() {
        btnEditOrder.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit order functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        btnDeleteOrder.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete " + order.toString() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteOrder())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteOrder() {
        btnDeleteOrder.setEnabled(false);
        btnDeleteOrder.setText("Deleting...");

        orderRepository.deleteOrder(order.getId(), new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order deletedOrder) {
                btnDeleteOrder.setEnabled(true);
                btnDeleteOrder.setText("Delete Order");
                
                Toast.makeText(requireContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                
                // Navigate back to list
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            @Override
            public void onError(String message) {
                btnDeleteOrder.setEnabled(true);
                btnDeleteOrder.setText("Delete Order");
                
                Toast.makeText(requireContext(), "Error deleting order: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // adapter for order items
    private static class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
        private java.util.List<OrderItem> orderItems;

        public OrderItemAdapter() {
            this.orderItems = new java.util.ArrayList<>();
        }

        public void setOrderItems(java.util.List<OrderItem> orderItems) {
            android.util.Log.d("ViewOrderFragment", "=== SET ORDER ITEMS ===");
            android.util.Log.d("ViewOrderFragment", "Received order items: " + (orderItems != null ? orderItems.size() : "null"));
            this.orderItems = orderItems != null ? orderItems : new java.util.ArrayList<>();
            android.util.Log.d("ViewOrderFragment", "Order items set in adapter: " + this.orderItems.size());
            notifyDataSetChanged();
            android.util.Log.d("ViewOrderFragment", "notifyDataSetChanged() called");
        }

        @NonNull
        @Override
        public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_order_item_template, parent, false);
            return new OrderItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
            android.util.Log.d("ViewOrderFragment", "=== BIND VIEW HOLDER ===");
            android.util.Log.d("ViewOrderFragment", "Position: " + position + ", Order items size: " + orderItems.size());
            OrderItem item = orderItems.get(position);
            android.util.Log.d("ViewOrderFragment", "Binding item: " + item.toString());
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return orderItems.size();
        }

        static class OrderItemViewHolder extends RecyclerView.ViewHolder {
            private android.widget.TextView tvProductName;
            private android.widget.TextView tvProductId;
            private android.widget.TextView tvQuantity;
            private android.widget.TextView tvItemTotal;

            public OrderItemViewHolder(@NonNull View itemView) {
                super(itemView);
                tvProductName = itemView.findViewById(R.id.tv_product_name);
                tvProductId = itemView.findViewById(R.id.tv_product_id);
                tvQuantity = itemView.findViewById(R.id.tv_quantity);
                tvItemTotal = itemView.findViewById(R.id.tv_item_total);
            }

            public void bind(OrderItem item) {
                try {
                    android.util.Log.d("ViewOrderFragment", "=== BINDING ORDER ITEM ===");
                    android.util.Log.d("ViewOrderFragment", "Item: " + item.toString());
                    
                    String productName = item.getProductName();
                    if (productName != null && !productName.isEmpty()) {
                        tvProductName.setText(productName);
                        android.util.Log.d("ViewOrderFragment", "Product name set: " + productName);
                    } else {
                        tvProductName.setText("Product " + item.getProductId());
                        android.util.Log.d("ViewOrderFragment", "Product ID set: " + item.getProductId());
                    }
                    
                    tvProductId.setText("Product ID: " + item.getProductId());
                    tvQuantity.setText("Qty: " + item.getQuantity());
                    tvItemTotal.setText(String.format("$%.2f", item.getTotalPrice()));
                    
                    android.util.Log.d("ViewOrderFragment", "Order item bound successfully");
                } catch (Exception e) {
                    android.util.Log.e("ViewOrderFragment", "Error binding order item: " + e.getMessage(), e);
                    // Set fallback values
                    tvProductName.setText("Unknown Product");
                    tvProductId.setText("Product ID: N/A");
                    tvQuantity.setText("Qty: 0");
                    tvItemTotal.setText("$0.00");
                }
            }
        }
    }
}
