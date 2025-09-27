package com.example.pharmacymanager.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onOrderLongClick(Order order);
        void onOrderDeleteClick(Order order);
    }

    public OrderAdapter() {
        this.orders = new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        android.util.Log.d("OrderAdapter", "Setting " + this.orders.size() + " orders to adapter");
        notifyDataSetChanged();
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        notifyItemInserted(orders.size() - 1);
    }

    public void updateOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == order.getId()) {
                orders.set(i, order);
                notifyItemChanged(i);
                android.util.Log.d("OrderAdapter", "Updated order: " + order.toString());
                break;
            }
        }
    }

    public void removeOrder(int orderId) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == orderId) {
                orders.remove(i);
                notifyItemRemoved(i);
                android.util.Log.d("OrderAdapter", "Removed order with ID: " + orderId);
                break;
            }
        }
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_order_list_template, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvCustomerName;
        private TextView tvOrderDate;
        private TextView tvOrderStatus;
        private TextView tvTotalAmount;
        private TextView tvItemCount;
        private ImageButton btnEditOrder;
        private ImageButton btnDeleteOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            btnEditOrder = itemView.findViewById(R.id.btn_edit_order);
            btnDeleteOrder = itemView.findViewById(R.id.btn_delete_order);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(orders.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onOrderLongClick(orders.get(getAdapterPosition()));
                }
                return true;
            });

            btnEditOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(orders.get(getAdapterPosition()));
                }
            });

            btnDeleteOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderDeleteClick(orders.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Order order) {
            tvOrderId.setText("#" + order.getId());
            
            String customerName = order.getCustomerName();
            if (customerName != null && !customerName.isEmpty()) {
                tvCustomerName.setText(customerName);
            } else {
                tvCustomerName.setText("Customer #" + order.getCustomerId());
            }
            
            tvOrderDate.setText(formatDate(order.getOrderDate()));
            tvOrderStatus.setText(order.getStatus().toUpperCase());
            tvTotalAmount.setText(String.format("$%.2f", order.getTotalAmount()));
            tvItemCount.setText(order.getItems().size() + " item(s)");
            
            // Set status color
            int statusColor = getStatusColor(order.getStatus());
            tvOrderStatus.setTextColor(statusColor);
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
        
        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "pending":
                    return itemView.getContext().getResources().getColor(R.color.status_pending, null);
                case "processing":
                    return itemView.getContext().getResources().getColor(R.color.status_processing, null);
                case "completed":
                    return itemView.getContext().getResources().getColor(R.color.status_completed, null);
                case "cancelled":
                    return itemView.getContext().getResources().getColor(R.color.status_cancelled, null);
                default:
                    return itemView.getContext().getResources().getColor(R.color.main_text_color, null);
            }
        }
    }
}
