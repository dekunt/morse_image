package com.meu.morseimage.utils;

import com.meu.morseimage.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dekunt on 15/10/8.
 */
public class FaceContentUtil
{
    private static List<Integer> faceList = Arrays.asList(
            R.mipmap.ic_face_01, R.mipmap.ic_face_02, R.mipmap.ic_face_03, R.mipmap.ic_face_04,
            R.mipmap.ic_face_05, R.mipmap.ic_face_06, R.mipmap.ic_face_07, R.mipmap.ic_face_08,
            R.mipmap.ic_face_09, R.mipmap.ic_face_10, R.mipmap.ic_face_11, R.mipmap.ic_face_12,
            R.mipmap.ic_face_13, R.mipmap.ic_face_14, R.mipmap.ic_face_15, R.mipmap.ic_face_16);


    public static List<List<Integer>> getFaceLists(int countPerPage)
    {
        List<List<Integer>> result = new LinkedList<>();
        for (int i = 0; i < faceList.size(); i += countPerPage) {
            int end = Math.min(i + countPerPage, faceList.size());
            result.add(faceList.subList(i, end));
        }
        return result;
    }
}
