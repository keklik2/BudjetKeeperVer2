package com.demo.budjetkeeperver2.billsdb;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.budjetkeeperver2.MainActivity;
import com.demo.budjetkeeperver2.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.demo.budjetkeeperver2.MainActivity.SHARED_PREF_NAME;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillsViewHolder> {
    private List<Bill> bills;
    private BillsAdapter.OnBillClickListener onBillClickListener;
    private int darkGreenColor;
    private int darkRedColor;

    private SharedPreferences sp;
    private String spCurrency;

    public BillsAdapter(ArrayList<Bill> bills) {
        this.bills = bills;
    }

    @NonNull
    @Override
    public BillsAdapter.BillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item, parent, false);
        darkGreenColor = ResourcesCompat.getColor(parent.getResources(), R.color.dark_green, null);
        darkRedColor = ResourcesCompat.getColor(parent.getResources(), R.color.dark_red, null);

        sp = parent.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        spCurrency = sp.getString("currency", "none");
        return new BillsAdapter.BillsViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull BillsAdapter.BillsViewHolder holder, int position) {
        Bill bill = bills.get(position);
        holder.textViewBillTitle.setText(bill.getTitle());

        double insideMoney = bill.getMoney();
        holder.textViewBillMoney.setText(String.format(String.format("%.2f %s", insideMoney, spCurrency)));
        if (insideMoney > 0) {
            holder.textViewBillMoney.setTextColor(darkGreenColor);
        } else {
            holder.textViewBillMoney.setTextColor(darkRedColor);
        }
    }

    class BillsViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewBillTitle;
        private final TextView textViewBillMoney;

        public BillsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBillTitle = itemView.findViewById(R.id.textViewBillTitle);
            textViewBillMoney = itemView.findViewById(R.id.textViewBillMoney);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBillClickListener != null) {
                        onBillClickListener.onBillClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnBillClickListener {
        void onBillClick(int position);
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
        notifyDataSetChanged();
    }

    public void setOnBillClickListener(BillsAdapter.OnBillClickListener onBillClickListener) {
        this.onBillClickListener = onBillClickListener;
    }

    public List<Bill> getBills() {
        return bills;
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }
}
