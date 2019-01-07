package mvpdemo.hd.net.ruler.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

import mvpdemo.hd.greendao.DaoMaster;
import mvpdemo.hd.greendao.DbInit;


public class DicApplication extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        DbInit.init(context);
        JRCrashHandler jrCrashHandler = JRCrashHandler.getInstance();
        jrCrashHandler.init(context);
        Thread.setDefaultUncaughtExceptionHandler(jrCrashHandler);
        JRConfig.downloadPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
    }

    public static class JRConfig {
        static String downloadPath;

        public static String getSoundPath(boolean boy, String word) {
            String file = downloadPath + "/" + word + ".mov";
            if (new File(file).exists()) {
                return file;
            } else {
                return downloadPath + "/" + word + (boy ? "-m" : "-f") + ".mp3";
            }
        }

        public static String getDownloadPath() {
            return downloadPath;
        }
    }
}
