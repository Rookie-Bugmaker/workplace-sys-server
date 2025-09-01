package com.crrc.sys.common;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * 中文转拼音
 *
 * @author 谭永强
 * @date 2024-08-01
 */
public class ChineseToPinyin {

    public static void main(String[] args) {
        String chinese = "莲花";
        String pinyin1 = convertToPinyin(chinese);
        String pinyin2 = convertToHanYuPinyin(chinese);
        System.out.println(pinyin1);
        System.out.println(pinyin2);
    }

    /**
     * 中文转拼音(带声调)
     *
     * @param chinese 中文汉字
     * @return 拼音(ni3hao3 ， shi4jie4 ！)
     */
    public static String convertToPinyin(String chinese) {
        StringBuilder pinyin = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (char c : chars) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null) {
                pinyin.append(pinyinArray[0]);
            } else {
                pinyin.append(c);
            }
        }
        return pinyin.toString();
    }

    /**
     * 中文转拼音(带声调)
     *
     * @param chinese 中文汉字
     * @return 拼音(nihao ， shijie ！)
     */
    public static String convertToHanYuPinyin(String chinese) {
        // 设置不带声调的输出选项
        net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat format = new net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat();
        format.setToneType(net.sourceforge.pinyin4j.format.HanyuPinyinToneType.WITHOUT_TONE);
        StringBuilder pinyin = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            try {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    pinyin.append(pinyinArray[0]);
                } else {
                    pinyin.append(c);
                }
            } catch (Exception e) {
                pinyin.append(c);
            }
        }
        return pinyin.toString().trim();
    }
}
