package com.example.notes.App;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.NoteFileReader;
import com.example.notes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.NoteListener, Filterable {
    public static final String EXTRA_REQUEST = "com.example.notes.EXTRA_REQUEST";
    public static final String EXTRA_NOTE = "com.example.notes.NOTE";
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    private List<Note> notes = new ArrayList<>();
    private NoteAdapter noteAdapter;
    private NoteFileReader fileReader;
    private Filter notesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(notes);
            } else {
                String filterPattern = constraint.toString().toLowerCase();

                for (Note note : notes) {
                    if (note.getContent().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Note> filteredList = (List<Note>) results.values;
            noteAdapter.setNotes(filteredList);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileReader = new NoteFileReader(this);
        notes.addAll(fileReader.readNotes());
        buildToolbar();
        buildRecyclerView();
        buildFab();
    }

    private void buildToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void buildRecyclerView() {
        noteAdapter = new NoteAdapter(this);
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

    private void buildFab() {
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
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra(EXTRA_REQUEST, EDIT_NOTE_REQUEST);
        intent.putExtra(EXTRA_NOTE, note);
        startActivityForResult(intent, EDIT_NOTE_REQUEST);
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

            saveData();
        }
    }

    @Override
    public void onNotesDelete(List<Note> toDelete) {
        Iterator<Note> notesIter = notes.iterator();

        while (notesIter.hasNext()) {
            Note note = notesIter.next();

            for (Note delNote : toDelete) {
                if (note.getExactCreationDate().equals(delNote.getExactCreationDate())) {
                    notesIter.remove();
                }
            }
        }

        saveData();
    }

    private void saveData() {
        noteAdapter.setNotes(notes);
        fileReader.saveNotes(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public Filter getFilter() {
        return notesFilter;
    }
}
