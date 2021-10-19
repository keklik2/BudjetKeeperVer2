package com.demo.budjetkeeperver2.categorydb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.demo.budjetkeeperver2.R;

import java.util.List;

import static com.demo.budjetkeeperver2.R.string.repeating_bank_name_warning;
import static com.demo.budjetkeeperver2.R.string.unfilled_fields_warning;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText editTextCategoryTitle;

    private CategoriesViewModel categoriesViewModel;
    private Spinner spinnerCategoryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

        editTextCategoryTitle = findViewById(R.id.editTextCategoryTitle);
        spinnerCategoryType = findViewById(R.id.spinnerCategoryType);
    }

    public void apply(View view) {
        if (areFilled(editTextCategoryTitle)) {
            String name = editTextCategoryTitle.getText().toString();
            if (isNameUnique(name)) {
                int type = spinnerCategoryType.getSelectedItemPosition();

                Category category = new Category(name, type);
                categoriesViewModel.insertCategory(category);
                cancel(view);
            } else {
                editTextCategoryTitle.setText(null);
                Toast.makeText(this, repeating_bank_name_warning, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, unfilled_fields_warning, Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View view) {
        finish();
    }

    private boolean areFilled(EditText ... et) {
        for (EditText etTest: et) {
            if (etTest.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isNameUnique(String name) {
        List<Category> categories = categoriesViewModel.getCategoriesForCounting();

        for (Category category: categories) {
            if (category.getName().toLowerCase().equals(name.toLowerCase())) return false;
        }
        return true;
    }
}