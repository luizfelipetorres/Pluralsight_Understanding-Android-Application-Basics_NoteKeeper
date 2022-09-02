package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.notekeeper.databinding.ActivityNoteBinding;

import java.util.List;


public class NoteActivity extends AppCompatActivity {

    /**
     * Salvar a position passada pela intent de NoteListActivity
     */
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;

    private AppBarConfiguration appBarConfiguration;
    private ActivityNoteBinding binding;

    /**
     * Armazena as configurações da nota
     */
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewModel;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null)
            mViewModel.saveState(outState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState != null)
            mViewModel.saveState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * Metodo chamado ao criar a activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Definir viewModel para armazenar o estado da activity
        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));

        mViewModel = viewModelProvider.get(NoteActivityViewModel.class);

        if (savedInstanceState != null && mViewModel.mIsNewlyCreated)
            mViewModel.restoreState(savedInstanceState);


        mViewModel.mIsNewlyCreated = false;

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Referência para campos pelo id
        mSpinnerCourses = findViewById(R.id.spinner_courses);
        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        //Capturando a lista de cursos
        List<CourseInfo> courses = DataManager.getInstance().getCourses();

        //Criando adaptador para spinner (gerencia layouts e dados)
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);

        //Selecionando o tipo padrão de dropdown
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Settar adaptador no spinner
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        saveOriginalNoteValues();

        //Se não foruma nota nova, iniciar a nota já criada
        if (!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

    }

    /**
     * Salvar valores originais da nota (se não for uma nota nova) na instância ViewModel
     */
    private void saveOriginalNoteValues() {
        if (mIsNewNote)
            return;
        mViewModel.mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mViewModel.mOriginalNoteTitle = mNote.getTitle();
        mViewModel.mOriginalNoteText = mNote.getText();
    }

    /**
     * Atribuir os valores de uma nota existente à activity
     *
     * @param spinnerCourses
     * @param textNoteTitle
     * @param textNoteText
     */
    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {

        // Carregar a lista de cursos do spinner e atribuir o index do curso selecionado
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());

        // Atribuir valores no spinner, text e title
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    /**
     * Se for uma newNote, Criar
     * Se for uma oldNote, carregar infos em mNote
     */
    private void readDisplayStateValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        //Se for uma nota nova, mIsNewNote será true
        mIsNewNote = position == POSITION_NOT_SET;

        if (mIsNewNote) {
            createNewNote();
        } else
            mNote = DataManager.getInstance().getNotes().get(position);
    }

    /**
     * Salvar uma nova nota vazia (usado ao clicar no fab)
     */
    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);
    }

    /**
     * Método para criação do menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    /**
     * Método de seleção de itens do menu
     *
     * @param item - Item selecionado
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Metodo chamado ao "sair da tela", apertar o botão de voltar, tirar da pilha

    /**
     * Método de lifecycle chamado ao sair do foreground.
     * Se cancelar for verdadeiro, verifica se é uma nova nota
     * Se for uma nova nota, remover
     * Senão, salvar com informações originais
     * Senão, salvar nota
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling) {
            if (mIsNewNote)
                DataManager.getInstance().removeNote(mNotePosition);
            else
                storePreviousNoteValues();
        } else {
            saveNote();
        }
    }

    /**
     * Salvar os dados originais das notas na instância do ViewModel
     */
    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mViewModel.mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mViewModel.mOriginalNoteTitle);
        mNote.setText(mViewModel.mOriginalNoteText);
    }

    /**
     * Salvar informações das notas em mNote
     */
    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());

    }

    /**
     * Método para montar um e-mail padrão com informações da nota usando implicit intent
     */
    private void sendEmail() {
        //Pegar os atributos que serão colocados no e-mail
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Check ou what i learned at the Pluralsight course \"" +
                course.getTitle() + "\"\n" + mTextNoteText.getText().toString();

        //Motando a intent do e-mail. Olhar documentação de intents
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2922");

        //Passar subject e text como extras
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}