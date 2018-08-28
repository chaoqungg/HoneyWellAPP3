package com.honeywell.honeywellproject.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by QHT on 2017-04-28.
 */
public class RegexUtil {

    /**
     * 手机号码正则表达式
     */
    private static final String  PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
    /**
     * Email正则表达式
     */
    private static final String  RULE_EMAIL       = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    private static       Pattern EMAIL_p          = Pattern.compile(RULE_EMAIL);

    /**
     * 正则匹配是否为纯数字
     */
    public static boolean matchNum(String str) {
        if (str.matches("^\\d+$")) {
            return true;
        }
        return false;
    }

    /**
     * 正则匹配是否为手机号
     */
    public static boolean matchPhone(String str) {
        if (str.matches(PHONE_NUMBER_REG)) {
            return true;
        }
        return false;
    }

    /**
     * 正则匹配是否为邮箱
     */
    public static boolean matchEmail(String str) {
        Matcher m = EMAIL_p.matcher(str);
        return m.matches();
    }
}
