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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

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

public class EditProductFragment extends Fragment {

    private static final String ARG_PRODUCT_UUID = "product_uuid";
    private static final String ARG_PRODUCT_NAME = "product_name";
    private static final String ARG_PRODUCT_DESCRIPTION = "product_description";
    private static final String ARG_PRODUCT_PRICE = "product_price";
    private static final String ARG_PRODUCT_QUANTITY = "product_quantity";
    private static final String ARG_PRODUCT_TOTAL = "product_total";
    private static final String ARG_PRODUCT_MANUFACTURE_DATE = "product_manufacture_date";
    private static final String ARG_PRODUCT_EXPIRY_DATE = "product_expiry_date";
    private static final String ARG_PRODUCT_CATEGORY_ID = "product_category_id";

    private String productUuid;
    private String productName;
    private String productDescription;
    private double productPrice;
    private int productQuantity;
    private int productTotal;
    private String productManufactureDate;
    private String productExpiryDate;
    private int productCategoryId;

    private TextInputLayout nameInput, descriptionInput, priceInput, 
                          quantityInput, totalInput, manufactureDateInputLayout, 
                          expiryDateInputLayout, categoryInput;
    private AutoCompleteTextView categoryDropdown;
    private Button updateButton, deleteButton;
    private TextInputEditText manufactureDateInput, expiryDateInput;
    private ScrollView scrollView;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private List<Category> categories = new ArrayList<>();
    private int selectedCategoryId = 0;
    private Calendar manufactureCalendar = Calendar.getInstance();
    private Calendar expiryCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    public EditProductFragment() {
        // Required empty public constructor
    }

    public static EditProductFragment newInstance(Product product) {
        EditProductFragment fragment = new EditProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_UUID, product.getUuid());
        args.putString(ARG_PRODUCT_NAME, product.getName());
        args.putString(ARG_PRODUCT_DESCRIPTION, product.getDescription());
        args.putDouble(ARG_PRODUCT_PRICE, product.getPrice());
        args.putInt(ARG_PRODUCT_QUANTITY, product.getQuantity());
        args.putInt(ARG_PRODUCT_TOTAL, product.getTotal());
        args.putString(ARG_PRODUCT_MANUFACTURE_DATE, product.getManufactureDate());
        args.putString(ARG_PRODUCT_EXPIRY_DATE, product.getExpiryDate());
        args.putInt(ARG_PRODUCT_CATEGORY_ID, product.getCategoryId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productUuid = getArguments().getString(ARG_PRODUCT_UUID);
            productName = getArguments().getString(ARG_PRODUCT_NAME);
            productDescription = getArguments().getString(ARG_PRODUCT_DESCRIPTION);
            productPrice = getArguments().getDouble(ARG_PRODUCT_PRICE);
            productQuantity = getArguments().getInt(ARG_PRODUCT_QUANTITY);
            productTotal = getArguments().getInt(ARG_PRODUCT_TOTAL);
            productManufactureDate = getArguments().getString(ARG_PRODUCT_MANUFACTURE_DATE);
            productExpiryDate = getArguments().getString(ARG_PRODUCT_EXPIRY_DATE);
            productCategoryId = getArguments().getInt(ARG_PRODUCT_CATEGORY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_product, container, false);

        // Initialize views
        initializeViews(view);

        // Initialize repositories
        productRepository = new ProductRepository(requireContext());
        categoryRepository = new CategoryRepository(requireContext());

        // Load categories for dropdown
        loadCategories();

        // Populate fields with existing data
        populateFields();

        // Set up button click listeners
        updateButton.setOnClickListener(v -> updateProduct());
        deleteButton.setOnClickListener(v -> deleteProduct());
        
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
        if (manufactureDateInput != null) {
            manufactureDateInput.setOnFocusChangeListener(focusListener);
        }
        if (expiryDateInput != null) {
            expiryDateInput.setOnFocusChangeListener(focusListener);
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
        updateButton = view.findViewById(R.id.update_product_btn);
        deleteButton = view.findViewById(R.id.delete_product_btn);
        scrollView = view.findViewById(R.id.scrollView);
        
        
        // Date input fields
        manufactureDateInput = view.findViewById(R.id.manufacture_date_input);
        expiryDateInput = view.findViewById(R.id.expiry_date_input);
    }

    private void populateFields() {
        if (productName != null) {
            nameInput.getEditText().setText(productName);
        }
        if (productDescription != null) {
            descriptionInput.getEditText().setText(productDescription);
        }
        priceInput.getEditText().setText(String.valueOf(productPrice));
        quantityInput.getEditText().setText(String.valueOf(productQuantity));
        totalInput.getEditText().setText(String.valueOf(productTotal));
        if (productManufactureDate != null) {
            manufactureDateInput.setText(productManufactureDate);
        }
        if (productExpiryDate != null) {
            expiryDateInput.setText(productExpiryDate);
        }
        selectedCategoryId = productCategoryId;
    }

    private void loadCategories() {
        categoryRepository.getCategories(new CategoryRepository.CategoryListCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    categories = parseCategoriesFromResponse(response);
                    setupCategoryDropdown();
                } catch (JSONException e) {
                    android.util.Log.e("EditProductFragment", "Error parsing categories: " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                android.util.Log.e("EditProductFragment", "Error loading categories: " + message);
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
                    android.util.Log.e("EditProductFragment", "Error parsing category " + i + ": " + e.getMessage(), e);
                }
            }
        }
        
        return categoryList;
    }

