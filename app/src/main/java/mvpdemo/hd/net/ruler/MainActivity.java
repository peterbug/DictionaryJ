package mvpdemo.hd.net.ruler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends Activity {
    SharedPreferences sp;
    final String SP_KEY = "ratio";
    int index = 1;

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
        getScreenSizeOfDevice2();
//        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((TextView) v).setText(String.format("  %05d   ", index++));
//            }
//        });
        testMath();
        sp = this.getPreferences(MODE_PRIVATE);
        String ratio = sp.getString(SP_KEY, String.format("%.2f", RulerView.getPixel_and_mm_ratio2()));
        ((EditText) findViewById(R.id.ratio)).setText(ratio);
    }

    public void setRatio(View v) {
        SharedPreferences.Editor editor = sp.edit();
        String ratio = ((EditText) findViewById(R.id.ratio)).getText().toString();
        editor.putString(SP_KEY, ratio);
        editor.commit();
        RulerView.setPixel_and_mm_ratio2(Double.parseDouble(ratio));
    }

    public void x(){
        
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
        Log.e("XXX", this.getClass().getSimpleName() + " dispatchTouchEvent: " + MotionEvent.actionToString(ev.getAction()) + " " + Integer.toBinaryString(decorView.getSystemUiVisibility()) + " " + Integer.toBinaryString(decorView.getWindowSystemUiVisibility()) + " " + result);
//
//        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) == 0) {
//        hideNavigationBar();
//            Log.e("XXX", this.getClass().getSimpleName() + " dispatchTouchEvent: ");
//        }
        return result;
    }
}
