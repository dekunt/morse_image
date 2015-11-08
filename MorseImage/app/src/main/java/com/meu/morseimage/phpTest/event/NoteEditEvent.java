package com.meu.morseimage.phpTest.event;

import com.meu.morseimage.phpTest.user.bean.NoteBean;

/**
 * Created by dekunt on 15/11/8.
 */
public class NoteEditEvent
{
    public enum EditAction {ACTION_SENT, ACTION_RESPOND, ACTION_DONE};

    public EditAction action;
    public NoteBean noteBean;

    public NoteEditEvent(EditAction action, NoteBean noteBean)
    {
        this.action = action;
        this.noteBean = noteBean;
    }
}
