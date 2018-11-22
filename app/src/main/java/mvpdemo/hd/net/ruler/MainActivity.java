package mvpdemo.hd.net.ruler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends Activity {
    SharedPreferences sp;
    final String SP_KEY = "ratio";
    int index = 1;
    OkHttpClient client = new OkHttpClient();
    MediaPlayer mPlayer = new MediaPlayer();
    EditText input_word;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideNavigationBar();
//        View actionBar = findViewById(R.id.action_bar);
//        if (actionBar != null) {
//            actionBar.setVisibility(View.GONE);
//        }
        getPingMuSize(getApplicationContext());
        getWindow().setTitle(null);
//        getScreenSizeOfDevice2();
//        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((TextView) v).setText(String.format("  %05d   ", index++));
//            }
//        });
//        testMath();
        sp = this.getPreferences(MODE_PRIVATE);
        String ratio = sp.getString(SP_KEY, String.format("%.2f", RulerView.getPixel_and_mm_ratio2()));
        ((EditText) findViewById(R.id.ratio)).setText(ratio);
        input_word = ((EditText) findViewById(R.id.input_word));
        getFile(null);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("XXX", this.getClass().getSimpleName() + " onPrepared: ");
                mp.start();
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
            }
        });
    }

    public void setRatio(View v) {
        SharedPreferences.Editor editor = sp.edit();
        String ratio = ((EditText) findViewById(R.id.ratio)).getText().toString();
        editor.putString(SP_KEY, ratio);
        editor.commit();
        RulerView.setPixel_and_mm_ratio2(Double.parseDouble(ratio));
    }

    public void getFile(View v) {
        if (TextUtils.isEmpty(input_word.getText())) {
            Toast.makeText(getApplicationContext(), "空字符", LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                getSoundName(input_word.getText().toString());
            }
        }).start();
    }

    public void playMp3(View v) {
        String filePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + input_word.getText().toString() + "-f" + ".mp3";
        try {
//            mPlayer.stop();
            mPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                } else {
//                    mPlayer.setVolume();
                    mPlayer.prepareAsync();
//                    mPlayer.start();

                }
            }
        }).start();
    }

    private void downloadMp3File(final String mp3Name, final String word, final String gender) {

//        String url = "http://audio.dict.cn/muc0cdd51a8f4db31832568351615c08bedadb.mp3";
        String url = "http://audio.dict.cn/" + mp3Name;
        Request request = new Request.Builder()
                .url(url)
                .build();

        String body = null;
        Call call = client.newCall(request);
        //4.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("XXX", this.getClass().getSimpleName() + " onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream is = response.body().byteStream();
                saveFileToDisk(is, word, gender);
            }
        });
        Log.e("XXX", this.getClass().getSimpleName() + " getFile: " + body);
    }

    private void saveFileToDisk(final InputStream is, final String word, final String gender) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
        //拿到字节流

        int len = 0;
        //设置下载图片存储路径和名称
        String filePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + word + "-" + gender + ".mp3";
//        String filePath = Environment.getExternalStorageDirectory() + "/a/" + word + "-" + gender + ".mp3";
        Log.e("XXX", this.getClass().getSimpleName() + " onResponse: " + filePath + " Thread:" + Thread.currentThread().getName());
        File file = new File(filePath);
        try {
            if (!file.exists()) {
//                file.createNewFile();

//            filePath = Environment.getExternalStorageDirectory() + "/a/" + word + "-" + gender + ".mp3";
                String p = "android.permission.READ_EXTERNAL_STORAGE";
                FileOutputStream fos = new FileOutputStream(filePath);
                byte[] buf = new byte[128];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//            }
//        });
    }

    private void getSoundName(String word) {
        try {
            //从一个URL加载一个Document对象。
            Document doc = Jsoup.connect("http://dict.cn/" + word).get();
            //选择“美食天下”所在节点
            Elements elements = doc.select("div.phonetic");
            //打印 <a>标签里面的title

            for (Element e : elements) {
                for (Element span : e.select("span")) {
                    String text = span.text();
                    if (!TextUtils.isEmpty(text) && text.contains("美")) {
                        for (Element i : span.select("i")) {
                            String mp3Name = i.attr("naudio").split("\\?")[0];
                            Log.e("XXX", this.getClass().getSimpleName() + " getSoundName: mp3Name:" + mp3Name);
                            String gender = "f";
                            if (i.attr("title").contains("男")) {
                                gender = "m";
                            }

                            downloadMp3File(mp3Name, word, gender);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void testMath() {
        double[] data = {3.0, 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 3.9};
        for (double d : data) {
            Log.e("XXX", String.format(this.getClass().getSimpleName() + " testMath: Math.ceil(%.1f)=%.1f", d, Math.ceil(d)));
            Log.e("XXX", String.format(this.getClass().getSimpleName() + " testMath: Math.floor(%.1f)=%.1f", d, Math.floor(d)));
            Log.e("XXX", String.format(this.getClass().getSimpleName() + " testMath: Math.round(%.1f)=%d", d, Math.round(d)));
        }

        for (double d : data) {
            d = -d;
            Log.e("XXX", String.format(this.getClass().getSimpleName() + " testMath: Math.ceil(%.1f)=%.1f", d, Math.ceil(d)));
            Log.e("XXX", String.format(this.getClass().getSimpleName() + " testMath: Math.floor(%.1f)=%.1f", d, Math.floor(d)));
            Log.e("XXX", String.format(this.getClass().getSimpleName() + " testMath: Math.round(%.1f)=%d", d, Math.round(d)));
        }
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0x00000000);
    }

    private float getPingMuSize(Context mContext) {
        int densityDpi = mContext.getResources().getDisplayMetrics().densityDpi;
        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        float density = mContext.getResources().getDisplayMetrics().density;
        float xdpi = mContext.getResources().getDisplayMetrics().xdpi;
        float ydpi = mContext.getResources().getDisplayMetrics().ydpi;
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        // 这样可以计算屏幕的物理尺寸
        float width2 = (width / xdpi) * (width / xdpi);
        float height2 = (height / ydpi) * (width / xdpi);

        float size = (float) Math.sqrt(width2 + height2);
        //6.54878 12.5306225 4.3679976
        Log.e("XXX", this.getClass().getSimpleName() + " getPingMuSize: " + width2 + " " + height2 + " " + size);
        return size;
    }

    private void getScreenSizeOfDevice2() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        double x = Math.pow(point.x / dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        x = 0;
        double screenInches = Math.sqrt(x + y);
        Log.e("XXX", this.getClass().getSimpleName() + " getScreenSizeOfDevice: " + screenInches);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                /*| View.SYSTEM_UI_FLAG_FULLSCREEN*/;
//        decorView.setSystemUiVisibility(uiOptions);
        boolean result = false;
        result = super.dispatchTouchEvent(ev);
//        Log.e("XXX", this.getClass().getSimpleName() + " dispatchTouchEvent: " + MotionEvent.actionToString(ev.getAction()) + " " + Integer.toBinaryString(decorView.getSystemUiVisibility()) + " " + Integer.toBinaryString(decorView.getWindowSystemUiVisibility()) + " " + result);
//
//        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) == 0) {
//        hideNavigationBar();
//            Log.e("XXX", this.getClass().getSimpleName() + " dispatchTouchEvent: ");
//        }
        return result;
    }
}
