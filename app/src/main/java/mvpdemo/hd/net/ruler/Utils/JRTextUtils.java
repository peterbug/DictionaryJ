package mvpdemo.hd.net.ruler.Utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

public class JRTextUtils {
    public static SpannableString getSpanningString(CharSequence text, int start, int end, String color) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * @param text
     * @param highLightText
     * @param color         "#000000"
     * @return
     */
    public static SpannableString getSpanningString(String text, String highLightText, String color) {
        int start = text.indexOf(highLightText);
        int end = start + highLightText.length();
        return getSpanningString(text, start, end, color);
    }

    public static String replace(String source, String target, String replaceMent) {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(target) || TextUtils.isEmpty(replaceMent)) {
            return source;
        }
        String x = source;
        String x2 = x;
        do {
            x = x2;
            x2 = x.replace(target, replaceMent);
        } while (!x.equals(x2));

        return x2;
    }
}
