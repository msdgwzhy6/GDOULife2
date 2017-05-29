package com.fedming.gdoulife.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * 打开或关闭软键盘
 *
 * @author fedming
 */
public class KeyBoardUtils {

    /**
     * 隐藏软键盘
     * @param context context
     */
    public static void hideSoftKeyboard(Context context) {

        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context)
                    .getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e) {
            Log.e("InputMethodManager", "hideSoftKeyboard Catch error, skip it!", e);
        }
    }
}
