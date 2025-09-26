package com.example.pharmacymanager.ui.order;

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
import com.example.pharmacymanager.data.entities.Order;
import com.example.pharmacymanager.data.repositories.OrderRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private FloatingActionButton fabAddOrder;
    private OrderRepository orderRepository;

    public ListOrderFragment() {
        // Required empty public constructor
    }

    public static ListOrderFragment newInstance(String param1, String param2) {
        ListOrderFragment fragment = new ListOrderFragment();
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

        View view = inflater.inflate(R.layout.fragment_list_order, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        fabAddOrder = view.findViewById(R.id.fabAddOrder);

        // Initialize repository
        orderRepository = new OrderRepository(requireContext());

        // Test parsing with sample data
        Order.testParsing();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Load orders
        loadOrders();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Set click listener for order items
        adapter.setOnOrderClickListener(new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                // Navigate to ViewOrderFragment
                ViewOrderFragment viewFragment = ViewOrderFragment.newInstance(order);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragement_container, viewFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onOrderLongClick(Order order) {
                // Handle long click (e.g., show context menu)
                Toast.makeText(requireContext(), "Long clicked: " + order.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOrderDeleteClick(Order order) {
                // Handle delete click
                showDeleteConfirmation(order);
            }
        });
    }

    private void setupClickListeners() {
        fabAddOrder.setOnClickListener(v -> {
            // Navigate to AddOrderFragment
            if (getActivity() != null) {
                ((com.example.pharmacymanager.ui.MainActivity) getActivity()).navigateToAddOrder();
            }
        });
    }

    private void loadOrders() {
        showLoading(true);
        
        orderRepository.getOrders(new OrderRepository.OrderListCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                showLoading(false);
                try {
                    List<Order> orders = parseOrdersFromResponse(response);
                    updateUI(orders);
                } catch (JSONException e) {
                    android.util.Log.e("ListOrderFragment", "Error parsing orders: " + e.getMessage());
                    showError("Failed to parse order data");
                }
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                showError(message);
            }
        });
    }

    private List<Order> parseOrdersFromResponse(JSONObject response) throws JSONException {
        List<Order> orders = new ArrayList<>();
        
        if (response.has("data")) {
            JSONArray dataArray = response.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject orderJson = dataArray.getJSONObject(i);
                Order order = Order.fromJson(orderJson);
                orders.add(order);
            }
        }
        
        return orders;
    }

    private void updateUI(List<Order> orders) {
        if (orders.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            adapter.setOrders(orders);
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

    private void showDeleteConfirmation(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete " + order.toString() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteOrder(order))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteOrder(Order order) {
        showLoading(true);
        
        orderRepository.deleteOrder(order.getId(), new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order deletedOrder) {
                showLoading(false);
                Toast.makeText(requireContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                adapter.removeOrder(order.getId());
                
                // Check if list is now empty
                if (adapter.getItemCount() == 0) {
                    showEmptyState(true);
                }
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(requireContext(), "Error deleting order: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list when returning to this fragment
        loadOrders();
    }
}
