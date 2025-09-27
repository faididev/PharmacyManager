package com.example.pharmacymanager.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Product;

import java.util.ArrayList;
import java.util.List;

public class InlineProductAdapter extends RecyclerView.Adapter<InlineProductAdapter.ProductViewHolder> {
    
    private List<Product> products;
    private List<Product> filteredProducts;
    private OnProductActionListener listener;
    
    public interface OnProductActionListener {
        void onAddProduct(Product product, int quantity);
        void onUpdateQuantity(Product product, int quantity);
        void onRemoveProduct(Product product);
    }
    
    public InlineProductAdapter(List<Product> products, OnProductActionListener listener) {
        this.products = new ArrayList<>(products);
        this.filteredProducts = new ArrayList<>(products);
        this.listener = listener;
    }
    
    public void updateProducts(List<Product> newProducts) {
        android.util.Log.d("InlineProductAdapter", "=== UPDATE PRODUCTS ===");
        android.util.Log.d("InlineProductAdapter", "New products count: " + (newProducts != null ? newProducts.size() : "null"));
        
        this.products = new ArrayList<>(newProducts);
        this.filteredProducts = new ArrayList<>(newProducts);
        
        android.util.Log.d("InlineProductAdapter", "Products updated - total: " + products.size() + ", filtered: " + filteredProducts.size());
        notifyDataSetChanged();
        android.util.Log.d("InlineProductAdapter", "notifyDataSetChanged() called");
    }
    
    public void filterProducts(String query) {
        filteredProducts.clear();
        
        if (query.isEmpty()) {
            filteredProducts.addAll(products);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Product product : products) {
                if (product.getName().toLowerCase().contains(lowerQuery) ||
                    product.getSku().toLowerCase().contains(lowerQuery) ||
                    product.getDescription().toLowerCase().contains(lowerQuery)) {
                    filteredProducts.add(product);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inline_product, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        android.util.Log.d("InlineProductAdapter", "=== BIND VIEW HOLDER ===");
        android.util.Log.d("InlineProductAdapter", "Position: " + position + ", Filtered products size: " + filteredProducts.size());
        
        Product product = filteredProducts.get(position);
        android.util.Log.d("InlineProductAdapter", "Product: " + product.getName() + " (ID: " + product.getId() + ")");
        holder.bind(product, listener);
    }
    
    @Override
    public int getItemCount() {
        android.util.Log.d("InlineProductAdapter", "=== GET ITEM COUNT ===");
        android.util.Log.d("InlineProductAdapter", "Filtered products size: " + filteredProducts.size());
        return filteredProducts.size();
    }
    
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productSku;
        private TextView productPrice;
        private TextView productQuantity;
        private TextView productDescription;
        private Button btnAdd;
        private Button btnDecrease;
        private Button btnIncrease;
        private TextView tvQuantity;
        
        private Product product;
        private int selectedQuantity = 0;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            productSku = itemView.findViewById(R.id.tv_product_sku);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productQuantity = itemView.findViewById(R.id.tv_product_quantity);
            productDescription = itemView.findViewById(R.id.tv_product_description);
            btnAdd = itemView.findViewById(R.id.btn_add_product);
            btnDecrease = itemView.findViewById(R.id.btn_decrease_qty);
            btnIncrease = itemView.findViewById(R.id.btn_increase_qty);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
        
        public void bind(Product product, OnProductActionListener listener) {
            android.util.Log.d("InlineProductAdapter", "=== BIND PRODUCT ===");
            android.util.Log.d("InlineProductAdapter", "Product: " + product.getName() + ", Price: $" + product.getPrice());
            
            this.product = product;
            
            productName.setText(product.getName());
            productSku.setText("SKU: " + product.getSku());
            productPrice.setText("$" + String.format("%.2f", product.getPrice()));
            productQuantity.setText("Available: " + product.getQuantity());
            productDescription.setText(product.getDescription());
            
            android.util.Log.d("InlineProductAdapter", "Product data set in views");
            
            updateQuantityDisplay();
            
            btnAdd.setOnClickListener(v -> {
                if (selectedQuantity > 0) {
                    if (listener != null) {
                        listener.onAddProduct(product, selectedQuantity);
                    }
                    selectedQuantity = 0;
                    updateQuantityDisplay();
                }
            });
            
            btnDecrease.setOnClickListener(v -> {
                if (selectedQuantity > 0) {
                    selectedQuantity--;
                    updateQuantityDisplay();
                }
            });
            
            btnIncrease.setOnClickListener(v -> {
                if (selectedQuantity < product.getQuantity()) {
                    selectedQuantity++;
                    updateQuantityDisplay();
                }
            });
        }
        
        private void updateQuantityDisplay() {
            tvQuantity.setText(String.valueOf(selectedQuantity));
            btnAdd.setEnabled(selectedQuantity > 0);
            btnDecrease.setEnabled(selectedQuantity > 0);
            btnIncrease.setEnabled(selectedQuantity < product.getQuantity());
        }
    }
}