    private void setupCategoryDropdown() {
        List<String> categoryNames = new ArrayList<>();
        int selectedIndex = 0;
        
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            categoryNames.add(category.getName());
            if (category.getId() == selectedCategoryId) {
                selectedIndex = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_dropdown_item_1line, categoryNames);
        categoryDropdown.setAdapter(adapter);
        
        // Set the selected category
        if (selectedIndex < categoryNames.size()) {
            categoryDropdown.setText(categoryNames.get(selectedIndex), false);
        }

        categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategoryId = categories.get(position).getId();
            android.util.Log.d("EditProductFragment", "Selected category: " + categories.get(position).getName() + " (ID: " + selectedCategoryId + ")");
        });
    }

    private void updateProduct() {
        if (!validateInputs()) {
            return;
        }

        String name = nameInput.getEditText().getText().toString().trim();
        String description = descriptionInput.getEditText().getText().toString().trim();
        double price = Double.parseDouble(priceInput.getEditText().getText().toString().trim());
        int quantity = Integer.parseInt(quantityInput.getEditText().getText().toString().trim());
        int total = Integer.parseInt(totalInput.getEditText().getText().toString().trim());
        String manufactureDate = manufactureDateInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();

        updateButton.setEnabled(false);
        updateButton.setText("Updating...");

        productRepository.updateProduct(productUuid, name, "", description, quantity, total, 
                manufactureDate, expiryDate, selectedCategoryId, price, 
                new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                updateButton.setEnabled(true);
                updateButton.setText("Update Product");
                
                Toast.makeText(requireContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();
                
                // Navigate back to list product page
                navigateToListProduct();
            }

            @Override
            public void onError(String message) {
                updateButton.setEnabled(true);
                updateButton.setText("Update Product");
                Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteProduct() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteButton.setEnabled(false);
                    deleteButton.setText("Deleting...");

                    productRepository.deleteProduct(productUuid, new ProductRepository.ProductCallback() {
                        @Override
                        public void onSuccess(Product product) {
                            deleteButton.setEnabled(true);
                            deleteButton.setText("Delete Product");
                            
                            Toast.makeText(requireContext(), "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                            
                            // Navigate back to list product page
                            navigateToListProduct();
                        }

                        @Override
                        public void onError(String message) {
                            deleteButton.setEnabled(true);
                            deleteButton.setText("Delete Product");
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
