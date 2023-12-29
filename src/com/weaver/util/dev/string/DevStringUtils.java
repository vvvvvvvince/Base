package com.weaver.util.dev.string;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 字符串相关工具类
 * e9源码weaver.general.Util中也有相关字符操作方法
     * null2s(String s,String defaultVal)
     * null2String(String s,String defaultVal)
     * 转换页面中的特殊字符 convertInput2DB
     * EXCEL导出数据去除HTML元素（TD12523） toExcelData
     * 内容多语言拼接 toMultiLangScreen
     * 流程页面字符串转换为保存到数据库中的字符串 toHtmlForWorkflow
     * 解决utf-8编码下，&nbsp;会被转换为乱码？的问题 htmlFilter4UTF8
     * 保存到数据库中的字符串转换为流程页面字符串 toScreenForWorkflow
     * 创建随机字符串 passwordBuilder
     * 小写金额转化为大写 numtochinese
     * 用于去除XML文件中的特殊字符 filterForXml
     * 判断文件是否为图片 isPicture
     * 将英文单引号转换为中文单引号 convertDbInput
     * 检测文件内容是否含有非法字符 isValidFile
     * 当SQL语句IN查询子句列表超过1000，将会查询报错。故在此将In语句分隔成  getSubINClause
     * 获取客户端的IP getIpAddr
     * 检测文件格式是否合法 validateFileExt
 */
public class DevStringUtils {
    @Test
    public void Test1(){
        String ss="dsada213sadhu的撒";
        System.out.println(replace(ss, "d", "哈",3));
        char[] chars = ss.toCharArray();
        for (char aChar : chars) {
            if(isChineseChar(aChar)){
                System.out.println(aChar);
            }
        }
    }


    // 为了兼容apache StringUtils
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    public static boolean isEmpty(String str){
        return StringUtils.isEmpty(str);
    }

    public static String replace(String text, String repl, String with, int max) {
        if (!isEmpty(text) && !isEmpty(repl) && with != null && max != 0) {
            int start = 0;
            int end = text.indexOf(repl, start);
            if (end == -1) {
                return text;
            } else {
                int replLength = repl.length();
                int increase = with.length() - replLength;
                increase = Math.max(increase, 0);
                increase *= max < 0 ? 16 : (Math.min(max, 64));

                StringBuffer buf;
                for (buf = new StringBuffer(text.length() + increase); end != -1; end = text.indexOf(repl, start)) {
                    buf.append(text.substring(start, end)).append(with);
                    start = end + replLength;
                    --max;
                    if (max == 0) {
                        break;
                    }
                }

                buf.append(text.substring(start));
                return buf.toString();
            }
        } else {
            return text;
        }
    }


    /**
     * 是否汉字字符
     *
     * @param c
     * @return
     */
    public static boolean isChineseChar(char c) {
        return String.valueOf(c).matches("[\u4e00-\u9fa5]");
    }

