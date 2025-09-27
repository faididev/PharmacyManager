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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public enum SortType {
        NONE,
        QUANTITY_ASC,
        QUANTITY_DESC,
        NAME_ASC,
        NAME_DESC,
        PRICE_ASC,
        PRICE_DESC
    }

    private List<Product> products;
    private List<Product> filteredProducts;
    private OnProductClickListener listener;
    private String currentSearchQuery = "";
    private SortType currentSortType = SortType.NONE;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onProductLongClick(Product product);
        void onProductDeleteClick(Product product);
    }

    public ProductAdapter() {
        this.products = new ArrayList<>();
        this.filteredProducts = new ArrayList<>();
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        android.util.Log.d("ProductAdapter", "Setting " + this.products.size() + " products to adapter");
        applyFiltersAndSort();
    }

    public void filterProducts(String query) {
        currentSearchQuery = query != null ? query.toLowerCase().trim() : "";
        applyFiltersAndSort();
    }

    public void sortProducts(SortType sortType) {
        currentSortType = sortType;
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        filteredProducts.clear();
        
        // Apply search filter
        for (Product product : products) {
            if (currentSearchQuery.isEmpty() || 
                product.getName().toLowerCase().contains(currentSearchQuery) ||
                (product.getSku() != null && product.getSku().toLowerCase().contains(currentSearchQuery)) ||
                (product.getDescription() != null && product.getDescription().toLowerCase().contains(currentSearchQuery))) {
                filteredProducts.add(product);
            }
        }
        
        // Apply sorting
        switch (currentSortType) {
            case QUANTITY_ASC:
                Collections.sort(filteredProducts, (p1, p2) -> Integer.compare(p1.getQuantity(), p2.getQuantity()));
                break;
            case QUANTITY_DESC:
                Collections.sort(filteredProducts, (p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity()));
                break;
            case NAME_ASC:
                Collections.sort(filteredProducts, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case NAME_DESC:
                Collections.sort(filteredProducts, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                break;
            case PRICE_ASC:
                Collections.sort(filteredProducts, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case PRICE_DESC:
                Collections.sort(filteredProducts, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case NONE:
            default:
                // No sorting
                break;
        }
        
        android.util.Log.d("ProductAdapter", "Filtered and sorted products: " + filteredProducts.size());
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        this.products.add(product);
        applyFiltersAndSort();
    }

    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getUuid().equals(product.getUuid())) {
                products.set(i, product);
                android.util.Log.d("ProductAdapter", "Updated product: " + product.getName());
                break;
            }
        }
        applyFiltersAndSort();
    }

    public void removeProduct(String uuid) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getUuid().equals(uuid)) {
                products.remove(i);
                break;
            }
        }
        applyFiltersAndSort();
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
        Product product = filteredProducts.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        android.util.Log.d("ProductAdapter", "getItemCount() called, returning: " + filteredProducts.size());
        return filteredProducts.size();
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
                        listener.onProductLongClick(filteredProducts.get(position)); // Use edit functionality
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductDeleteClick(filteredProducts.get(position));
                    }
                }
            });

            // Set click listeners for item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onProductClick(filteredProducts.get(position));
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
                        
                        listener.onProductLongClick(filteredProducts.get(position));
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
