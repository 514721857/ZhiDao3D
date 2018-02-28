package com.zhidao.sgr.zhidao3d.image_handle;

import java.io.File;

import android.os.Environment;

public class FileUtils {

    private static File mCacheFile;

    /**
     * 获取临时图片的缓存路径
     *
     * @return
     */
    public static File getCacheFile() {
        File file = new File(getAppCacheDir(), "image");
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = "temp_" + System.currentTimeMillis() + ".jpg";
        return new File(file, fileName);
    }

    private static File getAppCacheDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            mCacheFile = Lib.getInstance().getExternalCacheDir();
        }
        if (mCacheFile == null) {
            mCacheFile = Lib.getInstance().getCacheDir();
        }
        return mCacheFile;
    }
}
