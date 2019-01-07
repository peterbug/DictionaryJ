package mvpdemo.hd.net.ruler.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;

public class JRStorageUtils {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private JRStorageUtils() {

    }

    public static File getDownloadDirectory(Context context) {
        return getDirectory(context, null, true);
    }

    public static File getDirectory(Context context, String dirName) {
        return getDirectory(context, dirName, true);
    }

    public static File getDirectory(Context context, String dirName, boolean preferExternal) {
        File file = null;
        String externalStorageState = getExternalStorageState();

        if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            file = getExternalDir(dirName);
        }
        if (file == null) {
            file = context.getFilesDir();
        }
        if (file == null) {
            String downloadDirPath = "/data/data/" + context.getPackageName() + "/" + dirName + "/";
            file = new File(downloadDirPath);
        }
        return file;
    }

    private static String getExternalStorageState() {
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (Throwable e) {
            e.printStackTrace();
            externalStorageState = "";
        }
        return externalStorageState;
    }

    public static boolean isExternalStorageWritable() {
        String externalStorageState = getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String externalStorageState = getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(externalStorageState) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState)) {
            return true;
        }
        return false;
    }

    /**
     * 可以指定 dirName, 如果没有指定默认放在 HDDownloads 目录下
     *
     * @param dirName
     * @return
     */
    private static File getExternalDir(String dirName) {
        File file = new File(Environment.getExternalStorageDirectory(), TextUtils.isEmpty(dirName) ? "HDDownloads" : dirName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return null;
            }
            try {
                new File(file, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return file;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int permission = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    //获取内部存储空间总大小
    public static String getTotalInternalMemorySize(Context context) {
        if (!isExternalStorageWritable())
            return "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                File file = Environment.getDataDirectory();
                StatFs statFs = new StatFs(file.getPath());
                long totalBytes = statFs.getTotalBytes();
                return Formatter.formatFileSize(context, totalBytes);

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //获取内部存储空间可用大小
    public static String getAvailableInternalMemorySize(Context context) {
        if (!isExternalStorageWritable())
            return "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                File file = Environment.getDataDirectory();
                StatFs statFs = new StatFs(file.getPath());
                long availableBytes = statFs.getAvailableBytes();
                return Formatter.formatFileSize(context, availableBytes);

            } catch (Throwable e) {

            }
        }
        return "";
    }

    //获取外部存储空间总大小
    public static String getTotalExternalMemorySize(Context context) {
        if (!isExternalStorageWritable())
            return "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                File file = Environment.getExternalStorageDirectory();
                StatFs statFs = new StatFs(file.getPath());
                long totalBytes = statFs.getTotalBytes();
                return Formatter.formatFileSize(context, totalBytes);

            } catch (Throwable e) {

            }
        }
        return "";
    }

    //获取外部存储空间可用大小
    public static String getAvailableExternalMemorySize(Context context) {
        if (!isExternalStorageWritable())
            return "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                File file = Environment.getExternalStorageDirectory();
                StatFs statFs = new StatFs(file.getPath());
                long availableBytes = statFs.getAvailableBytes();
                return Formatter.formatFileSize(context, availableBytes);

            } catch (Throwable e) {

            }
        }
        return "";
    }

}
