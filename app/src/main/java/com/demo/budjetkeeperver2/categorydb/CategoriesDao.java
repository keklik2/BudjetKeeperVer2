package com.demo.budjetkeeperver2.categorydb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.demo.budjetkeeperver2.billsdb.Bill;

import java.util.List;

@Dao
public interface CategoriesDao {

    @Query("SELECT * FROM categories ORDER BY id")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories ORDER BY id")
    List<Category> getAllCategoriesForCounting();

    @Query("SELECT * FROM categories WHERE id == :categoryId")
    Category getCategoryById(int categoryId);

    @Insert
    void insertCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("DELETE FROM categories")
    void deleteAllCategories();
}
