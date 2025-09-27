package com.example.pharmacymanager.ui.order;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductSelectionDialog extends DialogFragment {
    
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    private ProductSelectionAdapter adapter;
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private Button cancelButton;
    
    private ProductSelectionCallback callback;
    
    public interface ProductSelectionCallback {
        void onProductSelected(Product product, int quantity);
    }
    
    public ProductSelectionDialog(List<Product> products, ProductSelectionCallback callback) {
        Log.d("ProductSelectionDialog", "=== CONSTRUCTOR CALLED ===");
        Log.d("ProductSelectionDialog", "Products count: " + (products != null ? products.size() : "null"));
        Log.d("ProductSelectionDialog", "Callback is null: " + (callback == null));
        
        this.allProducts = new ArrayList<>(products);
        this.filteredProducts = new ArrayList<>(products);
        this.callback = callback;
        
        Log.d("ProductSelectionDialog", "Constructor completed");
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ProductSelectionDialog", "=== onCreate CALLED ===");
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        Log.d("ProductSelectionDialog", "Style set to FullScreenDialogStyle");
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ProductSelectionDialog", "=== onCreateView CALLED ===");
        View view = inflater.inflate(R.layout.dialog_product_selection, container, false);
        Log.d("ProductSelectionDialog", "Layout inflated successfully");
        
        initializeViews(view);
        Log.d("ProductSelectionDialog", "Views initialized");
        setupRecyclerView();
        Log.d("ProductSelectionDialog", "RecyclerView setup");
        setupSearch();
        Log.d("ProductSelectionDialog", "Search setup");
        setupClickListeners();
        Log.d("ProductSelectionDialog", "Click listeners setup");
        
        Log.d("ProductSelectionDialog", "onCreateView completed");
        return view;
    }
    
    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.et_search_products);
        recyclerView = view.findViewById(R.id.rv_products);
        cancelButton = view.findViewById(R.id.btn_cancel);
    }
    
    private void setupRecyclerView() {
        adapter = new ProductSelectionAdapter(filteredProducts, new ProductSelectionAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                showQuantityDialog(product);
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupClickListeners() {
        cancelButton.setOnClickListener(v -> dismiss());
    }
    
    private void filterProducts(String query) {
        filteredProducts.clear();
        
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Product product : allProducts) {
                if (product.getName().toLowerCase().contains(lowerQuery) ||
                    product.getSku().toLowerCase().contains(lowerQuery) ||
                    product.getDescription().toLowerCase().contains(lowerQuery)) {
                    filteredProducts.add(product);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }
    
    private void showQuantityDialog(Product product) {
        Log.d("ProductSelectionDialog", "=== SHOWING QUANTITY DIALOG ===");
        Log.d("ProductSelectionDialog", "Product: " + product.getName());
        Log.d("ProductSelectionDialog", "Callback is null: " + (callback == null));
        
        QuantityInputDialog quantityDialog = new QuantityInputDialog(product, new QuantityInputDialog.QuantityCallback() {
            @Override
            public void onQuantitySelected(Product product, int quantity) {
                Log.d("ProductSelectionDialog", "=== QUANTITY SELECTED ===");
                Log.d("ProductSelectionDialog", "Product: " + product.getName());
                Log.d("ProductSelectionDialog", "Quantity: " + quantity);
                Log.d("ProductSelectionDialog", "Callback is null: " + (callback == null));
                
                if (callback != null) {
                    Log.d("ProductSelectionDialog", "Calling callback...");
                    callback.onProductSelected(product, quantity);
                    Log.d("ProductSelectionDialog", "Callback called successfully");
                } else {
                    Log.e("ProductSelectionDialog", "Callback is null, cannot notify parent!");
                }
                dismiss();
            }
        });
        quantityDialog.show(getParentFragmentManager(), "QuantityInputDialog");
    }
}
