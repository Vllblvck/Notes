package com.example.notes.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.notes.App.Note;
import com.example.notes.NoteFileReader;
import com.example.notes.R;

import java.util.ArrayList;
import java.util.List;

public class NoteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NoteRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class NoteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<Note> notes = new ArrayList<>();
    private Context context;
    private int appWidgetId;

    public NoteRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        NoteFileReader noteFileReader = new NoteFileReader(context);
        notes.addAll(noteFileReader.readNotes());
    }

    @Override
    public void onDataSetChanged() {
        NoteFileReader noteFileReader = new NoteFileReader(context);
        notes.addAll(noteFileReader.readNotes());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_notes_item);
        String noteContent = notes.get(position).getContent();
        view.setTextViewText(R.id.widget_note_text, noteContent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
