package com.demo.budjetkeeperver2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.budjetkeeperver2.JSON.api.APIFactory;
import com.demo.budjetkeeperver2.JSON.api.APIService;
import com.demo.budjetkeeperver2.JSON.pojo.Currency;
import com.demo.budjetkeeperver2.billsdb.Bill;
import com.demo.budjetkeeperver2.billsdb.BillsViewModel;
import com.demo.budjetkeeperver2.notesdb.Note;
import com.demo.budjetkeeperver2.notesdb.NotesViewModel;

import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.demo.budjetkeeperver2.MainActivity.SHARED_PREF_NAME;
import static com.demo.budjetkeeperver2.R.string.enternet_connection_error_warning;

public class SettingsActivity extends AppCompatActivity {
    /** Перевести переменные с символами валюты в отдельный ENUm */
    private final String CURRENCY_RUBLE_LABEL = "₽";
    private final String CURRENCY_DOLLAR_LABEL = "$";

    private TextView textViewSettingsCurrency;
    private Button buttonSettingsSwitchToRubles;
    private Button buttonSettingsSwitchToDollars;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private NotesViewModel notesViewModel;
    private BillsViewModel billsViewModel;
    private Date date;

    private Disposable disposable;
    private int purpleColorId;
    private int blackColorId;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sp.edit();

        purpleColorId = ResourcesCompat.getColor(getResources(), R.color.purple_700, null);
        blackColorId = ResourcesCompat.getColor(getResources(), R.color.black, null);

        billsViewModel = ViewModelProviders.of(this).get(BillsViewModel.class);
        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        textViewSettingsCurrency = findViewById(R.id.textViewSettingsCurrency);
        buttonSettingsSwitchToRubles = findViewById(R.id.buttonSettingsSwitchToRubles);
        buttonSettingsSwitchToDollars = findViewById(R.id.buttonSettingsSwitchToDollars);

        date = new Date();

        if (!wasUpdatedToday()) {
            System.out.println("GOT HERE");
            APIFactory apiFactory = APIFactory.getInstance();
            APIService apiService = apiFactory.getApiService();
            disposable = apiService.getCurrencies()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Currency>() {
                        @Override
                        public void accept(Currency currency) throws Exception {
                            double usdrub = currency.getQuotes().getUsdrub();
                            editor.putFloat("usdrub", (float) usdrub);
                            editor.putLong("lastCurrencyUpdateDate", date.getTime());
                            editor.apply();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                        }
                    });
        }

        setInfoAccordingToSP();
    }

    public void switchToRubles(View view) {
        double usdrub = -1;
        if (sp.contains("usdrub") && sp.getFloat("usdrub", -1) != -1) {
            usdrub = sp.getFloat("usdrub", -1);
            editor.putString("currency", CURRENCY_RUBLE_LABEL);
            editor.apply();
        }
        changeAllPrices(usdrub);
    }

    public void switchToDollars(View view) {
        double rubusd = -1;
        if (sp.contains("usdrub") && sp.getFloat("usdrub", -1) != -1) {
            rubusd = 1 / sp.getFloat("usdrub", -1);
            editor.putString("currency", CURRENCY_DOLLAR_LABEL);
            editor.apply();
        }
        changeAllPrices(rubusd);
    }

    public void cancel(View view) {
        finish();
    }

    private void setInfoAccordingToSP() {
        if (sp.contains("currency")) {
            String currencyLabel = sp.getString("currency", "none");

            switch (currencyLabel) {
                case CURRENCY_RUBLE_LABEL:
                    buttonSettingsSwitchToRubles.setClickable(false);
                    buttonSettingsSwitchToRubles.setTextColor(blackColorId);
                    buttonSettingsSwitchToDollars.setTextColor(purpleColorId);
                    break;
                case CURRENCY_DOLLAR_LABEL:
                    buttonSettingsSwitchToDollars.setClickable(false);
                    buttonSettingsSwitchToDollars.setTextColor(blackColorId);
                    buttonSettingsSwitchToRubles.setTextColor(purpleColorId);
                    break;
                default:
                    buttonSettingsSwitchToDollars.setTextColor(purpleColorId);
                    buttonSettingsSwitchToRubles.setTextColor(purpleColorId);
                    break;
            }

            textViewSettingsCurrency.setText(currencyLabel);
        }
    }

    private void changeAllPrices(double ratio) {
        if (ratio != -1) {
            for (Note note : notesViewModel.getNotesForCounting()) {
                double newPrice = note.getPrice() * ratio;
                notesViewModel.updateNote(new Note(note.getId(), note.getTitle(), note.getCategory(), note.getBillId(), newPrice, note.getType(), note.getDate()));
            }

            for (Bill bill : billsViewModel.getBillsForCounting()) {
                double newMoney = bill.getMoney() * ratio;
                billsViewModel.updateBill(new Bill(bill.getId(), bill.getTitle(), bill.getBankName(), newMoney));
            }
            setInfoAccordingToSP();
        } else {
            Toast.makeText(this, enternet_connection_error_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean wasUpdatedToday() {
        if (sp.contains("lastCurrencyUpdateDate")) {
            Date oldDate = new Date(sp.getLong("lastCurrencyUpdateDate", -1));
            System.out.println("OLD DAY: " + oldDate.getDay());
            System.out.println("NEW DAY: " + date.getDay());
            if (oldDate.getDay() == date.getDay()) return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}