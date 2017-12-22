package com.zyc.player.util;

import android.text.TextUtils;
import android.util.SparseIntArray;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [字符处理工具类]
 */
public class StringUtils {

    private final static String TAG = "StringUtils";

    public static final String STAT_FORMAT = "yyyy-MM-dd HH";
    public static final String TIME_FORMAT = "H:mm:ss";
    public static final String TIME_FORMAT_M_S = "mm:ss";
    public static final String TIME_FORMAT_M_S_1 = "m:ss";
    public static final String DATE_FORMAT_CH = "yyyy年M月d日";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String MONTH_DATE_FORMAT = "MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String AM_PM_TIME_FORMAT = "HH:mm";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String WWW_LOW = "www.";
    public static final String WWW_UPPER = "WWW.";
    public static final String FAVICON_URL_SUFFIX = "/favicon.ico";
    public static DecimalFormat DIGITAL_FORMAT_1 = new DecimalFormat("####.0");
    private static final String TIMEZONE_ID = "GMT +08:00, GMT +0800";

    private final static String HEX_CAPITAL = "0123456789ABCDEF";
    private final static String HEX = "0123456789abcdef";
    private final static String HEX_64 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_@";

    /**
     * 1秒的毫秒数
     */
    public static final long ONE_SECOND_AS_MILLS = 1000;
    /**
     * 1分钟的毫秒数
     */
    public static final long ONE_MINUTE_AS_MILLS = ONE_SECOND_AS_MILLS * 60;
    /**
     * 1小时的毫秒数
     */
    public static final long ONE_HOUR_AS_MILLS = ONE_MINUTE_AS_MILLS * 60;
    /**
     * 1天的毫秒数
     */
    public static final long ONE_DAY_AS_MILLS = ONE_HOUR_AS_MILLS * 24;

    static SparseIntArray CHAR_MAP = new SparseIntArray(9);

    static {
        CHAR_MAP.put('一', 1);
        CHAR_MAP.put('二', 2);
        CHAR_MAP.put('三', 3);
        CHAR_MAP.put('四', 4);
        CHAR_MAP.put('五', 5);
        CHAR_MAP.put('六', 6);
        CHAR_MAP.put('七', 7);
        CHAR_MAP.put('八', 8);
        CHAR_MAP.put('九', 9);
    }

    private static int getIntFromMap(char ch) {
        Integer result = CHAR_MAP.get(ch);
        return result != null ? result : 0;
    }

    /**
     * 将0~9999以内的小写汉字转化为数字 例如：三千四百五十六转化为3456
     *
     * @param str
     * @return
     */
    public static int parseChineseNumber(String str) {
        if (str == null || str.length() <= 0) {
            return -1;
        }
        int result = 0;
        int index = -1;
        index = str.indexOf('千');
        if (index > 0) {
            result += getIntFromMap(str.charAt(index - 1)) * 1000;
        }
        index = str.indexOf('百');
        if (index > 0) {
            result += getIntFromMap(str.charAt(index - 1)) * 100;
        }
        index = str.indexOf('十');
        if (index > 0) {
            result += getIntFromMap(str.charAt(index - 1)) * 10;
        } else if (index == 0) {
            result += 10;
        }
        index = str.length();
        if (index > 0) {
            result += getIntFromMap(str.charAt(index - 1));
        }
        return result;
    }

    public static String OR(byte[] source, String key) {
        return OR(source, key, null);
    }

    public static byte[] OR2Byte(byte[] source, String key) {
        if (source == null || source.length < 1 || key == null || key.length() < 1) {
            return null;
        }
        final byte[] sourceBuff = source;
        byte[] keyBuff = key.getBytes();
        int keyLength = keyBuff.length;
        int keyIndex = 0;
        Integer byteResult = 0;

        byte[] result = new byte[sourceBuff.length];

        for (int i = 0; i < sourceBuff.length; i++) {
            keyIndex = i % keyLength;
            byteResult = sourceBuff[i] ^ keyBuff[keyIndex];
            result[i] = byteResult.byteValue();
        }

        return result;
    }

