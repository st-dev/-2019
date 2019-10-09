package com.example.user.notepad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class NoteSelect extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";
    private List<NotesBuilder> notesList = new ArrayList<>();
    private NotesAdapter nAdapter;
    private RecyclerView notesRecycler;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        notesRecycler = (RecyclerView) findViewById(R.id.notes);

        prepareNotes();//将文件中的数据载入notesList
        final File directory = getFilesDir();
        nAdapter = new NotesAdapter(notesList, directory, notesRecycler);
        nAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent myIntent = new Intent(NoteSelect.this, MainActivity.class);
                File[] files = directory.listFiles();
                String fname = files[position].getName();
                myIntent.putExtra(EXTRA_MESSAGE, fname);
                startActivity(myIntent);
            }
        });//点击一条笔记的view时，进入编辑界面，并传入笔记标题
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        notesRecycler.setLayoutManager(mLayoutManager);
        notesRecycler.setItemAnimator(new DefaultItemAnimator());
        notesRecycler.setAdapter(nAdapter);

        //设置view上的左划动作
        ItemTouchHelper.Callback callback = new myItemTouchHelperCallBack(nAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(notesRecycler);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(NoteSelect.this, MainActivity.class);
                startActivity(myIntent);
            }
        });//新建笔记
    }

    private void prepareNotes() {
        File directory = getFilesDir();
        File[] files = directory.listFiles();
        String fname;
        for (int i = 1; i <= files.length; i++) {
            fname = files[i-1].getName();
            NotesBuilder note = new NotesBuilder(fname, Open(fname));
            notesList.add(note);
        }
    }

    public String Open(String fileName) {
        String content = "";
        try {
            InputStream in = openFileInput(fileName);
            if (in != null) {
                InputStreamReader tmp = new InputStreamReader( in );
                BufferedReader reader = new BufferedReader(tmp);
                String str;
                StringBuilder buf = new StringBuilder();
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }
                in .close();
                content = buf.toString();
            }
        }
        catch (java.io.FileNotFoundException e) {}
        catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return content;
    }
}
