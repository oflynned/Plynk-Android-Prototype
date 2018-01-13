package com.syzible.wallet.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ed on 15/11/2017.
 */

public class CachingUtils {
    public static boolean doesImageExist(Context context, String id) {
        checkDirectoryExists(context);
        File[] files = new File(getDirectoryPath()).listFiles();

        if (files == null)
            return false;

        if (files.length == 0)
            return false;

        // does it exist?
        boolean exists = false;
        String fileName = "";
        for (File file : files) {
            if (id.equals(file.getName().split("_")[0])) {
                exists = true;
                fileName = file.getName();
            }
        }

        if (!exists) return false;

        // it exists, but is it stale?
        if (isStale(fileName)) {
            for (File file : files) {
                if (id.equals(file.getName().split("_")[0]))
                    file.delete();
            }

            return false;
        }

        return true;
    }

    public static void cacheImage(String id, Bitmap image) {
        saveToFile(id, image);
    }

    public static void clearCache(Context context) {
        File[] files = new File(getDirectoryPath()).listFiles();
        for (File file : files)
            file.delete();
    }

    public static Bitmap getCachedImage(String name) {
        File[] files = new File(getDirectoryPath()).listFiles();
        String fileName = name;

        for (File file : files) {
            if (name.equals(file.getName().split("_")[0]))
                fileName = file.getName();
        }

        String pathToFile = getDirectoryPath() + "/" + fileName;
        Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        return bitmap;
    }

    private static boolean isStale(String fileName) {
        // <id>_<last cached time>.png

        String[] fileData = fileName.split("_");
        long oneWeekInMillis = 1000 * 60 * 60 * 24 * 7;
        long cachingTime = Long.valueOf(fileData[1].replace(".png", ""));
        return System.currentTimeMillis() - cachingTime > oneWeekInMillis;
    }

    private static void saveToFile(String name, Bitmap file) {
        File pictureFile = getOutputMediaFile(name + "_" + System.currentTimeMillis());
        assert pictureFile != null;

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            file.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getOutputMediaFile(String name) {
        File mediaStorageDir = new File(getDirectoryPath());

        if (!mediaStorageDir.exists())
            if (!mediaStorageDir.mkdirs())
                return null;

        return new File(mediaStorageDir.getPath() + File.separator + getFileWithExtension(name));
    }

    private static void checkDirectoryExists(Context context) {
        String path = getDirectoryPath();
        File directory = new File(path);

        if (!directory.exists()) {
            File dir = new File(path);
            dir.mkdirs();
        }
    }

    private static String getDirectoryPath() {
        return Environment.getExternalStorageDirectory()
                + "/Android/data/com.syzible.loinnir/Images";
    }

    private static String getFileWithExtension(String name) {
        return name + ".png";
    }
}
