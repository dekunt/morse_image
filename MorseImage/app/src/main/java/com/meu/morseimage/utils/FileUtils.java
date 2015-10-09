package com.meu.morseimage.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by dekunt on 15/10/8.
 */
public class FileUtils
{
    private final static String EXTERNAL_DIR = "/MorseImage";

    public static File createFileExternalStorage(String part, String ext)
    {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + EXTERNAL_DIR);
        makeDirs(tempDir);
        try
        {
            return File.createTempFile(part, ext, tempDir);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    private static void makeDirs(File path)
    {
        if (!path.exists() && !path.mkdirs())
        {
            Log.e("FileUtils", "make dirs failed");
        }
    }
}
