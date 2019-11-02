package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.NoteListener {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    public static final String EXTRA_REQUEST = "com.example.notes.EXTRA_REQUEST";
    public static final String EXTRA_NOTE = "com.example.notes.NOTE";
    private String notesFile = "notes.txt";
    private List<Note> notes = new LinkedList<>();
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        readNotes();
        initRecyclerView();
        initFab();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void initRecyclerView() {
        noteAdapter = new NoteAdapter(notes, this);
        noteAdapter.setNotes(notes);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        dividerItemDecoration.setDrawable(getDrawable(R.drawable.separator_shape));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(noteAdapter);
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra(EXTRA_REQUEST, ADD_NOTE_REQUEST);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra(EXTRA_REQUEST, EDIT_NOTE_REQUEST);
        intent.putExtra(EXTRA_NOTE, notes.get(position));
        startActivityForResult(intent, EDIT_NOTE_REQUEST);
    }

    @Override
    public void onNoteDelete(List<Note> toDelete) {
        for (int i = 0; i < notes.size(); i++) {
            for (int j = 0; j < toDelete.size(); j++) {
                if (notes.get(i).getExactCreationDate().
                        equals(toDelete.get(j).getExactCreationDate())) {
                    notes.remove(i);
                }
            }
        }
        saveNotes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            Note newNote = (Note) data.getSerializableExtra(EXTRA_NOTE);

            if (requestCode == ADD_NOTE_REQUEST) {
                notes.add(newNote);
                Toast.makeText(getApplicationContext(), "Note added", Toast.LENGTH_SHORT).show();
            } else {

                for (int i = 0; i < notes.size(); i++) {
                    if (notes.get(i).getExactCreationDate().equals(newNote.getExactCreationDate())) {
                        notes.set(i, newNote);
                        Toast.makeText(getApplicationContext(), "Note edited", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            saveNotes();
        }
    }

    private void saveNotes() {
        try (FileOutputStream fileOutput = this.openFileOutput(notesFile, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {

            noteAdapter.setNotes(notes);
            objectOutput.writeObject(notes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readNotes() {
        try (FileInputStream fileInput = this.openFileInput(notesFile);
             ObjectInputStream inputStream = new ObjectInputStream(fileInput)) {

            notes = (LinkedList<Note>) inputStream.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
