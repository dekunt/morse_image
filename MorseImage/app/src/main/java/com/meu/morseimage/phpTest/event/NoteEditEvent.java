package com.meu.morseimage.phpTest.event;

import com.meu.morseimage.phpTest.user.bean.NoteBean;

/**
 * Created by dekunt on 15/11/8.
 */
public class NoteEditEvent
{
    public NoteBean noteBean;

    public NoteEditEvent(NoteBean noteBean)
    {
        this.noteBean = noteBean;
    }
}
