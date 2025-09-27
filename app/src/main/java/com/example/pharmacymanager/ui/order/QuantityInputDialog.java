package com.example.pharmacymanager.ui.order;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Product;

public class QuantityInputDialog extends DialogFragment {
    
    private Product product;
    private QuantityCallback callback;
    private EditText quantityEditText;
    private Button confirmButton;
    private Button cancelButton;
    private TextView productInfo;
    
    public interface QuantityCallback {
        void onQuantitySelected(Product product, int quantity);
    }
    
    public QuantityInputDialog(Product product, QuantityCallback callback) {
        this.product = product;
        this.callback = callback;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_quantity_input, container, false);
        
        initializeViews(view);
        setupProductInfo();
        setupClickListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        productInfo = view.findViewById(R.id.tv_product_info);
        quantityEditText = view.findViewById(R.id.et_quantity);
        confirmButton = view.findViewById(R.id.btn_confirm);
        cancelButton = view.findViewById(R.id.btn_cancel);
    }
    
    private void setupProductInfo() {
        String info = product.getName() + "\n" +
                     "Price: $" + String.format("%.2f", product.getPrice()) + "\n" +
                     "Available: " + product.getQuantity();
        productInfo.setText(info);
    }
    
    private void setupClickListeners() {
        confirmButton.setOnClickListener(v -> {
            try {
                Log.d("QuantityInputDialog", "=== CONFIRM BUTTON CLICKED ===");
                String quantityText = quantityEditText.getText().toString().trim();
                Log.d("QuantityInputDialog", "Quantity text: '" + quantityText + "'");
                
                if (quantityText.isEmpty()) {
                    Log.d("QuantityInputDialog", "Quantity is empty");
                    Toast.makeText(getContext(), "Please enter a quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                int quantity = Integer.parseInt(quantityText);
                Log.d("QuantityInputDialog", "Parsed quantity: " + quantity);
                
                if (quantity <= 0) {
                    Log.d("QuantityInputDialog", "Quantity is <= 0");
                    Toast.makeText(getContext(), "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (quantity > product.getQuantity()) {
                    Log.d("QuantityInputDialog", "Quantity exceeds stock. Requested: " + quantity + ", Available: " + product.getQuantity());
                    Toast.makeText(getContext(), "Quantity exceeds available stock", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Log.d("QuantityInputDialog", "Callback is null: " + (callback == null));
                if (callback != null) {
                    Log.d("QuantityInputDialog", "Calling callback with product: " + product.getName() + ", quantity: " + quantity);
                    callback.onQuantitySelected(product, quantity);
                    Log.d("QuantityInputDialog", "Callback called successfully");
                } else {
                    Log.e("QuantityInputDialog", "Callback is null!");
                }
                dismiss();
                
            } catch (NumberFormatException e) {
                Log.e("QuantityInputDialog", "Number format exception: " + e.getMessage());
                Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        
        cancelButton.setOnClickListener(v -> dismiss());
    }
}
