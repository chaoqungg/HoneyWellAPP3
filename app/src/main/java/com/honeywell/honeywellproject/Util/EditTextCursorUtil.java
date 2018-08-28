package com.honeywell.honeywellproject.Util;

import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by QHT on 2018-03-05.
 */
public class EditTextCursorUtil {

    public static void setCursorDrawable(EditText et,int drawableRes){
        try {//修改光标的颜色（反射）
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(et, drawableRes);

        } catch (Exception ignored) {
            // TODO: handle exception
        }
    }
}
