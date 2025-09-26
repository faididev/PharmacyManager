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

        // Get order from arguments
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
        // Set order ID
        android.widget.TextView orderId = getView().findViewById(R.id.order_id);
        orderId.setText("Order #" + order.getId());

        // Set customer name
        android.widget.TextView customerName = getView().findViewById(R.id.order_customer_name);
        String customerNameStr = order.getCustomerName();
        if (customerNameStr != null && !customerNameStr.isEmpty()) {
            customerName.setText(customerNameStr);
        } else {
            customerName.setText("Customer #" + order.getCustomerId());
        }

        // Set order date
        android.widget.TextView orderDate = getView().findViewById(R.id.order_date);
        orderDate.setText(formatDate(order.getOrderDate()));

        // Set order status
        android.widget.TextView orderStatus = getView().findViewById(R.id.order_status);
        orderStatus.setText(order.getStatus().toUpperCase());

        // Set total amount
        android.widget.TextView totalAmount = getView().findViewById(R.id.order_total_amount);
        totalAmount.setText(String.format("$%.2f", order.getTotalAmount()));

        // Set order items
        orderItemAdapter.setOrderItems(order.getItems());
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
            // If parsing fails, return the original string
            return dateString;
        }
    }

    private void setupClickListeners() {
        btnEditOrder.setOnClickListener(v -> {
            // Navigate to EditOrderFragment (not implemented yet)
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
        // Disable button to prevent multiple submissions
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

    // Simple adapter for order items
    private static class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
        private java.util.List<OrderItem> orderItems;

        public OrderItemAdapter() {
            this.orderItems = new java.util.ArrayList<>();
        }

        public void setOrderItems(java.util.List<OrderItem> orderItems) {
            this.orderItems = orderItems != null ? orderItems : new java.util.ArrayList<>();
            notifyDataSetChanged();
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
            OrderItem item = orderItems.get(position);
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
                String productName = item.getProductName();
                if (productName != null && !productName.isEmpty()) {
                    tvProductName.setText(productName);
                } else {
                    tvProductName.setText("Product " + item.getProductId());
                }
                
                tvProductId.setText("Product ID: " + item.getProductId());
                tvQuantity.setText("Qty: " + item.getQuantity());
                tvItemTotal.setText(String.format("$%.2f", item.getTotalPrice()));
            }
        }
    }
}
