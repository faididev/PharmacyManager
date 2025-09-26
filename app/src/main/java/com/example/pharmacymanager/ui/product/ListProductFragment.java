package com.example.pharmacymanager.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Product;
import com.example.pharmacymanager.data.repositories.ProductRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private FloatingActionButton fabAddProduct;
    private ProductRepository productRepository;

    public ListProductFragment() {
        // Required empty public constructor
    }

    public static ListProductFragment newInstance(String param1, String param2) {
        ListProductFragment fragment = new ListProductFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_product, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        // Initialize repository
        productRepository = new ProductRepository(requireContext());

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Load products
        loadProducts();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Set click listener for product items
        adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Navigate to view product fragment
                ViewProductFragment viewFragment = ViewProductFragment.newInstance(product);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragement_container, viewFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onProductLongClick(Product product) {
                // Handle long click - navigate to edit fragment
                EditProductFragment editFragment = EditProductFragment.newInstance(product);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragement_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onProductDeleteClick(Product product) {
                // Handle delete button click
                showDeleteConfirmationDialog(product);
            }
        });
    }

    private void setupClickListeners() {
        // Floating action button to add new product
        fabAddProduct.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_container, new AddProductFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void loadProducts() {
        showLoading(true);
        
        productRepository.getProducts(new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                showLoading(false);
                
                android.util.Log.d("ListProductFragment", "Received response: " + response.toString());
                
                try {
                    List<Product> products = parseProductsFromResponse(response);
                    android.util.Log.d("ListProductFragment", "Setting " + products.size() + " products to adapter");
                    adapter.setProducts(products);
                    
                    if (products.isEmpty()) {
                        android.util.Log.d("ListProductFragment", "No products found, showing empty state");
                        showEmptyState(true);
                    } else {
                        android.util.Log.d("ListProductFragment", "Products loaded successfully, hiding empty state");
                        showEmptyState(false);
                    }
                } catch (JSONException e) {
                    android.util.Log.e("ListProductFragment", "Error parsing products: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Error parsing products: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error loading products: " + message, Toast.LENGTH_LONG).show();
                showEmptyState(true);
            }
        });
    }

    private List<Product> parseProductsFromResponse(JSONObject response) throws JSONException {
        List<Product> products = new ArrayList<>();
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            android.util.Log.d("ListProductFragment", "Found " + dataArray.length() + " products in response");
            
            // Log the structure of the first few products for debugging
            for (int debugIndex = 0; debugIndex < Math.min(3, dataArray.length()); debugIndex++) {
                try {
                    JSONObject debugJson = dataArray.getJSONObject(debugIndex);
                    android.util.Log.d("ListProductFragment", "DEBUG - Product " + debugIndex + " structure: " + debugJson.toString());
                } catch (Exception e) {
                    android.util.Log.e("ListProductFragment", "DEBUG - Error reading product " + debugIndex, e);
                }
            }
            
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    JSONObject productJson = dataArray.getJSONObject(i);
                    android.util.Log.d("ListProductFragment", "Parsing product " + i + ": " + productJson.toString());
                    Product product = Product.fromJsonSafe(productJson);
                    products.add(product);
                    android.util.Log.d("ListProductFragment", "Successfully parsed product: " + product.getName());
                } catch (Exception e) {
                    android.util.Log.e("ListProductFragment", "Error parsing product " + i + ": " + e.getMessage(), e);
                    // Continue with next product instead of failing completely
                }
            }
        } else {
            android.util.Log.e("ListProductFragment", "No 'data' field found in response: " + response.toString());
        }
        
        android.util.Log.d("ListProductFragment", "Total products parsed: " + products.size());
        return products;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean show) {
        emptyStateText.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    // Method to refresh the list (can be called from AddProductFragment)
    public void refreshProducts() {
        loadProducts();
    }

    // Method to update a specific product in the list
    public void updateProductInList(Product updatedProduct) {
        adapter.updateProduct(updatedProduct);
    }

    private void showDeleteConfirmationDialog(Product product) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete '" + product.getName() + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteProduct(product);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void deleteProduct(Product product) {
        android.util.Log.d("ListProductFragment", "Starting delete for product: " + product.getName() + " (UUID: " + product.getUuid() + ")");
        productRepository.deleteProduct(product.getUuid(), new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product deletedProduct) {
                android.util.Log.d("ListProductFragment", "Delete successful for product: " + product.getName());
                Toast.makeText(requireContext(), "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                // Remove from adapter
                adapter.removeProduct(product.getUuid());
                
                // Show empty state if no products left
                if (adapter.getItemCount() == 0) {
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("ListProductFragment", "Delete failed for product: " + product.getName() + ", error: " + message);
                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}