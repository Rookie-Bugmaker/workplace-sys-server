package com.crrc.sys.common;

/**
 * 自定义工具类
 *
 * @author 谭永强
 * @date 2024-08-01
 */
public class MyUtils {

    /**
     * 判断字符串中是否存在超过3位的连续(数字/字母)或相同(数字/字母)
     * (例如：123、4567、7890、888、abc、cba、aaa)
     *
     * @param nums 字符串
     * @return true:存在3位及以上连续(数字/字母)、false:不存在3位连续(数字/字母)
     */
    public static boolean isContinuousDigits(String nums) {
        //正序
        int ascCount = 1;
        //倒叙
        int descCount = 1;
        //相同
        int equalCount = 1;
        for (int i = 0; i < nums.length(); i++) {
            //获取当前字符的ASCII
            int c1 = nums.charAt(i);
            //获取下一个字符的ASCII
            int index = (i + 1);
            if (index >= nums.length()) {
                break;
            }
            int c2 = nums.charAt(index);
            //当前数字+1等于后一个数字，说明这两个数字是连续数字
            if ((c1 + 1) == c2) {
                ascCount++;
                if (ascCount >= 3) {
                    //存在3位及以上的连续数字或字母(正序)
                    return true;
                }
                continue;
            }
            //当前字符-1等于后一个字符，说明这两个字符是连续字符
            if ((c1 - 1) == c2) {
                descCount++;
                if (descCount >= 3) {
                    //存在3位及以上的连续数字或字母(倒叙)
                    return true;
                }
                continue;
            }
            //当前字符等于后一个字符
            if (c1 == c2) {
                equalCount++;
                if (equalCount >= 3) {
                    //存在3位及以上的相同数字或字母
                    return true;
                }
                continue;
            }
            ascCount = 1;
            descCount = 1;
            equalCount = 1;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("abc:" + isContinuousDigits("abAA123bbb"));
    }
}
