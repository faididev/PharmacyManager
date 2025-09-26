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

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Load categories
        loadCategories();

        return view;
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
                // Handle long click (e.g., show context menu for edit/delete)
                Toast.makeText(requireContext(), "Long clicked: " + category.getName(), Toast.LENGTH_SHORT).show();
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
                
                try {
                    List<Category> categories = parseCategoriesFromResponse(response);
                    adapter.setCategories(categories);
                    
                    if (categories.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }
                } catch (JSONException e) {
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
            
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject categoryJson = dataArray.getJSONObject(i);
                Category category = Category.fromJson(categoryJson);
                categories.add(category);
            }
        }
        
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
}
