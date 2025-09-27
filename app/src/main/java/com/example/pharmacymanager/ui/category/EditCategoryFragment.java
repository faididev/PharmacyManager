package com.example.pharmacymanager.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Category;
import com.example.pharmacymanager.data.repositories.CategoryRepository;
import com.google.android.material.textfield.TextInputLayout;

public class EditCategoryFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_CATEGORY_NAME = "category_name";
    private static final String ARG_CATEGORY_DESCRIPTION = "category_description";

    private int categoryId;
    private String categoryName;
    private String categoryDescription;

    private TextInputLayout nameInput, descriptionInput;
    private Button updateButton, deleteButton;
    private CategoryRepository categoryRepository;

    public EditCategoryFragment() {
        // Required empty public constructor
    }

    public static EditCategoryFragment newInstance(Category category) {
        EditCategoryFragment fragment = new EditCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, category.getId());
        args.putString(ARG_CATEGORY_NAME, category.getName());
        args.putString(ARG_CATEGORY_DESCRIPTION, category.getDescription());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
            categoryDescription = getArguments().getString(ARG_CATEGORY_DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_category, container, false);

        // Initialize views
        nameInput = view.findViewById(R.id.category_name);
        descriptionInput = view.findViewById(R.id.category_description);
        updateButton = view.findViewById(R.id.update_category_btn);
        deleteButton = view.findViewById(R.id.delete_category_btn);

        // Initialize repository
        categoryRepository = new CategoryRepository(requireContext());

        // Populate fields with existing data
        if (categoryName != null) {
            nameInput.getEditText().setText(categoryName);
        }
        if (categoryDescription != null) {
            descriptionInput.getEditText().setText(categoryDescription);
        }

        // Set up button click listeners
        updateButton.setOnClickListener(v -> updateCategory());
        deleteButton.setOnClickListener(v -> deleteCategory());
        
        // Set up back button
        Button backButton = view.findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void updateCategory() {
        if (!validateInputs()) {
            return;
        }

        String name = nameInput.getEditText().getText().toString().trim();
        String description = descriptionInput.getEditText().getText().toString().trim();

        updateButton.setEnabled(false);
        updateButton.setText("Updating...");

        categoryRepository.updateCategory(categoryId, name, description, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(Category category) {
                updateButton.setEnabled(true);
                updateButton.setText("Update Category");
                
                Toast.makeText(requireContext(), "Category updated successfully!", Toast.LENGTH_SHORT).show();
                
                // Navigate back to list category page
                navigateToListCategory();
            }

            @Override
            public void onError(String message) {
                updateButton.setEnabled(true);
                updateButton.setText("Update Category");
                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteCategory() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteButton.setEnabled(false);
                    deleteButton.setText("Deleting...");

                    categoryRepository.deleteCategory(categoryId, new CategoryRepository.CategoryCallback() {
                        @Override
                        public void onSuccess(Category category) {
                            deleteButton.setEnabled(true);
                            deleteButton.setText("Delete Category");
                            
                            Toast.makeText(requireContext(), "Category deleted successfully!", Toast.LENGTH_SHORT).show();
                            
                            // Navigate back to list category page
                            navigateToListCategory();
                        }

                        @Override
                        public void onError(String message) {
                            deleteButton.setEnabled(true);
                            deleteButton.setText("Delete Category");
                            Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate name
        String name = nameInput.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            nameInput.setError("Category name is required");
            isValid = false;
        } else if (name.length() < 2) {
            nameInput.setError("Category name must be at least 2 characters");
            isValid = false;
        } else {
            nameInput.setError(null);
            nameInput.setErrorEnabled(false);
        }

        // Validate description
        String description = descriptionInput.getEditText().getText().toString().trim();
        if (description.isEmpty()) {
            descriptionInput.setError("Description is required");
            isValid = false;
        } else if (description.length() < 5) {
            descriptionInput.setError("Description must be at least 5 characters");
            isValid = false;
        } else {
            descriptionInput.setError(null);
            descriptionInput.setErrorEnabled(false);
        }

        return isValid;
    }

    private void navigateToListCategory() {
        if (getActivity() != null) {
            // Create a new instance of ListCategoryFragment
            ListCategoryFragment listFragment = new ListCategoryFragment();
            
            // Replace current fragment with list fragment
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_container, listFragment)
                    .commit();
        }
    }
}