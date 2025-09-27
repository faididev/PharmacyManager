package com.example.pharmacymanager.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customers;
    private OnCustomerClickListener listener;

    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
        void onCustomerLongClick(Customer customer);
        void onCustomerDeleteClick(Customer customer);
    }

    public CustomerAdapter() {
        this.customers = new ArrayList<>();
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers != null ? customers : new ArrayList<>();
        android.util.Log.d("CustomerAdapter", "Setting " + this.customers.size() + " customers to adapter");
        notifyDataSetChanged();
    }

    public void addCustomer(Customer customer) {
        this.customers.add(customer);
        notifyItemInserted(customers.size() - 1);
    }

    public void updateCustomer(Customer customer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == customer.getId()) {
                customers.set(i, customer);
                notifyItemChanged(i);
                android.util.Log.d("CustomerAdapter", "Updated customer: " + customer.getName());
                break;
            }
        }
    }

    public void removeCustomer(int customerId) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == customerId) {
                customers.remove(i);
                notifyItemRemoved(i);
                android.util.Log.d("CustomerAdapter", "Removed customer with ID: " + customerId);
                break;
            }
        }
    }

    public void setOnCustomerClickListener(OnCustomerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_customer_list_template, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName;
        private TextView tvCustomerEmail;
        private TextView tvCustomerPhone;
        private TextView tvCustomerLoyaltyPoints;
        private ImageButton btnEditCustomer;
        private ImageButton btnDeleteCustomer;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvCustomerEmail = itemView.findViewById(R.id.tv_customer_email);
            tvCustomerPhone = itemView.findViewById(R.id.tv_customer_phone);
            tvCustomerLoyaltyPoints = itemView.findViewById(R.id.tv_customer_loyalty_points);
            btnEditCustomer = itemView.findViewById(R.id.btn_edit_customer);
            btnDeleteCustomer = itemView.findViewById(R.id.btn_delete_customer);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerClick(customers.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerLongClick(customers.get(getAdapterPosition()));
                }
                return true;
            });

            btnEditCustomer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerClick(customers.get(getAdapterPosition()));
                }
            });

            btnDeleteCustomer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerDeleteClick(customers.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Customer customer) {
            tvCustomerName.setText(customer.getName());
            tvCustomerEmail.setText(customer.getEmail());
            tvCustomerPhone.setText(customer.getPhone());
            tvCustomerLoyaltyPoints.setText(String.valueOf(customer.getLoyaltyPoints()));
        }
    }
}
