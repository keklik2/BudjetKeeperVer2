package com.demo.budjetkeeperver2.notesdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM notes WHERE date >= :minDate ORDER BY date DESC")
    LiveData<List<Note>> getNotes(long minDate);

    @Query("SELECT * FROM notes WHERE date >= :minDate ORDER BY date DESC")
    List<Note> getNotesNotLive(long minDate);

    @Query("SELECT * FROM notes ORDER BY date DESC")
    List<Note> getAllNotesForCounting();

    @Query("SELECT * FROM notes WHERE id == :noteId")
    Note getNoteById(int noteId);

    @Update
    void updateNote(Note note);

    @Insert
    void insertNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("DELETE FROM notes")
    void deleteAllNotes();
}
