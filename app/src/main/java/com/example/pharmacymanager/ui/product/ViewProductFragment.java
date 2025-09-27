package com.example.pharmacymanager.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Product;
import com.google.android.material.button.MaterialButton;

public class ViewProductFragment extends Fragment {

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

    // Views
    private TextView productNameText;
    private TextView productDescriptionText;
    private TextView productPriceText;
    private TextView productQuantityText;
    private TextView productTotalText;
    private TextView productManufactureDateText;
    private TextView productExpiryDateText;
    private TextView productCategoryText;
    private MaterialButton btnEditProduct;
    private MaterialButton btnDeleteProduct;

    public ViewProductFragment() {
        // Required empty public constructor
    }

    public static ViewProductFragment newInstance(Product product) {
        ViewProductFragment fragment = new ViewProductFragment();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_product, container, false);

        initializeViews(view);
        populateFields();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        productNameText = view.findViewById(R.id.product_name);
        productDescriptionText = view.findViewById(R.id.product_description);
        productPriceText = view.findViewById(R.id.product_price);
        productQuantityText = view.findViewById(R.id.product_quantity);
        productTotalText = view.findViewById(R.id.product_total);
        productManufactureDateText = view.findViewById(R.id.product_manufacture_date);
        productExpiryDateText = view.findViewById(R.id.product_expiry_date);
        productCategoryText = view.findViewById(R.id.product_category);
        btnEditProduct = view.findViewById(R.id.btn_edit_product);
        btnDeleteProduct = view.findViewById(R.id.btn_delete_product);
    }

    private void populateFields() {
        // Set product name
        productNameText.setText(productName);


        // Set product description
        if (productDescription != null && !productDescription.trim().isEmpty()) {
            productDescriptionText.setText(productDescription);
        } else {
            productDescriptionText.setText("No description available");
        }

        // Set product price
        productPriceText.setText(String.format("$%.2f", productPrice));

        // Set product quantity
        productQuantityText.setText(String.valueOf(productQuantity));

        // Set product total
        productTotalText.setText(String.valueOf(productTotal));

        // Set manufacture date
        if (productManufactureDate != null && !productManufactureDate.trim().isEmpty()) {
            String date = productManufactureDate.split("T")[0];
            productManufactureDateText.setText(date);
        } else {
            productManufactureDateText.setText("N/A");
        }

        // Set expiry date
        if (productExpiryDate != null && !productExpiryDate.trim().isEmpty()) {
            String date = productExpiryDate.split("T")[0];
            productExpiryDateText.setText(date);
        } else {
            productExpiryDateText.setText("N/A");
        }

        // Set category
        productCategoryText.setText("Category ID: " + productCategoryId);
    }

    private void setupClickListeners() {
        btnEditProduct.setOnClickListener(v -> {
            // Create a Product object to pass to EditProductFragment
            Product product = new Product(
                0, productUuid, productName, "", productDescription,
                productQuantity, productTotal, productManufactureDate, productExpiryDate,
                productCategoryId, productPrice, null, null
            );
            
            EditProductFragment editFragment = EditProductFragment.newInstance(product);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnDeleteProduct.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });
    }

    private void showDeleteConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete '" + productName + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Navigate back to list and let the list handle the deletion
                    requireActivity().onBackPressed();
                    Toast.makeText(requireContext(), "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