    /**
     * 把中文转成Unicode码
     *
     * @param str
     * @return
     */
    public static String chinaToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }


    /**
     * 截取字符串尾部指定长度
     *
     * @param str 字符串源
     * @param len 截取长度
     * @return 符串尾部指定长度
     */
    public static String tailSubstring(String str, int len) {
        if (len <= 0) return str;
        if (str.length() < len) throw new RuntimeException("截取长度大于字符串本身");
        return str.substring(str.length() - len);
    }

    public static String tailChar(String str) {
        return tailSubstring(str, 1);
    }

    public static String headChar(String str) {
        return str.substring(0, 1);
    }


    /**
     * 判断字符串是否相等
     *
     * @param str1 源串
     * @param str2 比较串
     * @return
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        else return str1.equals(str2);
    }

    /**
     * 判断字符串是否相等 忽略大小写
     *
     * @param str1 源串
     * @param str2 比较串
     * @return
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        else return str1.equalsIgnoreCase(str2);
    }

    /**
     * 比较Object 与字符串是否相等 会使用toString
     *
     * @param obj 比较源
     * @param str 对比串
     * @return
     */
    public static boolean equals(Object obj, String str) {
        if (obj == null) return equals(null, str);
        return equals(obj.toString(), str);
    }

    public static boolean equals(Object obj1, Object obj2) {
        if ((obj1 == null && obj2 != null) || (obj1 != null && obj2 == null)) {
            return false;
        }
        if (obj1 == null || obj1.equals(obj2)) {
            return true;
        }
        return equals(obj1.toString(), obj2.toString());
    }

    /**
     * 比较Object 与字符串是否相等 会使用toString 忽略大小写
     *
     * @param obj 比较源
     * @param str 对比串
     * @return
     */
    public static boolean equalsIgnoreCase(Object obj, String str) {
        if (obj == null) return equals(null, str);
        return equalsIgnoreCase(obj.toString(), str);
    }


    @Test
    public void test2(){
        Map valuesMap = new HashMap();
        valuesMap.put("animal", "quick brown fox");
        valuesMap.put("target", "lazy dog");
        String templateString = "The ${animal} jumped over the ${target}.";

        //分割测试
        System.out.println(Arrays.toString(tokenizeToStringArray(null,
                ",", true, false)));
    }
    /**
     * 模板替换
     * @param replaceValue
     * @param tpl
     * @return
     */
    public static String replaceTpl(Map<String, String> replaceValue, String tpl) {
        StrSubstitutor strSubstitutor = new StrSubstitutor(replaceValue);
        return strSubstitutor.replace(tpl);
    }

    /**
     * 按字符截取长度
     *
     * @param text      原文本
     * @param maxLength 截取长度（按字符）
     * @return
     */
    public static String subStringByChar(String text, Integer maxLength) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        //名称最多展示14个字符，一个汉字算两个字符，超过展示...
        StringBuilder sBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        int length = 0;
        for (char ch : chars) {
            boolean chineseChar = isChineseChar(ch) || isSymbol(ch);
            length = length + (chineseChar ? 2 : 1);
            if (length > maxLength) {
                break;
            }
            sBuilder.append(ch);
        }
        String testResult = sBuilder.toString();
        if (length > maxLength) {
            testResult = testResult;
        }
        return testResult;
    }

    /**
     * 是否中文符号
     *
     * @param ch
     * @return
     */
    public static boolean isSymbol(char ch) {
        if (isCnSymbol(ch)) return true;
        if (isEnSymbol(ch)) return true;

        if (0x2010 <= ch && ch <= 0x2017) return true;
        if (0x2020 <= ch && ch <= 0x2027) return true;
        if (0x2B00 <= ch && ch <= 0x2BFF) return true;
        if (0xFF03 <= ch && ch <= 0xFF06) return true;
        if (0xFF08 <= ch && ch <= 0xFF0B) return true;
        if (ch == 0xFF0D || ch == 0xFF0F) return true;
        if (0xFF1C <= ch && ch <= 0xFF1E) return true;
        if (ch == 0xFF20 || ch == 0xFF65) return true;
        if (0xFF3B <= ch && ch <= 0xFF40) return true;
        if (0xFF5B <= ch && ch <= 0xFF60) return true;
        if (ch == 0xFF62 || ch == 0xFF63) return true;
        if (ch == 0x0020 || ch == 0x3000) return true;
        return false;

    }

    public static boolean isCnSymbol(char ch) {
        if (0x3004 <= ch && ch <= 0x301C) return true;
        if (0x3020 <= ch && ch <= 0x303F) return true;
        return false;
    }

    public static boolean isEnSymbol(char ch) {

        if (ch == 0x40) return true;
        if (ch == 0x2D || ch == 0x2F) return true;
        if (0x23 <= ch && ch <= 0x26) return true;
        if (0x28 <= ch && ch <= 0x2B) return true;
        if (0x3C <= ch && ch <= 0x3E) return true;
        if (0x5B <= ch && ch <= 0x60) return true;
        if (0x7B <= ch && ch <= 0x7E) return true;

        return false;
    }

    private static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * @description:分割字符
     * @param: str  源字符
     * @param: delimiters 分割字符
     * @param: trimTokens 是否去空格
     * @param: ignoreEmptyTokens 是否忽略空字符
     * @return: java.lang.String[]
     **/
    public static String[] tokenizeToStringArray(@Nullable String str,
                                                 String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (org.apache.commons.lang.StringUtils.isBlank(str)) return EMPTY_STRING_ARRAY;
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(@Nullable Collection<String> collection) {
        return (!CollectionUtils.isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
    }

    public static String[] tokenizeToStringArray(@Nullable String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

}