    public static String OR(byte[] source, String key, String charsetName) {
        byte[] result = OR2Byte(source, key);
        if (result == null) {
            return null;
        }

        String resultString = null;
        if (charsetName != null && !charsetName.equals("")) {
            try {
                resultString = new String(result, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            resultString = new String(result);
        }
        return resultString;
    }

    public static final boolean isHttpUrl(String url) {
        String regEx = "^(https|http://){0,1}([a-zA-Z0-9]{1,}[a-zA-Z0-9\\-]{0,}\\.){0,4}([a-zA-Z0-9]{1,}[a-zA-Z0-9\\-]{0,}\\.[a-zA-Z0-9]{1,})/{0,1}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        return m.find();
    }

    public static final String removeWWWHead(String url) {
        if (url == null) {
            return null;
        }

        String temp = url.toLowerCase();
        if (temp.startsWith(WWW_LOW)) {
            url = url.substring(WWW_LOW.length());
        }

        return url;
    }

    public static final String removeHttpHead(String url) {
        if (url == null) {
            return null;
        }

        if (url.startsWith(HTTP)) {
            url = url.substring(HTTP.length());
        } else if (url.startsWith(HTTPS)) {
            url = url.substring(HTTPS.length());
        }

        return url;
    }

    public static final String getFaviconUrl(String url) {
        String domainUrl = getDomainName(url, true);
        if (domainUrl != null) {
            return domainUrl + FAVICON_URL_SUFFIX;
        }
        return null;
    }

    public static final String getDomainName(String url) {
        return getDomainName(url, false);
    }

    public static final String getDomainName(String url, boolean hasHttpHead) {
        if (url == null) {
            return null;
        }

        String result = url;
        int index;
        if (hasHttpHead) {
            if (url.indexOf(HTTP) == 0) {
                index = url.indexOf("/", HTTP.length());
            } else if (url.indexOf(HTTPS) == 0) {
                index = url.indexOf("/", HTTPS.length());
            } else {
                index = url.indexOf("/");
            }
        } else {
            result = removeHttpHead(url);
            index = result.indexOf("/");
        }
        if (index > -1) {
            result = result.substring(0, index);
        }
        return result;
    }

    public static String formatStatDate() {
        SimpleDateFormat format = new SimpleDateFormat(STAT_FORMAT);
        return format.format(new Date());
    }

    public static String formatStatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat(STAT_FORMAT);
        return format.format(new Date(time));
    }

    public static String formatString(Date date, String formatString) {
        String nowdate = "";
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(formatString);
            nowdate = format.format(date);
        }

        return nowdate;
    }

    public static String formatString(Date date) {
        return formatString(date, DATE_FORMAT_CH);
    }

    public static String formartAmout(double num) {
        String pattern = "#0.00";
        DecimalFormat format = new DecimalFormat(pattern);
        String value = format.format(num);

        return value;
    }

