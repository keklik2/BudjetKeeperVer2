package com.demo.budjetkeeperver2.billsdb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.budjetkeeperver2.R;

import java.util.List;

import static com.demo.budjetkeeperver2.MainActivity.SHARED_PREF_NAME;
import static com.demo.budjetkeeperver2.R.string.repeating_bank_name_warning;
import static com.demo.budjetkeeperver2.R.string.unfilled_fields_warning;

public class AddBillActivity extends AppCompatActivity {

    private EditText editTextBillTitle;
    private EditText editTextBillPrice;
    private EditText editTextBillBankName;
    private TextView textViewBillCurrency;

    private SharedPreferences sp;

    private BillsViewModel billsViewModel;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        billsViewModel = ViewModelProviders.of(this).get(BillsViewModel.class);

        editTextBillTitle = findViewById(R.id.editTextBillTitle);
        editTextBillPrice = findViewById(R.id.editTextBillPrice);
        editTextBillBankName = findViewById(R.id.editTextBillBankName);
        textViewBillCurrency = findViewById(R.id.textViewBillCurrency);

        setCurrencyLabel();

        intent = getIntent();
        if (intent.hasExtra("id") && intent.getIntExtra("id", -1) >= 0) {
            loadFieldsFromIntent(intent);
        }
    }

    private void setCurrencyLabel() {
        if (sp.contains("currency")) {
            textViewBillCurrency.setText(sp.getString("currency", "none"));
        } else {
            textViewBillCurrency.setText("none");
        }
    }

    public void apply(View view) {
        if (areFilled(editTextBillTitle, editTextBillBankName, editTextBillPrice)) {

            String title = editTextBillTitle.getText().toString();
            if (isNameUnique(title) || intent.hasExtra("id")) {
                String bankName = editTextBillBankName.getText().toString();

                double money = Double.parseDouble(editTextBillPrice.getText().toString());

               if (!intent.hasExtra("id")) {
                    Bill bill = new Bill(title, bankName, money);
                    billsViewModel.insertBill(bill);
                } else {
                    int id = intent.getIntExtra("id", -1);
                   Bill bill = new Bill(id, title, bankName, money);
                   billsViewModel.updateBill(bill);
                }
                cancel(view);
            } else {
                editTextBillTitle.setText(null);
                Toast.makeText(this, repeating_bank_name_warning, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, unfilled_fields_warning, Toast.LENGTH_SHORT).show();
        }
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
        List<Bill> bills = billsViewModel.getBillsForCounting();

        for (Bill bill: bills) {
            if (bill.getTitle().toLowerCase().equals(name.toLowerCase())) return false;
        }
        return true;
    }

    public void cancel(View view) {
        finish();
    }

    @SuppressLint("DefaultLocale")
    private void loadFieldsFromIntent(Intent intent) {
        editTextBillTitle.setText(intent.getStringExtra("title"));
        editTextBillPrice.setText(String.format("%.2f", intent.getDoubleExtra("money", 0)).replace(',', '.'));
        editTextBillBankName.setText(intent.getStringExtra("bankName"));
        textViewBillCurrency.setText(sp.getString("currency", "none"));
    }
}