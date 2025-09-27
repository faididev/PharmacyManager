package com.example.pharmacymanager.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Product;

import java.util.List;

public class ProductSelectionAdapter extends RecyclerView.Adapter<ProductSelectionAdapter.ProductViewHolder> {
    
    private List<Product> products;
    private OnProductClickListener listener;
    
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    
    public ProductSelectionAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_selection, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener);
    }
    
    @Override
    public int getItemCount() {
        return products.size();
    }
    
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productSku;
        private TextView productPrice;
        private TextView productQuantity;
        private TextView productDescription;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            productSku = itemView.findViewById(R.id.tv_product_sku);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productQuantity = itemView.findViewById(R.id.tv_product_quantity);
            productDescription = itemView.findViewById(R.id.tv_product_description);
        }
        
        public void bind(Product product, OnProductClickListener listener) {
            productName.setText(product.getName());
            productSku.setText("SKU: " + product.getSku());
            productPrice.setText("$" + String.format("%.2f", product.getPrice()));
            productQuantity.setText("Available: " + product.getQuantity());
            productDescription.setText(product.getDescription());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}
