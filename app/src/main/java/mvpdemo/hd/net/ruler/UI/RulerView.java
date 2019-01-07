package mvpdemo.hd.net.ruler.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import mvpdemo.hd.net.ruler.R;

public class RulerView extends View {
    static double YDPI;
    static double XDPI;
    static final int text_size = 35;
    static final double pixel_and_mm_ratio = 2.54f;

    public static double getPixel_and_mm_ratio2() {
        return pixel_and_mm_ratio2;
    }

    public static void setPixel_and_mm_ratio2(double pixel_and_mm_ratio2) {
        RulerView.pixel_and_mm_ratio2 = pixel_and_mm_ratio2;
    }

    static double pixel_and_mm_ratio2 = 2.56f;


    private String mDirection;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RulerView);
        mDirection = a.getString(R.styleable.RulerView_direction);
        if (TextUtils.isEmpty(mDirection)) {
            mDirection = "left";
        }
        a.recycle();
        Log.e("XXX", this.getClass().getSimpleName() + " RulerView: " + context);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        YDPI = dm.ydpi;
        XDPI = dm.xdpi;
        paint.setStrokeWidth(2);
        paint.setTextSize(text_size);
        paint.setColor(0xff0000ff);
    }

    private Point getPointBy(int mmX, int mmH) {
        Point base = new Point(0, 0);
        base.y = (int) (((mmH * YDPI / pixel_and_mm_ratio) + 0) / 10);
        base.x = (int) (((mmX * XDPI / pixel_and_mm_ratio) + 0) / 10);
//        Log.e("XXX", this.getClass().getSimpleName() + " onDraw: " + base.y + "  offset:" + ((((mmH * YDPI / 2.54) + 0) / 10)));
        return base;
    }

    private Point getPointBy2(int mmX, int mmH) {
        Point base = new Point(0, 0);
        base.y = (int) (((mmH * YDPI / pixel_and_mm_ratio2) + 0) / 10);
        base.x = (int) (((mmX * XDPI / pixel_and_mm_ratio2) + 0) / 10) + 15;
//        Log.e("XXX", this.getClass().getSimpleName() + " onDraw: " + base.y + "  offset:" + ((((mmH * YDPI / 2.54) + 0) / 10)));
        return base;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        myDraw(canvas);
        myDraw2(canvas);
        canvas.restore();
    }

    private void myDraw(Canvas canvas) {
        int x = 0;//距离控件边缘距离
        int x_with = -12; //每毫米刻度左右宽度
        int x_max_witdh = -30;//每厘米刻度左右宽度
        int x_mid_witdh = (x_with + x_max_witdh) / 2;//每5毫米刻度左右宽度
        int text_x_offset = x_mid_witdh / 3;
        if (mDirection.equals("right")) {
            x_with = -x_with;
            x_max_witdh = -x_max_witdh;
            x_mid_witdh = -x_mid_witdh;
            text_x_offset = x_mid_witdh + text_size / 3;
//            canvas.translate();
        }

        int mm_10 = 0;//每10毫米计数
        int mm = 1;//距离顶部起始毫米数
        Point point_mm = new Point(2, mm);
        Point point = getPointBy(point_mm.x, point_mm.y);
        int y = point.y;
        int y_offset_in_pixel = 0;
        int screenH = getMeasuredHeight();
        while (point.y < screenH) {
            if (mm_10 % 10 == 0) {
                canvas.drawLine(point.x, point.y, point.x + x_max_witdh, point.y, paint);
                canvas.drawText("" + mm_10 / 10, point.x - text_x_offset, point.y + text_size / 2, paint);
            } else if (mm_10 % 10 == 5) {
                canvas.drawLine(point.x, point.y, point.x + x_mid_witdh, point.y, paint);
            } else {
                canvas.drawLine(point.x, point.y, point.x + x_with, point.y, paint);
            }
            mm_10++;
            point_mm.y++;
            y_offset_in_pixel = point.y;
            point = getPointBy(point_mm.x, point_mm.y);
        }
        //画长的跟尺平行的线
        canvas.drawLine(point.x, y, point.x, point.y, paint);
    }

    private void myDraw2(Canvas canvas) {
        int x = 0;//距离控件边缘距离
        int x_with = -12; //每毫米刻度左右宽度
        int x_max_witdh = -30;//每厘米刻度左右宽度
        int x_mid_witdh = (x_with + x_max_witdh) / 2;//每5毫米刻度左右宽度
        int text_x_offset = x_mid_witdh / 3;
        if (mDirection.equals("right")) {
            x_with = -x_with;
            x_max_witdh = -x_max_witdh;
            x_mid_witdh = -x_mid_witdh;
            text_x_offset = x_mid_witdh + text_size / 3;
//            canvas.translate();
        }

        int mm_10 = 0;//每10毫米计数
        int mm = 1;//距离顶部起始毫米数
        Point point_mm = new Point(2, mm);
        Point point = getPointBy2(point_mm.x, point_mm.y);
        int y = point.y;
        int y_offset_in_pixel = 0;
        int screenH = getMeasuredHeight();
        paint.setColor(0xffff0000);
        while (point.y < screenH) {
            if (mm_10 % 10 == 0) {
                canvas.drawLine(point.x, point.y, point.x + x_max_witdh, point.y, paint);
                canvas.drawText("" + mm_10 / 10, point.x - text_x_offset, point.y + text_size / 2, paint);
            } else if (mm_10 % 10 == 5) {
                canvas.drawLine(point.x, point.y, point.x + x_mid_witdh, point.y, paint);
            } else {
                canvas.drawLine(point.x, point.y, point.x + x_with, point.y, paint);
            }
            mm_10++;
            point_mm.y++;
            y_offset_in_pixel = point.y;
            point = getPointBy2(point_mm.x, point_mm.y);
        }
        //画长的跟尺平行的线
        canvas.drawLine(point.x, y, point.x, point.y, paint);
        paint.setColor(0xff0000ff);
    }
}
