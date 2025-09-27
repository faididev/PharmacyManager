package com.example.pharmacymanager.ui.customer;

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
import com.example.pharmacymanager.data.entities.Customer;
import com.example.pharmacymanager.data.repositories.CustomerRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListCustomerFragment extends Fragment {

    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private FloatingActionButton fabAddCustomer;
    private CustomerRepository customerRepository;

    public ListCustomerFragment() {
        // Required empty public constructor
    }

    public static ListCustomerFragment newInstance(String param1, String param2) {
        ListCustomerFragment fragment = new ListCustomerFragment();
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

        View view = inflater.inflate(R.layout.fragment_list_customer, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        fabAddCustomer = view.findViewById(R.id.fabAddCustomer);

        // Initialize repository
        customerRepository = new CustomerRepository(requireContext());

        // Test parsing with sample data
        Customer.testParsing();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Load customers
        loadCustomers();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new CustomerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnCustomerClickListener(new CustomerAdapter.OnCustomerClickListener() {
            @Override
            public void onCustomerClick(Customer customer) {
                // Navigate to EditCustomerFragment
                EditCustomerFragment editFragment = EditCustomerFragment.newInstance(customer);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragement_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onCustomerLongClick(Customer customer) {
                Toast.makeText(requireContext(), "Long clicked: " + customer.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCustomerDeleteClick(Customer customer) {
                // Handle delete click
                showDeleteConfirmation(customer);
            }
        });
    }

    private void setupClickListeners() {
        fabAddCustomer.setOnClickListener(v -> {
            // Navigate to AddCustomerFragment
            if (getActivity() != null) {
                ((com.example.pharmacymanager.ui.MainActivity) getActivity()).navigateToAddCustomer();
            }
        });
    }

    private void loadCustomers() {
        showLoading(true);
        
        customerRepository.getCustomers(new CustomerRepository.CustomerListCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                showLoading(false);
                try {
                    List<Customer> customers = parseCustomersFromResponse(response);
                    updateUI(customers);
                } catch (JSONException e) {
                    android.util.Log.e("ListCustomerFragment", "Error parsing customers: " + e.getMessage());
                    showError("Failed to parse customer data");
                }
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                showError(message);
            }
        });
    }

    private List<Customer> parseCustomersFromResponse(JSONObject response) throws JSONException {
        List<Customer> customers = new ArrayList<>();
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject customerJson = dataArray.getJSONObject(i);
                Customer customer = Customer.fromJson(customerJson);
                customers.add(customer);
            }
        }
        
        return customers;
    }

    private void updateUI(List<Customer> customers) {
        if (customers.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            adapter.setCustomers(customers);
        }
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

    private void showError(String message) {
        Toast.makeText(requireContext(), "Error: " + message, Toast.LENGTH_LONG).show();
        showEmptyState(true);
    }

    private void showDeleteConfirmation(Customer customer) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete " + customer.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteCustomer(customer))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteCustomer(Customer customer) {
        showLoading(true);
        
        customerRepository.deleteCustomer(customer.getId(), new CustomerRepository.CustomerCallback() {
            @Override
            public void onSuccess(Customer deletedCustomer) {
                showLoading(false);
                Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show();
                adapter.removeCustomer(customer.getId());
                
                // Check if list is now empty
                if (adapter.getItemCount() == 0) {
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error deleting customer: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list when returning to this fragment
        loadCustomers();
    }
}
