package com.demo.budjetkeeperver2.notesdb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.budjetkeeperver2.MainActivity;
import com.demo.budjetkeeperver2.R;
import com.demo.budjetkeeperver2.billsdb.AddBillActivity;
import com.demo.budjetkeeperver2.billsdb.Bill;
import com.demo.budjetkeeperver2.billsdb.BillsViewModel;
import com.demo.budjetkeeperver2.categorydb.AddCategoryActivity;
import com.demo.budjetkeeperver2.categorydb.CategoriesViewModel;
import com.demo.budjetkeeperver2.categorydb.Category;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.demo.budjetkeeperver2.MainActivity.SHARED_PREF_NAME;
import static com.demo.budjetkeeperver2.R.string.not_enought_money_warning;
import static com.demo.budjetkeeperver2.R.string.unfilled_fields_warning;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextNoteTitle;
    private EditText editTextNotePrice;
    private TextView textViewNoteCurrency;
    private TextView textViewNoteDate;
    private Spinner spinnerNoteCategory;
    private Spinner spinnerNoteBill;
    private Button buttonNoteAddNewCategory;
    private Button buttonNoteAddNewBill;

    private BillsViewModel billsViewModel;
    private CategoriesViewModel categoriesViewModel;
    private NotesViewModel notesViewModel;

    private List<Category> categoriesFromDb;
    private List<Bill> billsFromDb;
    private double oldPrice;

    private SharedPreferences sp;

    private DatePickerDialog.OnDateSetListener dateListener;
    private Calendar cal;
    private SimpleDateFormat form = new SimpleDateFormat("dd.MM.yy, HH:mm", Locale.getDefault());

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        cal = new GregorianCalendar();

        billsViewModel = ViewModelProviders.of(this).get(BillsViewModel.class);
        categoriesViewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNotePrice = findViewById(R.id.editTextNotePrice);
        textViewNoteCurrency = findViewById(R.id.textViewNoteCurrency);
        textViewNoteDate = findViewById(R.id.textViewNoteDate);
        spinnerNoteCategory = findViewById(R.id.spinnerNoteCategory);
        spinnerNoteBill = findViewById(R.id.spinnerNoteBill);
        buttonNoteAddNewCategory = findViewById(R.id.buttonNoteAddNewCategory);
        buttonNoteAddNewBill = findViewById(R.id.buttonNoteAddNewBill);

        categoriesFromDb = categoriesViewModel.getCategoriesForCounting();
        billsFromDb = billsViewModel.getBillsForCounting();

        setCurrencyLabel();
        setCategoriesSpinner();
        setBillsSpinner();
        setCurrentDate();

        intent = getIntent();
        if (intent.hasExtra("id") && intent.getIntExtra("id", -1) >= 0) {
            loadFieldsFromIntent(intent);

            oldPrice = Double.parseDouble(editTextNotePrice.getText().toString());
            if (isNegativeCategoryPrice(spinnerNoteCategory.getSelectedItemPosition()) && oldPrice != 0) {
                oldPrice *= -1;
            }
        }

        textViewNoteDate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddNoteActivity.this, dateListener, year, month, day);
                dialog.getWindow();
                dialog.show();
            }
        });
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                textViewNoteDate.setText(form.format(cal.getTime()));
            }
        };
    }

    public void addNewBill(View view) {
        Intent intent = new Intent(this, AddBillActivity.class);
        startActivity(intent);
    }

    public void addNewCategory(View view) {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        startActivity(intent);
    }

    public void apply(View view) {
        if (areFilled(editTextNoteTitle, editTextNotePrice)) {
            String title = editTextNoteTitle.getText().toString();

            if (categoriesFromDb.size() > 0 || billsFromDb.size() > 0) {
                String category = spinnerNoteCategory.getSelectedItem().toString();
                int billId = getBillDbId(spinnerNoteBill.getSelectedItemPosition());
                double price = Double.parseDouble(editTextNotePrice.getText().toString());
                if (isNegativeCategoryPrice(spinnerNoteCategory.getSelectedItemPosition()) && price != 0) {
                    price *= -1;
                }

                int type = -1;
                long date = cal.getTime().getTime();

                Bill oldBill = null;
                for (Bill b: billsFromDb) {
                    if (b.getId() == billId) {
                        oldBill = b;
                    }
                }

                if (!intent.hasExtra("id")) {
                    if (oldBill != null) {
                        if ((price < 0 && oldBill.getMoney() + price >= 0) || price > 0) {
                            double priceToChange = oldBill.getMoney() + price;
                            Bill bill = new Bill(oldBill.getId(), oldBill.getTitle(), oldBill.getBankName(), priceToChange);
                            billsViewModel.updateBill(bill);

                            Note note = new Note(title, category, billId, price, type, date);
                            notesViewModel.insertNote(note);
                            cancel(view);
                        } else {
                            Toast.makeText(this, not_enought_money_warning, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (oldBill != null) {
                        int id = intent.getIntExtra("id", -1);

                        if ((price < 0 && oldBill.getMoney() + price >= 0) || price > 0) {
                            double priceToChange = oldBill.getMoney() - (oldPrice - price);
                            Bill bill = new Bill(oldBill.getId(), oldBill.getTitle(), oldBill.getBankName(), priceToChange);
                            billsViewModel.updateBill(bill);

                            Note note = new Note(id, title, category, billId, price, type, date);
                            notesViewModel.updateNote(note);
                            cancel(view);
                        } else {
                            Toast.makeText(this, not_enought_money_warning, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Toast.makeText(this, R.string.zero_db_bills_or_categories_warning, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, unfilled_fields_warning, Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean areFilled(EditText ... et) {
        for (EditText etTest: et) {
            if (etTest.getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isNegativeCategoryPrice(int categoryId) {
        return categoriesFromDb.get(categoryId).getType() == 0;
    }

    private void loadFieldsFromIntent(Intent intent) {
        buttonNoteAddNewCategory.setVisibility(View.INVISIBLE);
        buttonNoteAddNewBill.setVisibility(View.INVISIBLE);

        editTextNoteTitle.setText(intent.getStringExtra("title"));

        double insidePrise = intent.getDoubleExtra("price", 0.00);
        if (insidePrise < 0) insidePrise *= -1;
        editTextNotePrice.setText(Double.toString(insidePrise));

        Date date = new Date(intent.getLongExtra("date", 0));
        SimpleDateFormat form = new SimpleDateFormat("MM.dd.yy", Locale.getDefault());
        textViewNoteDate.setText(form.format(date));

        final boolean[] isFirstLoad = {true, true};
        spinnerNoteCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstLoad[0]) {
                    String catName = intent.getStringExtra("category");
                    for (int i = 0; i < categoriesFromDb.size(); i++) {
                        if (categoriesFromDb.get(i).getName().toLowerCase().equals(catName.toLowerCase())) {
                            spinnerNoteCategory.setSelection(i, false);
                            isFirstLoad[0] = false;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerNoteBill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstLoad[1]) {
                    int bilId = intent.getIntExtra("bill", -1);
                    for (int i = 0; i < billsFromDb.size(); i++) {
                        if (billsFromDb.get(i).getId() == bilId) {
                            spinnerNoteBill.setSelection(i, false);
                            isFirstLoad[1] = false;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setBillsSpinner() {
        ArrayList<String> bills = new ArrayList<>();

        for (Bill bill: billsFromDb) {
            bills.add(String.format("%s (%.2f)", bill.getTitle(), bill.getMoney()));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bills);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNoteBill.setAdapter(adapter);
    }

    private void setCurrentDate() {
        textViewNoteDate.setText(form.format(cal.getTime()));
    }

    private void setCategoriesSpinner() {
        ArrayList<String> categories = new ArrayList<>();

        for (Category cat: categoriesFromDb) {
            categories.add(cat.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNoteCategory.setAdapter(adapter);
    }

    private void setCurrencyLabel() {
        if (sp.contains("currency")) {
            textViewNoteCurrency.setText(sp.getString("currency", "none"));
        } else {
            textViewNoteCurrency.setText("none");
        }
    }

    private int getBillDbId(int billId) {
        return billsFromDb.get(billId).getId();
    }


    @Override
    protected void onResume() {
        super.onResume();
        categoriesFromDb = categoriesViewModel.getCategoriesForCounting();
        billsFromDb = billsViewModel.getBillsForCounting();
        setCategoriesSpinner();
        setBillsSpinner();
    }
}