package com.example.pharmacymanager.ui.product;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

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
    private TextInputEditText searchEditText;
    private MaterialButton sortButton;
    private ProductAdapter.SortType currentSortType = ProductAdapter.SortType.NONE;

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
        searchEditText = view.findViewById(R.id.searchEditText);
        sortButton = view.findViewById(R.id.sortButton);

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

    @Override
    public void onResume() {
        super.onResume();
        // Load products when fragment becomes visible
        loadProducts();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {

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
                showDeleteConfirmationDialog(product);
            }
        });
    }

    private void setupClickListeners() {
        fabAddProduct.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_container, new AddProductFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterProducts(s.toString());
                updateEmptyState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sort functionality
        sortButton.setOnClickListener(v -> showSortDialog());
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

    private void updateEmptyState() {
        boolean isEmpty = adapter.getItemCount() == 0;
        showEmptyState(isEmpty);
    }

    private void showSortDialog() {
        String[] sortOptions = {
            "No Sorting",
            "Quantity (Low to High)",
            "Quantity (High to Low)",
            "Name (A to Z)",
            "Name (Z to A)",
            "Price (Low to High)",
            "Price (High to Low)"
        };

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Sort Products")
                .setItems(sortOptions, (dialog, which) -> {
                    ProductAdapter.SortType sortType;
                    switch (which) {
                        case 0:
                            sortType = ProductAdapter.SortType.NONE;
                            break;
                        case 1:
                            sortType = ProductAdapter.SortType.QUANTITY_ASC;
                            break;
                        case 2:
                            sortType = ProductAdapter.SortType.QUANTITY_DESC;
                            break;
                        case 3:
                            sortType = ProductAdapter.SortType.NAME_ASC;
                            break;
                        case 4:
                            sortType = ProductAdapter.SortType.NAME_DESC;
                            break;
                        case 5:
                            sortType = ProductAdapter.SortType.PRICE_ASC;
                            break;
                        case 6:
                            sortType = ProductAdapter.SortType.PRICE_DESC;
                            break;
                        default:
                            sortType = ProductAdapter.SortType.NONE;
                    }
                    
                    currentSortType = sortType;
                    adapter.sortProducts(sortType);
                    updateEmptyState();
                    
                    // Update sort button text
                    updateSortButtonText();
                })
                .show();
    }

    private void updateSortButtonText() {
        String buttonText;
        switch (currentSortType) {
            case QUANTITY_ASC:
                buttonText = "Qty ↑";
                break;
            case QUANTITY_DESC:
                buttonText = "Qty ↓";
                break;
            case NAME_ASC:
                buttonText = "Name A-Z";
                break;
            case NAME_DESC:
                buttonText = "Name Z-A";
                break;
            case PRICE_ASC:
                buttonText = "Price ↑";
                break;
            case PRICE_DESC:
                buttonText = "Price ↓";
                break;
            case NONE:
            default:
                buttonText = "Sort";
                break;
        }
        sortButton.setText(buttonText);
    }

    public void refreshProducts() {
        loadProducts();
    }

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