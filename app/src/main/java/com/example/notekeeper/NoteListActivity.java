package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.notekeeper.databinding.ActivityNoteListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note_list);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view -> {
            Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
            startActivity(intent);
        }));

        initializeDisplayContent();
    }

    private void initializeDisplayContent() {
        //Referenciando a listview pelo ID
        final ListView listNotes = findViewById(R.id.list_notes);

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

                //Cria a intent da atividade
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);

                // Pegando o item da posição na ListView (listNotes precisa ser final por
                // estar dentro de uma classe anônima). Código comentado depois da refatoração
                //NoteInfo note = (NoteInfo) listNotes.getItemAtPosition(position);

                // Colocando extra note na intent
                intent.putExtra(NoteActivity.NOTE_POSITION, position);

                //Passando a intent para NoteActivity
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_note_list);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}