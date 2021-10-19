package com.demo.budjetkeeperver2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.demo.budjetkeeperver2.billsdb.AddBillActivity;
import com.demo.budjetkeeperver2.billsdb.Bill;
import com.demo.budjetkeeperver2.billsdb.BillsAdapter;
import com.demo.budjetkeeperver2.billsdb.BillsViewModel;
import com.demo.budjetkeeperver2.notesdb.Note;
import com.demo.budjetkeeperver2.notesdb.NotesViewModel;

import java.util.ArrayList;
import java.util.List;

public class MenuBillsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewShowBills;

    private BillsAdapter adapter;
    private BillsViewModel billsViewModel;
    private NotesViewModel notesViewModel;

    private final ArrayList<Bill> testArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_bills);

        recyclerViewShowBills = findViewById(R.id.recyclerViewShowBills);
        billsViewModel = ViewModelProviders.of(this).get(BillsViewModel.class);
        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        getData();
        adapter = new BillsAdapter(testArr);
        recyclerViewShowBills.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewShowBills.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (billsViewModel.getBillsForCounting().size() > 1) {
                    remove(viewHolder.getAdapterPosition());
                } else {
                    Toast.makeText(MenuBillsActivity.this, "Вы пытаетесь удалить последний счёт, его удаление невозможно, т.к. это приведёт к удалению всех записей. " +
                            getString(R.string.last_bill_warning), Toast.LENGTH_SHORT).show();
                }
            }


        });

        itemTouchHelper.attachToRecyclerView(recyclerViewShowBills);

        adapter.setOnBillClickListener(new BillsAdapter.OnBillClickListener() {
            @Override
            public void onBillClick(int position) {
                Bill bill = adapter.getBills().get(position);
                Intent intent = new Intent(MenuBillsActivity.this, AddBillActivity.class);
                intent.putExtra("id", bill.getId());
                intent.putExtra("title", bill.getTitle());
                intent.putExtra("bankName", bill.getBankName());
                intent.putExtra("money", bill.getMoney());
                startActivity(intent);
            }
        });
    }

    public void addNewBill(View view) {
        Intent intent = new Intent(this, AddBillActivity.class);
        startActivity(intent);
    }

    public void goToMainPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void getData() {
        if (billsViewModel.getBills() != null) {
            LiveData<List<Bill>> notesFromDB = billsViewModel.getBills();
            notesFromDB.observe(this, new Observer<List<Bill>>() {
                @Override
                public void onChanged(List<Bill> bills) {
                    adapter.setBills(bills);
                }
            });
        }
    }

    private void remove(int position) {
        Bill bill = adapter.getBills().get(position);

        List<Note> notes  = notesViewModel.getNotesForCounting();
        for (Note note: notes) {
            if (note.getBillId() == bill.getId()) {
                int newBillId = getAnotherBill(bill.getId());
                notesViewModel.updateNote(new Note(note.getId(), note.getTitle(), note.getCategory(), newBillId, note.getPrice(), note.getType(), note.getDate()));
            }
        }

        billsViewModel.deleteBill(bill);
    }

    private int getAnotherBill(int oldBillId) {
        for (Bill bill: billsViewModel.getBillsForCounting()) {
            if (bill.getId() != oldBillId) return bill.getId();
        }
        return -1;
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}