package com.demo.budjetkeeperver2.billsdb;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BillsViewModel extends AndroidViewModel {
    private static BillsDatabase database;
    private LiveData<List<Bill>> bills;


    public BillsViewModel(@NonNull Application application) {
        super(application);
        database = BillsDatabase.getInstance(getApplication());
        bills = database.billsDao().getAllBills();
    }

    public LiveData<List<Bill>> getBills() {
        return bills;
    }

    public void updateBill(Bill bill) { new UpdateTask().execute(bill); }

    public List<Bill> getBillsForCounting() {
        try {
            return new GetBillsForCountingTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertBill(Bill bill) {
        new BillsViewModel.InsertTask().execute(bill);
    }

    public void deleteBill(Bill bill) {
        new BillsViewModel.DeleteTask().execute(bill);
    }

    public Bill getBillById(int id) {
        try {
            return new GetBillTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllBills() {
        new DeleteAllTask().execute();
    }


    private static class InsertTask extends AsyncTask<Bill, Void, Void> {

        @Override
        protected Void doInBackground(Bill... bills) {
            if (bills != null && bills.length > 0) {
                database.billsDao().insertBill(bills[0]);
            }
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Bill, Void, Void> {

        @Override
        protected Void doInBackground(Bill... bills) {
            if (bills != null && bills.length > 0) {
                database.billsDao().deleteBill(bills[0]);
            }
            return null;
        }
    }

    private static class DeleteAllTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            database.billsDao().deleteAllBills();
            return null;
        }
    }

    private static class GetBillTask extends AsyncTask<Integer, Void, Bill> {

        @Override
        protected Bill doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return database.billsDao().getBillById(integers[0]);
            }
            return null;
        }
    }

    private static class GetBillsForCountingTask extends AsyncTask<Void, Void, List<Bill>> {

        @Override
        protected List<Bill> doInBackground(Void... voids) {
            return database.billsDao().getAllBillsForCounting();
        }
    }

    private static class UpdateTask extends AsyncTask<Bill, Void, Void> {

        @Override
        protected Void doInBackground(Bill... bills) {
            if (bills != null && bills.length > 0) database.billsDao().updateBill(bills[0]);
            return null;
        }
    }
}
