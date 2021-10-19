package com.demo.budjetkeeperver2.notesdb;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotesViewModel extends AndroidViewModel {
    private static NotesDatabase database;
    private LiveData<List<Note>> notes;
    private long minDate = 0;


    public NotesViewModel(@NonNull Application application) {
        super(application);
        database = NotesDatabase.getInstance(getApplication());
        notes = database.notesDao().getNotes(minDate);
    }

    public List<Note> getNotesForCounting() {
        try {
            return new GetNotesForCountingTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateNote(Note note) { new UpdateTask().execute(note); }

    public void insertNote(Note note) {
        new InsertTask().execute(note);
    }

    public void deleteNote(Note note) {
        new DeleteTask().execute(note);
    }

    public Note getNoteById(int id) {
        try {
            return new GetNoteByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllNotes() {
        new DeleteAllTask().execute();
    }

    private static class InsertTask extends AsyncTask<Note, Void, Void> {

        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0) database.notesDao().insertNote(notes[0]);
            return null;
        }
    }

    private static class DeleteTask extends AsyncTask<Note, Void, Void> {

        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0) database.notesDao().deleteNote(notes[0]);
            return null;
        }
    }

    private static class DeleteAllTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            database.notesDao().deleteAllNotes();
            return null;
        }
    }

    private static class GetNotesForCountingTask extends AsyncTask<Void, Void, List<Note>> {

        @Override
        protected List<Note> doInBackground(Void... voids) {
            return database.notesDao().getAllNotesForCounting();
        }
    }

    private static class GetNotesNotLiveTask extends AsyncTask<Long, Void, List<Note>> {

        @Override
        protected List<Note> doInBackground(Long... longs) {
            if (longs != null && longs.length > 0) return database.notesDao().getNotesNotLive(longs[0]);
            return null;
        }
    }

    private static class GetNoteByIdTask extends AsyncTask<Integer, Void, Note> {

        @Override
        protected Note doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) return database.notesDao().getNoteById(integers[0]);
            return null;
        }
    }

    private static class UpdateTask extends AsyncTask<Note, Void, Void> {

        @Override
        protected Void doInBackground(Note... notes) {
            if (notes != null && notes.length > 0) database.notesDao().updateNote(notes[0]);
            return null;
        }
    }

    public void setMinDate(long minDate) {
        this.minDate = minDate;
        notes = database.notesDao().getNotes(minDate);
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

    public List<Note> getValues() {
        try {
            return new GetNotesNotLiveTask().execute(minDate).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