    public static Date parseDate(String dateStr, String formatString) {
        Date date = null;
        if (dateStr != null && !dateStr.equals("")) {

            try {
                SimpleDateFormat format = new SimpleDateFormat(formatString);
                date = format.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return date;
    }

    public static String getToday(String format) {
        Calendar now = Calendar.getInstance();
        if (format != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(now.getTime());
        } else {
            return now.getTime().getTime() + "";
        }
    }

    public static boolean isNumber(String str) {
        if (str == null || str.length() < 1) {
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*.[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isInteger(String str) {
        if (str == null || str.length() < 1) {
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isWebUrl(String url) {
        if (url == null || url.length() < 1) {
            return false;
        }

        url = url.toLowerCase();
        return url.startsWith("http://") || url.startsWith("https://") || url.startsWith("ftp://");
    }

    /**
     * 是否为空
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        int strLen;
        if (value == null || (strLen = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(value.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为邮件地址
     */
    public static boolean isVaildEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            return false;
        }
        String emailPattern = "[a-zA-Z0-9_-|\\.]+@[a-zA-Z0-9_-]+.[a-zA-Z0-9_.-]+";
        boolean result = Pattern.matches(emailPattern, email);
        return result;
    }

    public static String parseDuration(long milliseconds) {
        if (milliseconds < 0) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(TIMEZONE_ID));
        return format.format(new Date(milliseconds));
    }

    public static String parseDurationFormatMS(long milliseconds) {
        if (milliseconds < 0) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_M_S);
        format.setTimeZone(TimeZone.getTimeZone(TIMEZONE_ID));
        return format.format(new Date(milliseconds));
    }

    /**
     * 如果小于一小时，00：00 大于一小时 0：00：00
     */
    public static String parseDurationRetractH(long milliseconds) {
        if (milliseconds >= 60000 * 60) {
            return StringUtils.parseDuration((int) milliseconds);
        } else {
            return StringUtils.parseDurationFormatMS((int) milliseconds);
        }
    }

    public static String parseDurationFormatMS1(long milliseconds) {
        if (milliseconds < 0) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT_M_S_1);
        format.setTimeZone(TimeZone.getTimeZone(TIMEZONE_ID));
        return format.format(new Date(milliseconds));
    }

    public static int getFrontSynIndex(String text, int currentIndex) {
        if (text != null) {
            if (currentIndex >= 0 && currentIndex < text.length()) {
                char[] textArray = text.toCharArray();
                for (int i = currentIndex; i >= 0; i--) {
                    if (textArray[i] == '.') {
                        return i + 1;
                    }
                }
            }
        }
        return 0;
    }

    public static int getBehindSynIndex(String text, int currentIndex) {
        if (text != null) {
            if (currentIndex >= 0 && currentIndex < text.length()) {
                char[] textArray = text.toCharArray();
                for (int i = currentIndex; i < text.length(); i++) {
                    if (textArray[i] == '.') {
                        return i;
                    }
                }
                return text.length();
            }
        }
        return 0;
    }

    public static String parseDateFromInt(long date) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return myFormatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String parseDateIntoNumStr(long date) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            return myFormatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String parseDateInto_HH_mm_NumStr(long date) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("HH:mm");
        try {
            return myFormatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String parseDateFromInt(long date, String formatString) {
        SimpleDateFormat myFormatter = new SimpleDateFormat(formatString);
        try {
            return myFormatter.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将从0开始的毫秒数转换为hh:mm:ss格式 如果没有hh那么转换为 mm:ss
     *
     * @param milliseconds
     * @return
     */
    public static String parseZeroBaseMilliseconds(int milliseconds) {
        int hh = milliseconds / 1000 / 60 / 60;
        int mm = (milliseconds - (hh * 60 * 60 * 1000)) / 1000 / 60;
        int ss = (milliseconds - (mm * 1000 * 60) - (hh * 60 * 60 * 1000)) / 1000;
        if (hh != 0) {
            return String.format("%02d:%02d:%02d", hh, mm, ss);
        }
        return String.format("%02d:%02d", mm, ss);
    }

    /**
     * 将从0开始的毫秒数转换为hh:mm:ss格式 如果没有hh那么转换为 mm:ss
     *
     * @param milliseconds
     * @return
     */
    public static String parseMilliseconds(String format, long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(milliseconds);
        return hms;
    }

    /**
     * 把秒格式为标准时间模式 hh:mm:ss
     *
     * @param seconds
     * @return
     */
    public static String parseZeroBaseSeconds(int seconds) {
        int hh = seconds / 60 / 60;
        int mm = (seconds - (hh * 60 * 60)) / 60;
        int ss = seconds - (mm * 60) - (hh * 60 * 60);

        return String.format("%02d:%02d:%02d", hh, mm, ss);
    }

    /**
     * int ip地址 to String 地址
     */
    public static String intToIp(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(i & 0xFF);
        sb.append(".");
        sb.append((i >> 8) & 0xFF);
        sb.append(".");
        sb.append((i >> 16) & 0xFF);
        sb.append(".");
        sb.append((i >> 24) & 0xFF);
        return sb.toString();
    }

    public static final String[] filterStr = new String[]{".com", ".cn", ".mobi", ".co", ".net", ".so", ".org",
            ".gov", ".tel", ".tv", ".biz", ".cc", ".hk", ".name", ".info", ".asia", ".me", ".us"};

    private static String getFilterInfoPattern() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\[.*?(");
        int size = filterStr.length;
        for (int i = 0; i < size; i++) {
            sb.append(filterStr[i]);
            if (i != size - 1) {
                sb.append("|");
            }
        }
        sb.append(")\\]");
        return sb.toString();
    }

    private static final String FILTER_INFO_PATTERN = getFilterInfoPattern();

    public static String filterInfo(String str) {
        if (str == null) {
            return null;
        }
        Pattern pattern = Pattern.compile(FILTER_INFO_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    public static String formatChar(String source) {
        if (source != null) {
            Pattern p = Pattern.compile("[^a-zA-Z0-9]");
            return p.matcher(source).replaceAll("");
        }
        return null;
    }

    public static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        return isHex(wepKey);
    }

    public static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        return true;
    }

    public static String toHex(byte[] buf, boolean capital) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        String hex = capital ? HEX_CAPITAL : HEX;
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i], hex);
        }
        return result.toString();
    }

    public static String toHex(byte[] buf) {
        return toHex(buf, false);
    }

    private static void appendHex(StringBuffer sb, byte b, String hex) {
        sb.append(hex.charAt((b >> 4) & 0x0f)).append(hex.charAt(b & 0x0f));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String hex16ToHex64(String hexString) {
        if (isEmpty(hexString)) {
            return null;
        }
        int mod = hexString.length() % 3;
        String source;
        // 填充0；
        if (mod == 1) {
            source = hexString + "00";
        } else if (mod == 2) {
            source = hexString + "0";
        } else {
            source = hexString + "000";
        }

        StringBuffer sb = new StringBuffer();
        int len = source.length() / 3;
        for (int i = 0; i < len; i++) {
            int value = Integer.valueOf(source.substring(3 * i, 3 * i + 3), 16);
            appendHex64(sb, value, HEX_64);
        }
        return sb.toString();
    }

    private static void appendHex64(StringBuffer sb, int value, String hex) {
        sb.append(hex.charAt((value >> 6) & 0x3f)).append(hex.charAt(value & 0x3f));
    }

    public static String hex64ToHex16(String hexString) {
        return hex64ToHex16(hexString, false);
    }

    public static String hex64ToHex16(String hexString, boolean capital) {
        if (isEmpty(hexString)) {
            return null;
        }
        int mod = hexString.length() % 2;
        String source;
        // 填充0；
        if (mod == 1) {
            source = hexString + "0";
        } else {
            source = hexString;
        }
        String hex = capital ? HEX_CAPITAL : HEX;
        StringBuffer sb = new StringBuffer();
        int len = source.length() / 2;
        for (int i = 0; i < len; i++) {
            int value = (charToHex64(source.charAt(i * 2)) << 6) | charToHex64(source.charAt(i * 2 + 1));
            appendHex16(sb, value, hex);
        }
        len = sb.length();
        if (len % 3 == 0) {
            // 去掉末尾填充的0；
            String end = sb.substring(len - 3, len);
            if (end.equals("000")) {
                return sb.substring(0, len - 3);
            } else if (end.equals("00")) {
                return sb.substring(0, len - 2);
            } else if (end.endsWith("0")) {
                return sb.substring(0, len - 1);
            } else {
                return sb.toString();
            }
        } else {
            return sb.toString();
        }
    }

    private static void appendHex16(StringBuffer sb, int value, String hex) {
        sb.append(hex.charAt((value >> 8) & 0x0f)).append(hex.charAt((value >> 4) & 0x0f)).append(hex.charAt(value & 0x0f));
    }

    private static int charToHex64(char c) {
        if (c >= 48 && c <= 57) {
            // 0~9 0~9
            return c - 48;
        } else if (c >= 97 && c <= 122) {
            // a~z 10~35
            return c - 87;
        } else if (c >= 65 && c <= 90) {
            // A~Z 36~61
            return c - 29;
        } else if (c == '_') {
            return 62;
        } else if (c == '@') {
            return 63;
        } else {
            return 0;
        }
    }

    public static String toQuotedString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        final int lastPos = str.length() - 1;
        if (lastPos < 0 || (str.charAt(0) == '"' && str.charAt(lastPos) == '"')) {
            return str;
        }
        return "\"" + str + "\"";
    }

    public static String urlEncode(String url) {
        StringBuffer urlB = new StringBuffer();
        StringTokenizer st = new StringTokenizer(url, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals("/"))
                urlB.append("/");
            else if (tok.equals(" "))
                urlB.append("%20");
            else {
                try {
                    urlB.append(URLEncoder.encode(tok, "UTF-8"));
                } catch (java.io.UnsupportedEncodingException uee) {

                }
            }
        }
        // Log.d(TAG, "urlEncode urlB:" + urlB.toString());
        return urlB.toString();
    }

    /**
     * 将若干条字符串拼接成一条,多条之间的分隔符为分隔符连接
     *
     * @param list
     * @return
     */
    public static List<String> composeStringList(List<String> list, int maxLength, String split) {
        if (list == null || list.size() <= 1 || split == null || maxLength <= 0) {
            return list;
        }

        ArrayList<String> comList = new ArrayList<String>();
        StringBuilder comString = new StringBuilder(list.get(0));

        for (int i = 1; i < list.size(); i++) {
            if ((comString.length() + list.get(i).length()) >= maxLength) {
                comList.add(comString.toString());
                comString = new StringBuilder(list.get(i));
            } else {
                comString.append(split).append(list.get(i));
            }
        }

        comList.add(comString.toString());
        return comList;
    }

    /**
     * [获取指定长度的字符串]<br/>
     * 以半角长度为准
     *
     * @param str
     * @param len
     * @param symbol
     * @return
     */
    public static String getLimitLengthString(String str, int len, String symbol) {
        try {
            int counterOfDoubleByte = 0;
            byte[] b = str.getBytes("GBK");
            if (b.length <= len)
                return str;
            for (int i = 0; i < len; i++) {
                if (b[i] < 0)
                    counterOfDoubleByte++;
            }

            if (counterOfDoubleByte % 2 == 0)
                return new String(b, 0, len, "GBK") + symbol;
            else
                return new String(b, 0, len - 1, "GBK") + symbol;
        } catch (Exception e) {
        }
        return null;
    }

    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    sb.append("&apos;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }


    public static boolean equalsString(String str1, String str2) {
        if (str1 != null) {
            return str1.equals(str2);
        } else {
            return str2 == null;
        }
    }

    /**
     * 比较类似于3.5.0这样的版本号字符串的大小
     *
     * @param str1
     * @param str2
     * @return str1 > str2 返回 正数；str1 = str2 返回 0；str1 < str2 返回 负数；
     */
    public static int compareVerString(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0;
        }

        String[] cons1 = str1.split("\\.");
        String[] cons2 = str2.split("\\.");

        int i = 0;
        try {
            while (i < cons1.length && i < cons2.length) {
                int int1 = Integer.parseInt(cons1[i]);
                int int2 = Integer.parseInt(cons2[i]);
                int res = int1 - int2;
                if (res == 0) {
                    i++;
                    continue;
                } else {
                    return res;
                }
            }

            return cons1.length - cons2.length;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getTopicFormatString(String content) {
        if (isEmpty(content)) {
            return "";
        } else {
            return String.format("#%s#", content);
        }
    }



    /**
     * [格式化文件大小]<br/>
     * 例如： length 1022114 返回 998.2KB
     *
     * @param length
     * @return
     */
    public static String parseFileSizeF(long length) {

        if (length == 0) {
            return "0M";
        }

        String[] syn = {"B", "KB", "M", "G"};
        int i = 0;
        float f = length;
        while (f >= 1024) {
            if (i >= syn.length - 1) {
                break;
            }
            f = f / 1024;
            i++;
        }

        String size = DIGITAL_FORMAT_1.format(f) + syn[i];
        return size;
    }

    /**
     * 格式化文件大小<br />
     * 结果如果是整数，那么不带小数点，结果如果不是整数，保留一位小数点 <br />
     * 例：1024 * 1024 -> 1M <br />
     * 1024f * 1024 * 1029 * 573 ->587.5G <br />
     * 与parseFileSizeF的区别:1024 * 1024 parseFileSizeF转换为1024.0KB
     *
     * @param length
     * @return
     * @author Xiaoyuan
     */
    public static String parseFileSizeF1(long length) {

        if (length == 0) {
            return "0M";
        }

        String[] syn = {"B", "KB", "M", "G"};
        int i = 0;
        float f = length;
        while (f >= 1024) {
            if (i >= syn.length - 1) {
                break;
            }
            f = f / 1024;
            i++;
        }
        String pattern = "";
        pattern = (f - (int) f) > 0 ? "####.0" : "####";
        DecimalFormat format = new DecimalFormat(pattern);
        String size = format.format(f) + syn[i];
        return size;
    }

    public static String parseFileSizeF2(long length) {

        if (length == 0) {
            return "0M";
        }

        String[] syn = {"B", "KB", "M", "G"};
        int i = 0;
        float f = length;
        while (f >= 1024) {
            if (i >= syn.length - 1) {
                break;
            }
            f = f / 1024;
            i++;
        }
        String pattern = "";
        pattern = (f - (int) f) > 0 ? "####.00" : "####";
        DecimalFormat format = new DecimalFormat(pattern);
        String size = format.format(f) + syn[i];
        return size;
    }

    public static String parseHZ(int length) {

        String[] syn = {"HZ", "KHZ", "MHZ", "GHZ"};

        int i = 0;
        while (length > 1000) {
            if (i >= syn.length - 1) {
                break;
            }
            length = length / 1000;
            i++;
        }

        return length + syn[i];
    }

    public static String parseFileSize(long length, boolean hasUnit) {

        long hlen = length;

        String[] syn = {"B", "KB", "M", "G"};
        int i = 0;
        while (hlen > 1024) {
            if (i >= syn.length - 1) {
                break;
            }
            hlen = hlen / 1024;
            i++;
        }

        String str = hlen + "";
        if (i == 2) {
            float llen = (float) length / 1024 / 1024;

            String pattern = "#0.0";
            DecimalFormat format = new DecimalFormat(pattern);

            str = format.format(llen);
        } else if (i == 3) {
            float llen = (float) length / 1024 / 1024 / 1024;

            String pattern = "#0.0";
            DecimalFormat format = new DecimalFormat(pattern);

            str = format.format(llen);
        }

        if (hasUnit) {
            return str + syn[i];
        } else {
            return str;
        }
    }

    /**
     * [格式化文件大小]<br/>
     * 例如： length 1022114 返回 998KB
     *
     * @param length
     * @return
     */
    public static String parseFileSize(long length) {
        return parseFileSize(length, true);
    }

    public static String parseBitrate(long length) {
        return (length / 1000) + " Kbps";
    }

    public static String parseDowloadRate(long length) {
        return parseFileSize(length, true) + "/s";
    }

    /**
     * 格式化数量为以K或者W为单位的字符串
     *
     * @param count 数量
     * @return 显示用字符串
     */
    public static String formatCountKWStr(double count, String pattern) {
        double w = count / 10000;
        DecimalFormat format = pattern != null ? new DecimalFormat(pattern) : null;
        if (w >= 1d) {
            return format != null ? format.format(w) + "w" : ((int) w) + "w";
        } else {
//			double k = count / 1000;
//			if (k >= 1d) {
//				return format != null ? format.format(k) + "k" : ((int)k) + "k";
//			} else {
            return String.valueOf(((int) count));
//			}
        }
    }

    /**
     * 格式化数量为以K或者W为单位的字符串
     *
     * @param count 数量
     * @return 显示用字符串
     */
    public static String formatCountKWStr(double count) {
        return formatCountKWStr(count, null);
    }

    /**
     * 格式化数量为以万为单位的中文字符串
     *
     * @param count 数量
     * @return 显示用字符串
     */
    public static String formatCountChineseStr(double count) {
        double w = count / 10000;
        if (w >= 1d) {
            return ((int) w) + "万";
        } else {
            return String.valueOf(((int) count));
        }
    }

    /**
     * 判断字符串是否开始与emoji表情符号
     *
     * @param str 字符串
     * @return true 是，false 不是。
     */
    public static boolean isStartWithEmoji(String str) {

        boolean ret = false;

        char hs = str.charAt(0);
        // surrogate pair
        if (0xd800 <= hs && hs <= 0xdbff) {
            if (str.length() > 1) {
                char ls = str.charAt(1);
                int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                if (0x1d000 <= uc && uc <= 0x1f77f) {
                    ret = true;
                }
            }
        } else if (str.length() > 1) {
            char ls = str.charAt(1);
            if (ls == 0x20e3) {
                ret = true;
            }

        } else {
            // non surrogate
            if (0x2100 <= hs && hs <= 0x27ff) {
                ret = true;
            } else if (0x2B05 <= hs && hs <= 0x2b07) {
                ret = true;
            } else if (0x2934 <= hs && hs <= 0x2935) {
                ret = true;
            } else if (0x3297 <= hs && hs <= 0x3299) {
                ret = true;
            } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c || hs == 0x2b1b || hs == 0x2b50) {
                ret = true;
            }
        }

        return ret;
    }

    /**
     * 获取EditText的内容字符串
     *
     * @param editText
     * @return content
     */
    public static String getEditTextContent(EditText editText) {
        String content = null;
        if (editText == null) {
            return null;
        }

        if (editText.getText() == null) {
            return null;
        }

        try {
            content = editText.getText().toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return content;
    }

    public static String removeBlackChars(String source) {
        return source != null ? source.replaceAll("\\s*", "") : null;
    }

    public static boolean isNotEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    public static int getEmojiCharNums(String source) {
        int num = 0;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isNotEmojiCharacter(codePoint)) {
                num++;
            }
        }
        return num;
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        int len = source.length();
        StringBuilder buf = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isNotEmojiCharacter(codePoint)) {
                buf.append(codePoint);
            }
        }
        return buf.toString();
    }

    public static HashMap<String, String> parseParams(String url) {
        if (url != null) {
            int index = url.indexOf('?');
            if (index >= 0) {
                String tmp = url.substring(index + 1);
                String[] pairs = tmp.split("&");
                HashMap<String, String> result = new HashMap<>();
                for (String pair : pairs) {
                    String[] vals = pair.split("=");
                    if (vals != null && vals.length == 2) {
                        result.put(vals[0], vals[1]);
                    }
                }
                return result;
            }
        }
        return null;
    }


    /**
     * 获取字符串长度，汉字为2个字符长度
     *
     * @return
     */
    public static int getChineseStrLength(String str) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }


    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    //百分比
    public static int getPercentage(int molecule, int denominator) {
        if (denominator == 0) {
            return 0;
        } else {
            String str = (float) molecule / (float) denominator * 100 + "";
            return Integer.parseInt(str.substring(0, str.indexOf(".")));
        }
    }

    public static String formateTime(long time) {
        //格式化输出时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //格式还有yyMMdd hh:mm:ss yyyy-MM-dd hh:mm:ss dd-MM-yyyy hh:mm:ss等，注意，月为“M”，分为"m"，HH为24小时制，hh为12小时制
        //"今天是"+"yyyy年MM月dd日 E kk点mm分"(E代表星期几，显示为"星期x")
        Date date = new Date(time);
        String formatTime = format.format(date);
        return formatTime;
    }

    public static String formateTime(long time, String formatStr) {
        //格式化输出时间
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        //格式还有yyMMdd hh:mm:ss yyyy-MM-dd hh:mm:ss dd-MM-yyyy hh:mm:ss等，注意，月为“M”，分为"m"，HH为24小时制，hh为12小时制
        //"今天是"+"yyyy年MM月dd日 E kk点mm分"(E代表星期几，显示为"星期x")
        Date date = new Date(time);
        String formatTime = format.format(date);
        return formatTime;
    }

    public static String dealTime(String time) {
        if (TextUtils.isEmpty(time) || time.length() < 8)
            return "";
        StringBuffer sb = new StringBuffer(time.substring(0, 4));
        sb.append("-");
        sb.append(time.substring(4, 6));
        sb.append("-");
        sb.append(time.substring(6, 8));

        return sb.toString();
    }

    public static String dealTime(int time) {
        if (time <= 0 || time > Math.pow(9, 8))
            return "";
        int year = time / 10000;
        int month = time % 10000 / 100;
        int day = time % 100;

        StringBuffer sb = new StringBuffer(String.valueOf(year));
        sb.append("-");
        if (month < 10)
            sb.append("0");
        sb.append(month);
        sb.append("-");
        sb.append(day);

        return sb.toString();
    }

    /**
     * 判断是否今天
     */
    public static boolean isNewToday(long time) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() / 1000 < time) {
            return true;
        }

        return false;
    }
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * 判断两字符串是否相等
     *
     * @param a 待校验字符串a
     * @param b 待校验字符串b
     * @return @return {@code true}: 相等<br>{@code false}: 不相等
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * null转为长度为0的字符串
     *
     * @param s 待转字符串
     * @return s为null转为长度为0字符串，否则不改变
     */
    public static String null2Length0(String s) {
        return s == null ? "" : s;
    }

    /**
     * 返回字符串长度
     *
     * @param s 字符串
     * @return null返回0，其他返回自身长度
     */
    public static int length(CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     * @param s 待反转字符串
     * @return 反转字符串
     */
    public static String reverse(String s) {
        int len = length(s);
        if (len <= 1) return s;
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 转化为半角字符
     *
     * @param s 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     *
     * @param s 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(String s) {
        if (isEmpty(s)) {
            return s;
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    private static int[] pyValue = new int[]{-20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242,
            -20230, -20051, -20036, -20032,
            -20026, -20002, -19990, -19986, -19982, -19976, -19805, -19784, -19775, -19774, -19763, -19756, -19751,
            -19746, -19741, -19739, -19728,
            -19725, -19715, -19540, -19531, -19525, -19515, -19500, -19484, -19479, -19467, -19289, -19288, -19281,
            -19275, -19270, -19263, -19261,
            -19249, -19243, -19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006,
            -19003, -18996, -18977, -18961,
            -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697, -18696,
            -18526, -18518, -18501, -18490,
            -18478, -18463, -18448, -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201, -18184, -18183,
            -18181, -18012, -17997, -17988,
            -17970, -17964, -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721,
            -17703, -17701, -17697, -17692,
            -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185, -16983,
            -16970, -16942, -16915, -16733,
            -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470, -16465, -16459, -16452, -16448, -16433,
            -16429, -16427, -16423, -16419,
            -16412, -16407, -16403, -16401, -16393, -16220, -16216, -16212, -16205, -16202, -16187, -16180, -16171,
            -16169, -16158, -16155, -15959,
            -15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, -15681, -15667, -15661,
            -15659, -15652, -15640, -15631,
            -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369,
            -15363, -15362, -15183, -15180,
            -15165, -15158, -15153, -15150, -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121, -15119,
            -15117, -15110, -15109, -14941,
            -14937, -14933, -14930, -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902, -14894, -14889,
            -14882, -14873, -14871, -14857,
            -14678, -14674, -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429, -14407, -14399, -14384,
            -14379, -14368, -14355, -14353,
            -14345, -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135, -14125, -14123, -14122, -14112,
            -14109, -14099, -14097, -14094,
            -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878,
            -13870, -13859, -13847, -13831,
            -13658, -13611, -13601, -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383, -13367, -13359,
            -13356, -13343, -13340, -13329,
            -13326, -13318, -13147, -13138, -13120, -13107, -13096, -13095, -13091, -13076, -13068, -13063, -13060,
            -12888, -12875, -12871, -12860,
            -12858, -12852, -12849, -12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556,
            -12359, -12346, -12320, -12300,
            -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798, -11781,
            -11604, -11589, -11536, -11358,
            -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041, -11038, -11024,
            -11020, -11019, -11018, -11014,
            -10838, -10832, -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329,
            -10328, -10322, -10315, -10309,
            -10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254};

    private static String[] pyStr = new String[]{"a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao",
            "bei", "ben", "beng", "bi", "bian",
            "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai",
            "chan", "chang", "chao", "che",
            "chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci",
            "cong", "cou", "cu", "cuan",
            "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die",
            "ding", "diu", "dong", "dou", "du",
            "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou",
            "fu", "ga", "gai", "gan", "gang",
            "gao", "ge", "gei", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun",
            "guo", "ha", "hai", "han", "hang",
            "hao", "he", "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun",
            "huo", "ji", "jia", "jian",
            "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan",
            "kang", "kao", "ke", "ken",
            "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan",
            "lang", "lao", "le", "lei", "leng",
            "li", "lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv", "luan",
            "lue", "lun", "luo", "ma", "mai",
            "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min", "ming", "miu",
            "mo", "mou", "mu", "na", "nai",
            "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao", "nie", "nin", "ning",
            "niu", "nong", "nu", "nv", "nuan",
            "nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao",
            "pie", "pin", "ping", "po", "pu",
            "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun",
            "ran", "rang", "rao", "re",
            "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", "sang", "sao",
            "se", "sen", "seng", "sha",
            "shai", "shan", "shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan",
            "shuang", "shui", "shun",
            "shuo", "si", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta", "tai", "tan", "tang", "tao", "te",
            "teng", "ti", "tian", "tiao",
            "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei",
            "wen", "weng", "wo", "wu", "xi",
            "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya",
            "yan", "yang", "yao", "ye", "yi",
            "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang", "zao", "ze",
            "zei", "zen", "zeng", "zha",
            "zhai", "zhan", "zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai",
            "zhuan", "zhuang", "zhui",
            "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"};

    /**
     * 单个汉字转成ASCII码
     *
     * @param s 单个汉字字符串
     * @return 如果字符串长度是1返回的是对应的ascii码，否则返回-1
     */
    private static int oneCn2ASCII(String s) {
        if (s.length() != 1) return -1;
        int ascii = 0;
        try {
            byte[] bytes = s.getBytes("GB2312");
            if (bytes.length == 1) {
                ascii = bytes[0];
            } else if (bytes.length == 2) {
                int highByte = 256 + bytes[0];
                int lowByte = 256 + bytes[1];
                ascii = (256 * highByte + lowByte) - 256 * 256;
            } else {
                throw new IllegalArgumentException("Illegal resource string");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ascii;
    }

    /**
     * 单个汉字转成拼音
     *
     * @param s 单个汉字字符串
     * @return 如果字符串长度是1返回的是对应的拼音，否则返回{@code null}
     */
    private static String oneCn2PY(String s) {
        int ascii = oneCn2ASCII(s);
        if (ascii == -1) return null;
        String ret = null;
        if (0 <= ascii && ascii <= 127) {
            ret = String.valueOf((char) ascii);
        } else {
            for (int i = pyValue.length - 1; i >= 0; i--) {
                if (pyValue[i] <= ascii) {
                    ret = pyStr[i];
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 获得第一个汉字首字母
     *
     * @param s 单个汉字字符串
     * @return 拼音
     */
    public static String getPYFirstLetter(String s) {
        if (isSpace(s)) return "";
        String first, py;
        first = s.substring(0, 1);
        py = oneCn2PY(first);
        if (py == null) return null;
        return py.substring(0, 1);
    }

    /**
     * 中文转拼音
     *
     * @param s 汉字字符串
     * @return 拼音
     */
    public static String cn2PY(String s) {
        String hz, py;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            hz = s.substring(i, i + 1);
            py = oneCn2PY(hz);
            if (py == null) {
                py = "?";
            }
            sb.append(py);
        }
        return sb.toString();
    }
}
