package com.demo.budjetkeeperver2.notesdb;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.budjetkeeperver2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.demo.budjetkeeperver2.MainActivity.SHARED_PREF_NAME;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    private List<Note> notes;
    private OnNoteClickListener onNoteClickListener; // Экземпляр интерфейса, созданного ниже
    private int darkGreenColor;
    private int darkRedColor;

    private SharedPreferences sp;
    private String spCurrency;

    public NotesAdapter(ArrayList<Note> notes) { // Конструктор
        this.notes = notes;
    }

    @NonNull
    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        darkGreenColor = ResourcesCompat.getColor(parent.getResources(), R.color.dark_green, null);
        darkRedColor = ResourcesCompat.getColor(parent.getResources(), R.color.dark_red, null);

        sp = parent.getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return new NotesViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, int position) {
        spCurrency = sp.getString("currency", "none");

        Note note = notes.get(position);
        holder.textViewNoteTitle.setText(note.getTitle());

        double insidePrise = note.getPrice();
        if (insidePrise >= 0) {
            holder.textViewNotePrice.setText(String.format("%.2f %s", insidePrise, spCurrency));
            holder.textViewNotePrice.setTextColor(darkGreenColor);
        } else {
            insidePrise *= -1;
            holder.textViewNotePrice.setText(String.format("%.2f %s", insidePrise, spCurrency));
            holder.textViewNotePrice.setTextColor(darkRedColor);
        }


        Date date = new Date(note.getDate());
        SimpleDateFormat form = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        holder.textViewNoteDate.setText(form.format(date));
    }


    class NotesViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNoteTitle;
        private TextView textViewNotePrice;
        private TextView textViewNoteDate;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNoteTitle = itemView.findViewById(R.id.textViewNoteTitle);
            textViewNotePrice = itemView.findViewById(R.id.textViewNotePrice);
            textViewNoteDate = itemView.findViewById(R.id.textViewNoteDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNoteClickListener != null) {
                        onNoteClickListener.onNoteClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(int position);
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public void setOnNoteClickListener(OnNoteClickListener onNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener;
    }

    public List<Note> getNotes() {
        System.out.println("TEST: " + notes.size());
        return notes;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


}
