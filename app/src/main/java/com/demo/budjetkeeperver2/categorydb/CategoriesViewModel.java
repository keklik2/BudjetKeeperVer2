package com.demo.budjetkeeperver2.categorydb;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CategoriesViewModel extends AndroidViewModel {
    private static CategoriesDatabase database;
    private LiveData<List<Category>> categories;


    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        database = CategoriesDatabase.getInstance(getApplication());
        categories = database.categoriesDao().getAllCategories();
    }

    public List<Category> getCategoriesForCounting() {
        try {
            return new CategoriesViewModel.GetCategoriesForCountingTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertCategory(Category category) {
        new CategoriesViewModel.InsertTask().execute(category);
    }

    public void deleteCategory(Category category) {
        new CategoriesViewModel.DeleteTask().execute(category);
    }

    public Category getCategoryById(int id) {
        try {
            return new CategoriesViewModel.GetCategoryTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllCategories() {
        new CategoriesViewModel.DeleteAllTask().execute();
    }


    private static class InsertTask extends AsyncTask<Category, Void, Void> {

        @Override
        protected Void doInBackground(Category... categories) {
            if (categories != null && categories.length > 0) {
                database.categoriesDao().insertCategory(categories[0]);
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Category, Void, Void> {

        @Override
        protected Void doInBackground(Category... categories) {
            if (categories != null && categories.length > 0) {
                database.categoriesDao().deleteCategory(categories[0]);
            }
            return null;
        }
    }

    private static class DeleteAllTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            database.categoriesDao().deleteAllCategories();
            return null;
        }
    }

    private static class GetCategoryTask extends AsyncTask<Integer, Void, Category> {

        @Override
        protected Category doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.categoriesDao().getCategoryById(integers[0]);
            }
            return null;
        }
    }

    private static class GetCategoriesForCountingTask extends AsyncTask<Void, Void, List<Category>> {

        @Override
        protected List<Category> doInBackground(Void... voids) {
            return database.categoriesDao().getAllCategoriesForCounting();
        }
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }
}
