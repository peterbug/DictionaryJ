package mvpdemo.hd.net.ruler.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class JRInputMethodUtils {
    public static void hideSoftKeyboard(Activity activity, View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v
                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS, null);
    }
}
