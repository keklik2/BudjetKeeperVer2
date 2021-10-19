package com.demo.budjetkeeperver2.billsdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.budjetkeeperver2.notesdb.Note;

import java.util.List;

@Dao
public interface BillsDao {

    @Query("SELECT * FROM bills ORDER BY id")
    LiveData<List<Bill>> getAllBills();

    @Query("SELECT * FROM bills ORDER BY money")
    LiveData<List<Bill>> getAllBillsOrderedByMoney();

    @Query("SELECT * FROM bills ORDER BY id")
    List<Bill> getAllBillsForCounting();

    @Query("SELECT * FROM bills WHERE id == :billId")
    Bill getBillById(int billId);

    @Update
    void updateBill(Bill bill);

    @Insert
    void insertBill(Bill bill);

    @Delete
    void deleteBill(Bill bill);

    @Query("DELETE FROM bills")
    void deleteAllBills();
}
