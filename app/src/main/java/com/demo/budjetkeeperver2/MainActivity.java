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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.demo.budjetkeeperver2.billsdb.Bill;
import com.demo.budjetkeeperver2.billsdb.BillsViewModel;
import com.demo.budjetkeeperver2.categorydb.CategoriesViewModel;
import com.demo.budjetkeeperver2.categorydb.Category;
import com.demo.budjetkeeperver2.notesdb.AddNoteActivity;
import com.demo.budjetkeeperver2.notesdb.Note;
import com.demo.budjetkeeperver2.notesdb.NotesAdapter;
import com.demo.budjetkeeperver2.notesdb.NotesViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static String CURRENCY = "₽";
    public final static String SHARED_PREF_NAME = "appsettings";

    private SharedPreferences sp;

    private RecyclerView recyclerViewShowNotes;

    private NotesAdapter adapter;
    private NotesViewModel viewModel;
    private BillsViewModel billsViewModel;
    private CategoriesViewModel categoriesViewModel;
    private PieChart chartMain;

    private List<Bill> billsFromDb;
    private List<Category> categoriesFromDb;

    private Spinner spinnerMainTime;

    private Calendar calendar;

    private final ArrayList<Note> testArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();

        spinnerMainTime = findViewById(R.id.spinnerMainTime);
        chartMain = findViewById(R.id.chartMain);

        recyclerViewShowNotes = findViewById(R.id.recyclerViewShowNotes);
        viewModel = ViewModelProviders.of(this).get(NotesViewModel.class);
        billsViewModel = ViewModelProviders.of(this).get(BillsViewModel.class);
        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

        billsFromDb = billsViewModel.getBillsForCounting();
        categoriesFromDb = categoriesViewModel.getCategoriesForCounting();

        spinnerMainTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                System.out.println("GOT CHANGED");
                switch (position) {
                    case 1:
                        getWeek();
                        break;
                    case 2:
                        getMonth();
                        break;
                    case 3:
                        getYear();
                        break;
                    default:
                        viewModel.setMinDate(0);
                        break;
                }
                calendar = Calendar.getInstance();
                getData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                setChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        if (!sp.contains("currency")) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("currency", CURRENCY);
            editor.apply();
        }

        getData();
        adapter = new NotesAdapter(testArr);
        recyclerViewShowNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewShowNotes.setAdapter(adapter);

        setChart();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());
            }


        });

        itemTouchHelper.attachToRecyclerView(recyclerViewShowNotes);

        adapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                Note note = adapter.getNotes().get(position);
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("id", note.getId());
                intent.putExtra("title", note.getTitle());
                intent.putExtra("price", note.getPrice());
                intent.putExtra("category", note.getCategory());
                intent.putExtra("bill", note.getBillId());
                intent.putExtra("date", note.getDate());
                startActivity(intent);
            }
        });
    }

    public void addNewNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    public void goToShowBillsActivity(View view) {
        Intent intent = new Intent(this, MenuBillsActivity.class);
        startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void getData() {
        if (viewModel.getNotes() != null) {
            LiveData<List<Note>> notesFromDB = viewModel.getNotes();
            notesFromDB.observe(this, new Observer<List<Note>>() {
                @Override
                public void onChanged(List<Note> notes) {
                    adapter.setNotes(notes);
                }
            });
        }
    }

    private void getWeek() {
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        viewModel.setMinDate(calendar.getTimeInMillis());
    }

    private void getMonth() {
        int month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (month == 1) {
            calendar.add(Calendar.YEAR, -1);
            calendar.add(Calendar.MONTH, 11);
        } else {
            calendar.add(Calendar.MONTH, -1);
        }

        viewModel.setMinDate(calendar.getTimeInMillis());
    }

    private void getYear() {
        calendar.add(Calendar.YEAR, -1);

        viewModel.setMinDate(calendar.getTimeInMillis());
    }

    private void setChart() {
        chartMain.clear();

        List<PieEntry> entries = new ArrayList<>();
        List<Note> notes = viewModel.getValues();
        System.out.println(notes.size());
        Map<String, Double> mapCats = new HashMap<>();

        double size = 0;
        for (Note note: notes) {
            if (mapCats.containsKey(note.getCategory())) {
                double k = mapCats.get(note.getCategory());
                mapCats.put(note.getCategory(), k + Math.abs(note.getPrice()));
            } else {
                mapCats.put(note.getCategory(), Math.abs(note.getPrice()));
            }
            size += Math.abs(note.getPrice());
        }

        for (String key: mapCats.keySet()) {
            double per = mapCats.get(key);
            double p = (per / size) * 100;
            entries.add(new PieEntry((float) p, key));
        }

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        PieDataSet set = new PieDataSet(entries, " ");
        set.setDrawIcons(false);
        set.setSliceSpace(3f);
        set.setIconsOffset(new MPPointF(0, 40));
        set.setSelectionShift(5f);

        set.setColors(colors);
        PieData data = new PieData(set);
        chartMain.setHoleRadius(90);
        chartMain.setEntryLabelColor(R.color.purple_700);
        chartMain.setNoDataText(getString(R.string.no_notes_warning_pie_chart));
        chartMain.setData(data);
        chartMain.invalidate();
    }

    private void remove(int position) {
        Note note = adapter.getNotes().get(position);

        Bill oldBill = null;
        for (Bill b: billsFromDb) {
            if (b.getId() == note.getBillId()) {
                oldBill = b;
            }
        }

        double price = note.getPrice();

        if (oldBill != null) {
            double priceToChange = oldBill.getMoney() - price;
            Bill bill = new Bill(oldBill.getId(), oldBill.getTitle(), oldBill.getBankName(), priceToChange);

            billsViewModel.updateBill(bill);
        }

        viewModel.deleteNote(note);
        setChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}