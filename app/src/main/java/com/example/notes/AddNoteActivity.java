package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class AddNoteActivity extends AppCompatActivity {
    private EditText noteEditText;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setTitle(R.string.string_edit_note);
        noteEditText = findViewById(R.id.note_content);
        initToolbar();
        checkRequest();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void checkRequest() {
        Intent intent = getIntent();
        int requestCode = intent.getExtras().getInt(MainActivity.EXTRA_REQUEST);
        TextView creationDate = findViewById(R.id.note_date);

        if (requestCode == MainActivity.EDIT_NOTE_REQUEST) {
            note = (Note) intent.getExtras().getSerializable(MainActivity.EXTRA_NOTE);
            creationDate.setText(note.getCreationDate());
            noteEditText.setText(note.getContent());
        }

        if (requestCode == MainActivity.ADD_NOTE_REQUEST) {
            noteEditText.requestFocus();
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = DateFormat.getDateInstance();
            creationDate.setText(dateFormat.format(calendar.getTime()));
            showKeyboard();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addnote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_note:
                saveData();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        hideKeyboard();
        finish();
    }

    private void saveData() {
        Intent saveIntent = new Intent();
        String noteContent = noteEditText.getText().toString();

        if (note != null) {
            note.setContent(noteContent);
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = DateFormat.getDateInstance();
            note.setCreationDate(dateFormat.format(calendar.getTime()));
        } else {
            note = new Note(noteContent);
        }

        hideKeyboard();
        saveIntent.putExtra(MainActivity.EXTRA_NOTE, note);
        setResult(Activity.RESULT_OK, saveIntent);
        finish();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
