package com.hcz017.screenshot.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.CompressFormat.PNG;

public class FileUtil {
    private static final String TAG = "FileUtil";

    private static final String SCREEN_SHOT_PATH = File.separator + "Pictures" + File.separator
            + "Screenshots" + File.separator;
    private static final String SCREENSHOT_FILE_NAME_TEMPLATE = "Screenshot_%s.png";
    private static String mScreenshotDirAndName;
    private static Bitmap mBitmap;

    private static String getExternalStoragePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            return context.getFilesDir().toString();
        }
    }

    private static String getScreenShotDir(Context context) {
        StringBuffer stringBuffer = new StringBuffer(getExternalStoragePath(context));
        stringBuffer.append(SCREEN_SHOT_PATH);
        File file = new File(stringBuffer.toString());
        if (!file.exists()) {
            file.mkdirs();
        }
        return stringBuffer.toString();
    }

    /**
     * file name would be like "Screenshot_20170417_222222.png"
     */
    public static void generateScreenshotName(Context context) {
        long currentTime = System.currentTimeMillis();
        String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(currentTime));
        String screenshotName = String.format(SCREENSHOT_FILE_NAME_TEMPLATE, imageDate);

        StringBuffer screenshotDir = new StringBuffer(getScreenShotDir(context));
        screenshotDir.append(screenshotName);
        setScreenshotDirAndName(screenshotDir.toString());
        Log.d(TAG, "generateScreenshotName: file name: " + screenshotDir.toString());
    }

    private static void setScreenshotDirAndName(String dirAndName) {
        mScreenshotDirAndName = dirAndName;
    }

    public static String getScreenshotDirAndName() {
        return mScreenshotDirAndName;
    }

    /**
     * save screenshot bitmap temporarily
     */
    public static void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public static Bitmap getBitmap() {
        return mBitmap;
    }


    /**
     * save screenshot to sdcard/Pictures/Screenshots/
     * @param bitmap bitmap to be save
     * @param screenshotPath saved path and name
     */
    public static void saveScreenshot(final Bitmap bitmap, final String screenshotPath) {
        Log.d(TAG, "saveScreenshot to storage");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Save
                    OutputStream out = new FileOutputStream(screenshotPath);
                    bitmap.compress(PNG, 100, out);
                    out.flush();
                    out.close();
                    Log.d(TAG, "saveScreenshotFile: success");
                } catch (Throwable e) {
                    // Several error may come out with file handling or OOM'
                    Log.e(TAG, "saveScreenshotFile: failed");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * delete screenshot file
     * @param filePath the file to be deleted
     * @return if successfully deleted
     */
    public static boolean deleteScreenshot(String filePath) {
        File file = new File(filePath);
        return isFileExists(file) && file.delete();
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }
}
