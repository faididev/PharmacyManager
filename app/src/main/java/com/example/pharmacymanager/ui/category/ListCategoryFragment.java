package com.example.pharmacymanager.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListCategoryFragment extends Fragment {

    private FloatingActionButton fabAddCategory;

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

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_list_category, container, false);

        // Locate the FloatingActionButton
        fabAddCategory = view.findViewById(R.id.fabAddCategory);
        // Set click listener to open AddCategoryFragment
        fabAddCategory.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_container, new AddCategoryFragment())
                    .addToBackStack(null) // allows back navigation
                    .commit();
        });

        return view;
    }
}
