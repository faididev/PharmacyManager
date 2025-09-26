package com.example.pharmacymanager.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmacymanager.R;
import com.example.pharmacymanager.data.entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onCategoryLongClick(Category category);
        void onCategoryDeleteClick(Category category);
    }

    public CategoryAdapter() {
        this.categories = new ArrayList<>();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        android.util.Log.d("CategoryAdapter", "Setting " + this.categories.size() + " categories to adapter");
        notifyDataSetChanged();
    }

    public void addCategory(Category category) {
        this.categories.add(category);
        notifyItemInserted(categories.size() - 1);
    }

    public void updateCategory(Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == category.getId()) {
                categories.set(i, category);
                notifyItemChanged(i);
                android.util.Log.d("CategoryAdapter", "Updated category: " + category.getName());
                break;
            }
        }
    }

    public void removeCategory(int categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == categoryId) {
                categories.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_category_list_template, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        android.util.Log.d("CategoryAdapter", "getItemCount() called, returning: " + categories.size());
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private TextView categoryDescription;
        private TextView categoryDate;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryDescription = itemView.findViewById(R.id.category_description);
            categoryDate = itemView.findViewById(R.id.category_date);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            // Set click listeners for action buttons
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCategoryLongClick(categories.get(position)); // Use edit functionality
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCategoryDeleteClick(categories.get(position));
                    }
                }
            });

            // Set click listeners for item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCategoryClick(categories.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Provide visual feedback
                        v.setAlpha(0.7f);
                        v.postDelayed(() -> v.setAlpha(1.0f), 150);
                        
                        listener.onCategoryLongClick(categories.get(position));
                        return true;
                    }
                }
                return false;
            });
        }

        public void bind(Category category) {
            android.util.Log.d("CategoryAdapter", "Binding category: " + category.getName());
            categoryName.setText(category.getName());
            
            // Handle null or empty descriptions
            String description = category.getDescription();
            if (description == null || description.trim().isEmpty()) {
                categoryDescription.setText("No description available");
            } else {
                categoryDescription.setText(description);
            }
            
            // Format date (remove time part)
            if (category.getCreatedAt() != null) {
                String date = category.getCreatedAt().split("T")[0];
                categoryDate.setText("Created: " + date);
            } else {
                categoryDate.setText("Created: Unknown");
            }
        }
    }
}



