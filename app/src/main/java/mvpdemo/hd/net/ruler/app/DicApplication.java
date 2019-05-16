package mvpdemo.hd.net.ruler.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.igexin.sdk.PushManager;

import java.io.File;

import mvpdemo.hd.greendao.DbInit;
import mvpdemo.hd.net.ruler.getui.PJGTIntentService;
import mvpdemo.hd.net.ruler.getui.PJGTPushService;
import mvpdemo.hd.net.utils.PJLog;

public class DicApplication extends Application {
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        PJLog.e("DicApplication onCreate");
        context = getApplicationContext();
        DbInit.init(context);
        JRCrashHandler jrCrashHandler = JRCrashHandler.getInstance();
        jrCrashHandler.init(context);
        Thread.setDefaultUncaughtExceptionHandler(jrCrashHandler);
        JRConfig.downloadPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        PushManager.getInstance().initialize(context, PJGTPushService.class);
        PushManager.getInstance().registerPushIntentService(context, PJGTIntentService.class);
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
