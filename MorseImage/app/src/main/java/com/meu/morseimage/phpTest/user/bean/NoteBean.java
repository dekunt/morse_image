package com.meu.morseimage.phpTest.user.bean;

import java.io.Serializable;

/**
 * Created by dekunt on 15/11/4.
 */
public class NoteBean implements Serializable
{
    public boolean checked = false;
    public String noteId;
    public String title;
    private String content;
    private String simpleContent;
    public String createTime;


    public String getContent() {
        return content;
    }

    public String getSimpleContent() {
        if (simpleContent == null)
            simpleContent = simply(content);
        return simpleContent;
    }

    public void setContent(String content) {
        this.content = content;
        this.simpleContent = simply(content);
    }

    private String simply(String src)
    {
        if (src == null || src.length() == 0)
            return "";
        return src.trim().replaceAll("\\s+", " ");
    }

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
