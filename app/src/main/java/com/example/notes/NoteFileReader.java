package com.example.notes;

import android.content.Context;

import com.example.notes.App.Note;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NoteFileReader {
    private static final String notesFile = "notes.txt";
    private Context context;

    public NoteFileReader(Context context) {
        this.context = context;
    }

    public List<Note> readNotes() {
        List<Note> notes = new ArrayList<>();

        try (FileInputStream fileInput = context.openFileInput(notesFile);
             ObjectInputStream inputStream = new ObjectInputStream(fileInput)) {

            notes = (ArrayList<Note>) inputStream.readObject();
            return notes;

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return notes;
    }

    public void saveNotes(List<Note> notes) {
        try (FileOutputStream fileOutput = context.openFileOutput(notesFile, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {

            objectOutput.writeObject(notes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
