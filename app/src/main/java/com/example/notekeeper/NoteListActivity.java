package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.notekeeper.databinding.ActivityNoteListBinding;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityNoteListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.list_notes);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeDisplayContent();


    }

    private void initializeDisplayContent() {

        //Referenciando a listview pelo ID
        ListView listNotes = findViewById(R.id.list_notes);

        //Recuperando uma lista de notas
        List<NoteInfo> notes = DataManager.getInstance().getNotes();

        //Criando adaptador, passando contexto, item pronto do android e a lista de notas
        ArrayAdapter<NoteInfo> adapterNotes =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);

        //Settando adaptador
        listNotes.setAdapter(adapterNotes);

        //Definindo o clicklistener com uma classe anônima na interface
        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Cria a intent da atividade, e depois starta
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.list_notes);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}