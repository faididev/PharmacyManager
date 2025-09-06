package com.example.pharmacymanager.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pharmacymanager.R;
import com.google.android.material.button.MaterialButton;

public class AddCategoryFragment extends Fragment {

    private MaterialButton addNew;
    private MaterialButton btnBack;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AddCategoryFragment() {
        // Required empty public constructor
    }

    public static AddCategoryFragment newInstance(String param1, String param2) {
        AddCategoryFragment fragment = new AddCategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        // Locate buttons inside the fragment
        addNew = view.findViewById(R.id.btnAdd);
        btnBack = view.findViewById(R.id.btnBack);

        // Example: add click listeners
        addNew.setOnClickListener(v -> {
            // Handle Add button click
        });

        btnBack.setOnClickListener(v -> {
            // Handle Back button click
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
