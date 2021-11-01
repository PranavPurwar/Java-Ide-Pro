package com.duy.ide.javaide.sample;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetUtil {


    public static boolean copyAssetSample(AssetManager assetManager,
                                          String fromAssetPath, String toPath) throws IOException {
        String[] files = assetManager.list(fromAssetPath);
        new File(toPath).mkdirs();
        boolean success = true;
        for (String file : files)
            if (file.contains(".")) {
                success &= copyAsset(assetManager,
                        fromAssetPath + "/" + file,
                        toPath + "/" + file);
            } else {
                success &= copyAssetSample(assetManager,
                        fromAssetPath + "/" + file,
                        toPath + "/" + file);
            }
        return success;
    }

    private static boolean copyAsset(AssetManager assetManager,
                                     String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


}
