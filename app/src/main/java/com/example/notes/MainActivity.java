package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    private String notesFile = "notes.txt";
    private LinkedList<Note> notes = new LinkedList<>();
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
        noteAdapter = new NoteAdapter(notes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        dividerItemDecoration.setDrawable(getDrawable(R.drawable.separator_shape));
        noteAdapter.setNotes(notes);
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
                startAddNoteActivity();
            }
        });
    }

    private void startAddNoteActivity() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivityForResult(intent, ADD_NOTE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == ADD_NOTE_REQUEST) {
            if(resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra(AddNoteActivity.NOTE_EXTRA);
                notes.add(note);
                saveNotes();
            }
        }
    }

    private void saveNotes() {
        try (FileOutputStream fileOutput = this.openFileOutput(notesFile, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {

            noteAdapter.setNotes(notes);
            objectOutput.writeObject(notes);

            Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_LONG).show();
        }
    }

    private void readNotes() {
        try (FileInputStream fileInput = this.openFileInput(notesFile);
             ObjectInputStream inputStream = new ObjectInputStream(fileInput)) {

            notes = (LinkedList<Note>) inputStream.readObject();

        } catch (IOException e) {
           Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
