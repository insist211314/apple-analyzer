package com.appleframework.analyzer.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/21.
 */
public class StringUtils {

    public StringUtils() {
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isMac(String mac) {
        String regex = "[a-f\\d]{2}[a-f\\d]{2}[a-f\\d]{2}[a-f\\d]{2}[a-f\\d]{2}[a-f\\d]{2}";
        return mac != null && mac.toLowerCase().matches(regex);
    }

    public static String dateFormat(Date date) {
        if(date != null) {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sf.format(date);
        } else {
            return "";
        }
    }

    public static String dateFormat(Date date, String style) {
        if(date != null) {
            SimpleDateFormat sf = new SimpleDateFormat(style);
            return sf.format(date);
        } else {
            return "";
        }
    }

    public String StringFilter(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}\':;\',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static boolean isDigit(String s) {
        if(isNullOrEmpty(s)) {
            return false;
        } else {
            for(int i = s.length() - 1; i >= 0; --i) {
                if(!Character.isDigit(s.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String trim(String str) {
        return str == null?null:str.trim();
    }

    public static boolean isEmpty(String str) {
        return str == null?true:(str.length() == 0?true:("null".equals(str)?true:"0".equals(str)));
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String replaceAll(String s, String oss, String nss) {
        if(null != s && oss != null && nss != null) {
            String rlt = s;
            StringBuffer sb = new StringBuffer();

            while(true) {
                int idx = rlt.indexOf(oss);
                if(idx < 0) {
                    return rlt;
                }

                sb.delete(0, sb.length());
                if(idx > 0) {
                    sb.append(rlt.substring(0, idx));
                }

                sb.append(nss);
                sb.append(rlt.substring(idx + oss.length()));
                rlt = sb.toString();
            }
        } else {
            return s;
        }
    }

    public static String appendParam(String string, String name, String value, String valueSeparate, String paramSeparate) {
        StringBuffer sb = new StringBuffer();
        if(null != string && !"".equals(string)) {
            sb.append(string);
            sb.append(paramSeparate);
            sb.append(name);
            sb.append(valueSeparate);
            sb.append(value);
        } else {
            sb.append(name);
            sb.append(valueSeparate);
            sb.append(value);
        }

        return sb.toString();
    }

    public static String getValueFromString(String string, String name, String valueSeparate, String paramSeparate) {
        String[] params = string.split(paramSeparate);

        for(int i = 0; i < params.length; ++i) {
            String[] param = params[i].split(valueSeparate);
            if(param != null && param.length > 0 && param[0].equals(name)) {
                if(param.length > 1) {
                    return param[1];
                }

                return null;
            }
        }

        return null;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static List<Integer> getListId(String id) {
        String[] str = id.split(",");
        ArrayList list = new ArrayList();

        for(int i = 0; i < str.length; ++i) {
            list.add(Integer.valueOf(Integer.parseInt(str[i])));
        }

        return list;
    }

    public static Boolean isEqualString(String arg1, String arg2) {
        return Boolean.valueOf(arg1.equals(arg2));
    }

    public static String formatterString(String arg, Object... objects) {
        return MessageFormat.format(arg, objects);
    }
}
