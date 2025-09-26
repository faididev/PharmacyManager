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

public class AddCategoryFragment extends Fragment {

    private TextInputLayout nameInput, descriptionInput;
    private Button createButton;
    private CategoryRepository categoryRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        // Initialize views
        nameInput = view.findViewById(R.id.category_name);
        descriptionInput = view.findViewById(R.id.category_description);
        createButton = view.findViewById(R.id.create_category_btn);

        // Initialize repository
        categoryRepository = new CategoryRepository(requireContext());

        // Set up button click listeners
        createButton.setOnClickListener(v -> createCategory());
        
        // Set up back button
        Button backButton = view.findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void createCategory() {
        if (!validateInputs()) {
            return;
        }

        // Check if user is still authenticated
        String token = new com.example.pharmacymanager.data.local.SessionManager(requireContext()).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        String name = nameInput.getEditText().getText().toString().trim();
        String description = descriptionInput.getEditText().getText().toString().trim();

        createButton.setEnabled(false);
        createButton.setText("Creating...");

        android.util.Log.d("AddCategoryFragment", "Creating category: " + name + ", Token present: " + (token != null));
        categoryRepository.createCategory(name, description, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(Category category) {
                android.util.Log.d("AddCategoryFragment", "Category created successfully: " + category.getName());
                createButton.setEnabled(true);
                createButton.setText("Create Category");
                
                Toast.makeText(requireContext(), "Category created successfully!", Toast.LENGTH_SHORT).show();
                
                // Clear form
                nameInput.getEditText().setText("");
                descriptionInput.getEditText().setText("");
                
                // Navigate back and refresh the list
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                    // Refresh the category list
                    ListCategoryFragment listFragment = (ListCategoryFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.fragement_container);
                    if (listFragment != null) {
                        listFragment.refreshCategories();
                    }
                }
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("AddCategoryFragment", "Error creating category: " + message);
                createButton.setEnabled(true);
                createButton.setText("Create Category");
                
                // Check if it's an authentication error
                if (message.contains("401") || message.contains("Unauthorized") || message.contains("Token")) {
                    Toast.makeText(requireContext(), "Authentication error. Please login again.", Toast.LENGTH_LONG).show();
                    // Don't redirect automatically, let user handle it
                } else {
                    Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
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
}