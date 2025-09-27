package com.example.pharmacymanager.ui.product;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Category;
import com.example.pharmacymanager.data.entities.Product;
import com.example.pharmacymanager.data.repositories.CategoryRepository;
import com.example.pharmacymanager.data.repositories.ProductRepository;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddProductFragment extends Fragment {

    private TextInputLayout nameInput, descriptionInput, priceInput, 
                          quantityInput, totalInput, manufactureDateInputLayout, 
                          expiryDateInputLayout, categoryInput;
    private AutoCompleteTextView categoryDropdown;
    private Button createButton;
    private TextInputEditText manufactureDateInput, expiryDateInput;
    private ScrollView scrollView;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private List<Category> categories = new ArrayList<>();
    private int selectedCategoryId = 0;
    private Calendar manufactureCalendar = Calendar.getInstance();
    private Calendar expiryCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Initialize views
        initializeViews(view);

        // Initialize repositories
        productRepository = new ProductRepository(requireContext());
        categoryRepository = new CategoryRepository(requireContext());

        // Load categories for dropdown
        loadCategories();

        // Set up button click listeners
        createButton.setOnClickListener(v -> createProduct());
        
        // Set up back button
        Button backButton = view.findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Set up focus listeners for auto-scrolling
        setupFocusListeners();

        // Set up date picker listeners
        setupDateListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Adjust window to allow scrolling when keyboard appears
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Reset window adjustment
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    // Method to scroll to a specific view
    private void scrollToView(View view) {
        if (scrollView != null && view != null) {
            scrollView.post(() -> {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int viewTop = location[1];
                int scrollViewTop = scrollView.getTop();
                int scrollY = viewTop - scrollViewTop - 200; // Add some padding
                scrollView.smoothScrollTo(0, Math.max(0, scrollY));
            });
        }
    }

    private void setupFocusListeners() {
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (hasFocus) {
                scrollToView(v);
            }
        };

        // Add focus listeners to all input fields
        if (nameInput != null && nameInput.getEditText() != null) {
            nameInput.getEditText().setOnFocusChangeListener(focusListener);
        }
        if (descriptionInput != null && descriptionInput.getEditText() != null) {
            descriptionInput.getEditText().setOnFocusChangeListener(focusListener);
        }
        if (priceInput != null && priceInput.getEditText() != null) {
            priceInput.getEditText().setOnFocusChangeListener(focusListener);
        }
        if (quantityInput != null && quantityInput.getEditText() != null) {
            quantityInput.getEditText().setOnFocusChangeListener(focusListener);
        }
        if (totalInput != null && totalInput.getEditText() != null) {
            totalInput.getEditText().setOnFocusChangeListener(focusListener);
        }
        if (manufactureDateInputLayout != null && manufactureDateInputLayout.getEditText() != null) {
            manufactureDateInputLayout.getEditText().setOnFocusChangeListener(focusListener);
        }
        if (expiryDateInputLayout != null && expiryDateInputLayout.getEditText() != null) {
            expiryDateInputLayout.getEditText().setOnFocusChangeListener(focusListener);
        }
        if (categoryDropdown != null) {
            categoryDropdown.setOnFocusChangeListener(focusListener);
        }
    }

    private void setupDateListeners() {
        // Date picker listeners
        manufactureDateInput.setOnClickListener(v -> showDatePicker(manufactureCalendar, manufactureDateInput));
        expiryDateInput.setOnClickListener(v -> showDatePicker(expiryCalendar, expiryDateInput));
    }

    private void showDatePicker(Calendar calendar, TextInputEditText inputField) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    inputField.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void initializeViews(View view) {
        nameInput = view.findViewById(R.id.product_name);
        descriptionInput = view.findViewById(R.id.product_description);
        priceInput = view.findViewById(R.id.product_price);
        quantityInput = view.findViewById(R.id.product_quantity);
        totalInput = view.findViewById(R.id.product_total);
        manufactureDateInputLayout = view.findViewById(R.id.product_manufacture_date);
        expiryDateInputLayout = view.findViewById(R.id.product_expiry_date);
        categoryInput = view.findViewById(R.id.product_category);
        categoryDropdown = view.findViewById(R.id.category_dropdown);
        createButton = view.findViewById(R.id.create_product_btn);
        scrollView = view.findViewById(R.id.scrollView);
        
        // Date input fields
        manufactureDateInput = view.findViewById(R.id.manufacture_date_input);
        expiryDateInput = view.findViewById(R.id.expiry_date_input);
    }

    private void loadCategories() {
        categoryRepository.getCategories(new CategoryRepository.CategoryListCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    categories = parseCategoriesFromResponse(response);
                    setupCategoryDropdown();
                } catch (JSONException e) {
                    android.util.Log.e("AddProductFragment", "Error parsing categories: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("AddProductFragment", "Error loading categories: " + message);
                Toast.makeText(requireContext(), "Error loading categories: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Category> parseCategoriesFromResponse(JSONObject response) throws JSONException {
        List<Category> categoryList = new ArrayList<>();
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    JSONObject categoryJson = dataArray.getJSONObject(i);
                    Category category = Category.fromJsonSafe(categoryJson);
                    categoryList.add(category);
                } catch (Exception e) {
                    android.util.Log.e("AddProductFragment", "Error parsing category " + i + ": " + e.getMessage(), e);
                }
            }
        }
        
        return categoryList;
    }

    private void setupCategoryDropdown() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        categoryDropdown.setAdapter(adapter);

        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategoryId = categories.get(position).getId();
            android.util.Log.d("AddProductFragment", "Selected category: " + categories.get(position).getName() + " (ID: " + selectedCategoryId + ")");
        });
    }

    private void createProduct() {
        if (!validateInputs()) {
            return;
        }

        // Check if user is still authenticated
        String token = new com.example.pharmacymanager.data.local.SessionManager(requireContext()).getToken();
        android.util.Log.d("AddProductFragment", "Token check: " + (token != null ? "Token present (length: " + token.length() + ")" : "Token null"));
        
        if (token == null || token.isEmpty()) {
            android.util.Log.w("AddProductFragment", "No token available - user needs to login");
            Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }
        
        android.util.Log.d("AddProductFragment", "Token is valid, proceeding with product creation");

        String name = nameInput.getEditText().getText().toString().trim();
        String description = descriptionInput.getEditText().getText().toString().trim();
        double price = Double.parseDouble(priceInput.getEditText().getText().toString().trim());
        int quantity = Integer.parseInt(quantityInput.getEditText().getText().toString().trim());
        int total = Integer.parseInt(totalInput.getEditText().getText().toString().trim());
        String manufactureDate = manufactureDateInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();

        createButton.setEnabled(false);
        createButton.setText("Creating...");

        android.util.Log.d("AddProductFragment", "Creating product: " + name + ", Category ID: " + selectedCategoryId);
        
        productRepository.createProduct(name, "", description, quantity, total, 
                manufactureDate, expiryDate, selectedCategoryId, price,
                new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                android.util.Log.d("AddProductFragment", "Product created successfully: " + product.getName());
                createButton.setEnabled(true);
                createButton.setText("Create Product");
                
                Toast.makeText(requireContext(), "Product created successfully!", Toast.LENGTH_SHORT).show();
                
                // Navigate back to list product page and refresh
                navigateToListProduct();
            }

            @Override
            public void onError(String message) {
                // Enhanced error logging
                android.util.Log.e("AddProductFragment", "=== PRODUCT CREATION ERROR ===");
                android.util.Log.e("AddProductFragment", "Error message: " + message);
                android.util.Log.e("AddProductFragment", "Product name: " + name);
                android.util.Log.e("AddProductFragment", "Product description: " + description);
                android.util.Log.e("AddProductFragment", "Product price: " + price);
                android.util.Log.e("AddProductFragment", "Product quantity: " + quantity);
                android.util.Log.e("AddProductFragment", "Product total: " + total);
                android.util.Log.e("AddProductFragment", "Manufacture date: " + manufactureDate);
                android.util.Log.e("AddProductFragment", "Expiry date: " + expiryDate);
                android.util.Log.e("AddProductFragment", "Category ID: " + selectedCategoryId);
                android.util.Log.e("AddProductFragment", "=== END ERROR DETAILS ===");
                
                createButton.setEnabled(true);
                createButton.setText("Create Product");
                
                // Check if it's an authentication error
                if (message.contains("401") || message.contains("Unauthorized") || message.contains("Token")) {
                    android.util.Log.e("AddProductFragment", "Authentication error detected");
                    Toast.makeText(requireContext(), "Authentication error. Please login again.", Toast.LENGTH_LONG).show();
                } else if (message.contains("422") || message.contains("Unprocessable Entity")) {
                    android.util.Log.e("AddProductFragment", "Validation error detected");
                    Toast.makeText(requireContext(), "Validation error. Please check your input data.", Toast.LENGTH_LONG).show();
                } else if (message.contains("500") || message.contains("Internal Server Error")) {
                    android.util.Log.e("AddProductFragment", "Server error detected");
                    Toast.makeText(requireContext(), "Server error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                } else {
                    android.util.Log.e("AddProductFragment", "General error detected");
                    Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void clearForm() {
        nameInput.getEditText().setText("");
        descriptionInput.getEditText().setText("");
        priceInput.getEditText().setText("");
        quantityInput.getEditText().setText("");
        totalInput.getEditText().setText("");
        manufactureDateInput.setText("");
        expiryDateInput.setText("");
        categoryDropdown.setText("");
        selectedCategoryId = 0;
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate name
        String name = nameInput.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            nameInput.setError("Product name is required");
            isValid = false;
        } else if (name.length() < 2) {
            nameInput.setError("Product name must be at least 2 characters");
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

        // Validate price
        String priceStr = priceInput.getEditText().getText().toString().trim();
        if (priceStr.isEmpty()) {
            priceInput.setError("Price is required");
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    priceInput.setError("Price must be greater than 0");
                    isValid = false;
                } else {
                    priceInput.setError(null);
                    priceInput.setErrorEnabled(false);
                }
            } catch (NumberFormatException e) {
                priceInput.setError("Invalid price format");
                isValid = false;
            }
        }

        // Validate quantity
        String quantityStr = quantityInput.getEditText().getText().toString().trim();
        if (quantityStr.isEmpty()) {
            quantityInput.setError("Quantity is required");
            isValid = false;
        } else {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    quantityInput.setError("Quantity cannot be negative");
                    isValid = false;
                } else {
                    quantityInput.setError(null);
                    quantityInput.setErrorEnabled(false);
                }
            } catch (NumberFormatException e) {
                quantityInput.setError("Invalid quantity format");
                isValid = false;
            }
        }

        // Validate total
        String totalStr = totalInput.getEditText().getText().toString().trim();
        if (totalStr.isEmpty()) {
            totalInput.setError("Total stock is required");
            isValid = false;
        } else {
            try {
                int total = Integer.parseInt(totalStr);
                if (total < 0) {
                    totalInput.setError("Total stock cannot be negative");
                    isValid = false;
                } else {
                    totalInput.setError(null);
                    totalInput.setErrorEnabled(false);
                }
            } catch (NumberFormatException e) {
                totalInput.setError("Invalid total format");
                isValid = false;
            }
        }

        // Validate manufacture date
        String manufactureDate = manufactureDateInput.getText().toString().trim();
        if (manufactureDate.isEmpty()) {
            manufactureDateInputLayout.setError("Manufacture date is required");
            isValid = false;
        } else {
            manufactureDateInputLayout.setError(null);
            manufactureDateInputLayout.setErrorEnabled(false);
        }

        // Validate expiry date
        String expiryDate = expiryDateInput.getText().toString().trim();
        if (expiryDate.isEmpty()) {
            expiryDateInputLayout.setError("Expiry date is required");
            isValid = false;
        } else {
            expiryDateInputLayout.setError(null);
            expiryDateInputLayout.setErrorEnabled(false);
        }

        // Validate category
        if (selectedCategoryId == 0) {
            categoryInput.setError("Please select a category");
            isValid = false;
        } else {
            categoryInput.setError(null);
            categoryInput.setErrorEnabled(false);
        }

        return isValid;
    }

    private void navigateToListProduct() {
        if (getActivity() != null) {
            // Create a new instance of ListProductFragment
            ListProductFragment listFragment = new ListProductFragment();
            
            // Replace current fragment with list fragment
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_container, listFragment)
                    .commit();
        }
    }
}
