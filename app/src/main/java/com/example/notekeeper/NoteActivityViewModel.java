package com.example.notekeeper;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

/**
 * ViewModel é um padrão para salvar o estado da aplicação
 */
public class NoteActivityViewModel extends ViewModel {
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";

    public String mOriginalNoteCourseId;
    public String mOriginalNoteText;
    public String mOriginalNoteTitle;

    /**
     * Verificar se essa instância foi recentemente criada (ainda não foi destruída)
     */
    public boolean mIsNewlyCreated = true;


    /**
     * Salva o estado no Bundle do SO. Usado quando não for uma nova atividade E ainda existir
     * uma instância de ViewModel
     *
     * @param savedInstanceState Bundle do sistema
     */
    public void saveState(Bundle savedInstanceState) {
        savedInstanceState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        savedInstanceState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        savedInstanceState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    /**
     * Restaurar a instância de ViewModel com as informações contidas no Bundle do SO
     *
     * @param inState Bundle do sistema
     */
    public void restoreState(Bundle inState) {
        mOriginalNoteCourseId = inState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = inState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = inState.getString(ORIGINAL_NOTE_TEXT);
    }
}
