package com.jcode.apps.boliviasolidaria;

import android.os.Environment;

import java.io.File;

public class App {

    public static final String NAME = "Bolivia Solidaria";

    public static File getPhotoDir() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + NAME + File.separator);
        root.mkdirs();
        return root;
    }

    public static File getPhotoCache() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + NAME + File.separator + "cache" + File.separator);
        root.mkdirs();
        return root;
    }

}
