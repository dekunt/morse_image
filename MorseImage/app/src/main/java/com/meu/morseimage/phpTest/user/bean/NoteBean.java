package com.meu.morseimage.phpTest.user.bean;

import java.io.Serializable;

/**
 * Created by dekunt on 15/11/4.
 */
public class NoteBean implements Serializable
{
    public String noteId;
    public String title;
    public String content;
    public String modifyTime;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteBean bean = (NoteBean) o;
        return noteId.equals(bean.noteId);

    }

    @Override
    public int hashCode()
    {
        return noteId.hashCode();
    }
}
