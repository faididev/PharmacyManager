package com.example.pharmacymanager.ui.category;

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
// import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Category;
import com.example.pharmacymanager.data.repositories.CategoryRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListCategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    // private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAddCategory;
    private CategoryRepository categoryRepository;

    public ListCategoryFragment() {
        // Required empty public constructor
    }

    public static ListCategoryFragment newInstance(String param1, String param2) {
        ListCategoryFragment fragment = new ListCategoryFragment();
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

        View view = inflater.inflate(R.layout.fragment_list_category, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        // swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);

        // Initialize repository
        categoryRepository = new CategoryRepository(requireContext());

        // Test parsing with sample data
        Category.testParsing();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load categories when fragment becomes visible
        loadCategories();
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Set click listener for category items
        adapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Handle category click (e.g., show details or edit)
                Toast.makeText(requireContext(), "Clicked: " + category.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCategoryLongClick(Category category) {
                // Handle long click - navigate to edit fragment
                EditCategoryFragment editFragment = EditCategoryFragment.newInstance(category);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragement_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onCategoryDeleteClick(Category category) {
                // Handle delete button click
                showDeleteConfirmationDialog(category);
            }
        });
    }

    private void setupClickListeners() {
        // Floating action button to add new category
        fabAddCategory.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_container, new AddCategoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Swipe to refresh (disabled for now)
        // swipeRefreshLayout.setOnRefreshListener(this::loadCategories);
    }

    private void loadCategories() {
        showLoading(true);
        
        categoryRepository.getCategories(new CategoryRepository.CategoryListCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                showLoading(false);
                // swipeRefreshLayout.setRefreshing(false);
                
                android.util.Log.d("ListCategoryFragment", "Received response: " + response.toString());
                
                try {
                    List<Category> categories = parseCategoriesFromResponse(response);
                    android.util.Log.d("ListCategoryFragment", "Setting " + categories.size() + " categories to adapter");
                    adapter.setCategories(categories);
                    
                    if (categories.isEmpty()) {
                        android.util.Log.d("ListCategoryFragment", "No categories found, showing empty state");
                        showEmptyState(true);
                    } else {
                        android.util.Log.d("ListCategoryFragment", "Categories loaded successfully, hiding empty state");
                        showEmptyState(false);
                    }
                } catch (JSONException e) {
                    android.util.Log.e("ListCategoryFragment", "Error parsing categories: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Error parsing categories: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                // swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Error loading categories: " + message, Toast.LENGTH_LONG).show();
                showEmptyState(true);
            }
        });
    }

    private List<Category> parseCategoriesFromResponse(JSONObject response) throws JSONException {
        List<Category> categories = new ArrayList<>();
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            android.util.Log.d("ListCategoryFragment", "Found " + dataArray.length() + " categories in response");
            
            // Log the structure of the first few categories for debugging
            for (int debugIndex = 0; debugIndex < Math.min(3, dataArray.length()); debugIndex++) {
                try {
                    JSONObject debugJson = dataArray.getJSONObject(debugIndex);
                    android.util.Log.d("ListCategoryFragment", "DEBUG - Category " + debugIndex + " structure: " + debugJson.toString());
                } catch (Exception e) {
                    android.util.Log.e("ListCategoryFragment", "DEBUG - Error reading category " + debugIndex, e);
                }
            }
            
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    JSONObject categoryJson = dataArray.getJSONObject(i);
                    android.util.Log.d("ListCategoryFragment", "Parsing category " + i + ": " + categoryJson.toString());
                    Category category = Category.fromJsonSafe(categoryJson);
                    categories.add(category);
                    android.util.Log.d("ListCategoryFragment", "Successfully parsed category: " + category.getName());
                } catch (Exception e) {
                    android.util.Log.e("ListCategoryFragment", "Error parsing category " + i + ": " + e.getMessage(), e);
                    // Continue with next category instead of failing completely
                }
            }
        } else {
            android.util.Log.e("ListCategoryFragment", "No 'data' field found in response: " + response.toString());
        }
        
        android.util.Log.d("ListCategoryFragment", "Total categories parsed: " + categories.size());
        return categories;
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

    // Method to refresh the list (can be called from AddCategoryFragment)
    public void refreshCategories() {
        loadCategories();
    }

    // Method to update a specific category in the list
    public void updateCategoryInList(Category updatedCategory) {
        adapter.updateCategory(updatedCategory);
    }

    private void showDeleteConfirmationDialog(Category category) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete '" + category.getName() + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteCategory(category);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void deleteCategory(Category category) {
        android.util.Log.d("ListCategoryFragment", "Starting delete for category: " + category.getName() + " (ID: " + category.getId() + ")");
        categoryRepository.deleteCategory(category.getId(), new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(Category deletedCategory) {
                android.util.Log.d("ListCategoryFragment", "Delete successful for category: " + category.getName());
                Toast.makeText(requireContext(), "Category deleted successfully!", Toast.LENGTH_SHORT).show();
                // Remove from adapter
                adapter.removeCategory(category.getId());
                
                // Show empty state if no categories left
                if (adapter.getItemCount() == 0) {
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("ListCategoryFragment", "Delete failed for category: " + category.getName() + ", error: " + message);
                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
