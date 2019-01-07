package mvpdemo.hd.net.ruler.app;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class JRCrashHandler implements UncaughtExceptionHandler {
    private Context mcontext;
    private Map<String, String> appInfo = new HashMap<String, String>();
    private SimpleDateFormat timeF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public String filePath;

    private JRCrashHandler() {
    }

    private static class Holder {
        private static final JRCrashHandler INSTANCE = new JRCrashHandler();
    }

    public static JRCrashHandler getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mcontext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exceptions) {
        try {
            if (!handleException(exceptions)) {
            } else {

                Thread.sleep(3000);

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private boolean handleException(Throwable exception) {
        obtainDeviceInfo(mcontext);
        saveCrashInfo2File(exception);

        return false;
    }

    private void obtainDeviceInfo(Context context) {
        String time = timeF.format(new Date());
        appInfo.put("Device_Name", android.os.Build.MANUFACTURER);
        appInfo.put("Android_Version", android.os.Build.VERSION.RELEASE);
        appInfo.put("crash_time", time);
    }

    private String saveCrashInfo2File(Throwable exception) {
        Log.e("XXX", this.getClass().getSimpleName() + " saveCrashInfo2File: ");
        StringBuilder buffer = new StringBuilder();
        Iterator<String> iter = appInfo.keySet().iterator();

        while (iter.hasNext()) {
            String key = iter.next();
            String value = appInfo.get(key);
            buffer.append(key + "=" + value + "\r\n");
        }

        Writer writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        exception.printStackTrace(pwriter);
        Throwable cause = exception.getCause();

        while (cause != null) {
            cause.printStackTrace(pwriter);
            cause = cause.getCause();
        }
        pwriter.close();
        String result = writer.toString();
        buffer.append(result);
        try {
            String fileName = DicApplication.JRConfig.getDownloadPath();
            File dir = new File(fileName);
            fileName = dir.getParent() + "/alog";
            dir = new File(fileName);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdir();
            }
            fileName += "/crash-" + timeF.format(new Date()) + "-" + System.currentTimeMillis() + ".txt ";
            File file = new File(fileName);
            Log.e("XXX", this.getClass().getSimpleName() + " saveCrashInfo2File: " + file.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(buffer.toString().getBytes());
            fos.flush();
            fos.close();
            return file.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
