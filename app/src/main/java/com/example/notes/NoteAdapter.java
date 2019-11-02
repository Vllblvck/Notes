package com.example.notes;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;
    private List<Note> selectedNotes = new LinkedList<>();
    private List<CheckBox> checkBoxes = new LinkedList<>();
    private NoteListener clickListener;
    private boolean multiSelect = false;
    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            showCheckBoxes();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if (item.getItemId() == R.id.action_delete) {
                clickListener.onNoteDelete(selectedNotes);
                mode.finish();
                return true;
            }

            if (item.getItemId() == R.id.action_select_all) {

                if (selectedNotes.size() == notes.size()) {
                    selectedNotes.clear();

                    for (CheckBox checkBox : checkBoxes) {
                        checkBox.setChecked(false);
                    }

                    return true;
                }

                selectedNotes.clear();
                selectedNotes.addAll(notes);

                for (CheckBox checkBox : checkBoxes) {
                    checkBox.setChecked(true);
                }

                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            multiSelect = false;
            selectedNotes.clear();
            hideCheckBoxes();
        }
    };

    public NoteAdapter(List<Note> notes, NoteListener clickListener) {
        this.notes = notes;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_view_holder, parent, false);

        return new NoteViewHolder(view, clickListener);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        holder.content.setText(notes.get(position).getContent());
        holder.creationDate.setText(notes.get(position).getCreationDate());
        checkBoxes.add(holder.checkBox);
    }

    private void startActionMode(View view) {
        if (actionMode != null) {
            return;
        }

        MainActivity activityContext = (MainActivity) view.getContext();
        actionMode = activityContext.startSupportActionMode(actionModeCallback);
        multiSelect = true;
    }

    private void selectItem(int position, CheckBox checkBox) {
        Note item = notes.get(position);

        if (!selectedNotes.contains(item)) {
            selectedNotes.add(item);
            checkBox.setChecked(true);
        } else {
            selectedNotes.remove(item);
            checkBox.setChecked(false);
        }
    }

    private void showCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    private void hideCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public interface NoteListener {
        void onNoteClick(int position);

        void onNoteDelete(List<Note> toDelete);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView content;
        public TextView creationDate;
        public CheckBox checkBox;

        public NoteViewHolder(final View view, final NoteListener clickListener) {
            super(view);
            content = view.findViewById(R.id.text_view_note);
            creationDate = view.findViewById(R.id.text_view_date);
            checkBox = view.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.INVISIBLE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!multiSelect) {
                        clickListener.onNoteClick(getAdapterPosition());
                    } else {
                        selectItem(getAdapterPosition(), checkBox);
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    startActionMode(view);
                    selectItem(getAdapterPosition(), checkBox);
                    return true;
                }
            });
        }
    }

}

