package com.demo.budjetkeeperver2.categorydb;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.budjetkeeperver2.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {
    private List<Category> categories;
    private CategoriesAdapter.OnCategoryClickListener onCategoryClickListener;

    public CategoriesAdapter(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoriesAdapter.CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoriesViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.textViewCategoryTitle.setText(category.getName());
    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCategoryTitle;

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryTitle = itemView.findViewById(R.id.textViewCategoryTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCategoryClickListener != null) {
                        onCategoryClickListener.onCategoryClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    interface OnCategoryClickListener {
        void onCategoryClick(int position);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setOnCategoryClickListener(CategoriesAdapter.OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
