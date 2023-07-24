package com.carry.obtest;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.Nullable;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class NoteActivity extends Activity {
    private EditText editText;
    private View addNoteButton;

    private Box<Note> notesBox;
    private Query<Note> notesQuery;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setUpViews();
//        notesBox = ObjectBox.get().boxFor(Note.class);
        BoxStore boxStore = MyObjectBox.builder().androidContext(this).build();
        notesBox = boxStore.boxFor(Note.class);
        notesQuery = notesBox.query().order(Note_.text).build();
        updateNotes();
    }

    protected  void setUpViews() {
        ListView listView = findViewById(R.id.listViewNotes);
        listView.setOnItemClickListener(noteClickListener);

        notesAdapter = new NotesAdapter();
        listView.setAdapter(notesAdapter);

        addNoteButton = findViewById(R.id.buttonAdd);
        addNoteButton.setEnabled(false);

        editText = findViewById(R.id.editTextNote);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addNote();
                return true;
            }
            return false;
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;
                addNoteButton.setEnabled(enable);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    AdapterView.OnItemClickListener noteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Note note = notesAdapter.getItem(position);
            notesBox.remove(note);
            Log.d(App.TAG, "Deleted note, ID: " + note.getId());
            updateNotes();
        }
    };

    private void updateNotes() {
        List<Note> notes = notesQuery.find();
        notesAdapter.setNotes(notes);
    }

    private void addNote() {
        String noteText = editText.getText().toString();
        editText.setText("");

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());

        Note note = new Note();
        note.setText(noteText);
        note.setComment(comment);
        note.setDate(new Date());
        notesBox.put(note);
        Log.d(App.TAG, "Inserted new note, ID: " + note.getId());
        updateNotes();
    }

    public void onAddButtonClick(View view) {
        addNote();
    }
}
