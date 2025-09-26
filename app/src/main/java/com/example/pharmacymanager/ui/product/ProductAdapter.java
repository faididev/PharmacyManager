package com.example.pharmacymanager.ui.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onProductLongClick(Product product);
        void onProductDeleteClick(Product product);
    }

    public ProductAdapter() {
        this.products = new ArrayList<>();
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        android.util.Log.d("ProductAdapter", "Setting " + this.products.size() + " products to adapter");
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        this.products.add(product);
        notifyItemInserted(products.size() - 1);
    }

    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getUuid().equals(product.getUuid())) {
                products.set(i, product);
                notifyItemChanged(i);
                android.util.Log.d("ProductAdapter", "Updated product: " + product.getName());
                break;
            }
        }
    }

    public void removeProduct(String uuid) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getUuid().equals(uuid)) {
                products.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_product_list_template, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        android.util.Log.d("ProductAdapter", "getItemCount() called, returning: " + products.size());
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productSku;
        private TextView productDescription;
        private TextView productPrice;
        private TextView productQuantity;
        private TextView productExpiryDate;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productSku = itemView.findViewById(R.id.product_sku);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productExpiryDate = itemView.findViewById(R.id.product_expiry_date);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            // Set click listeners for action buttons
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductLongClick(products.get(position)); // Use edit functionality
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductDeleteClick(products.get(position));
                    }
                }
            });

            // Set click listeners for item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductClick(products.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Provide visual feedback
                        v.setAlpha(0.7f);
                        v.postDelayed(() -> v.setAlpha(1.0f), 150);
                        
                        listener.onProductLongClick(products.get(position));
                        return true;
                    }
                }
                return false;
            });
        }

        public void bind(Product product) {
            android.util.Log.d("ProductAdapter", "Binding product: " + product.getName());
            productName.setText(product.getName());
            
            
            // Handle SKU
            String sku = product.getSku();
            if (sku == null || sku.trim().isEmpty()) {
                productSku.setText("SKU: N/A");
            } else {
                productSku.setText("SKU: " + sku);
            }
            
            // Handle description
            String description = product.getDescription();
            if (description == null || description.trim().isEmpty()) {
                productDescription.setText("No description available");
            } else {
                productDescription.setText(description);
            }
            
            // Format price
            productPrice.setText(String.format("$%.2f", product.getPrice()));
            
            // Show quantity
            productQuantity.setText("Qty: " + product.getQuantity());
            
            // Format expiry date
            if (product.getExpiryDate() != null && !product.getExpiryDate().trim().isEmpty()) {
                String date = product.getExpiryDate().split("T")[0];
                productExpiryDate.setText("Expires: " + date);
            } else {
                productExpiryDate.setText("Expiry: N/A");
            }
        }
    }
}
