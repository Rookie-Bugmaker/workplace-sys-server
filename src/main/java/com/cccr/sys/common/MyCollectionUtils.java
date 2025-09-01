package com.cccr.sys.common;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * List集合工具类
 *
 * @author 谭永强
 * @date 2023-03-16
 */
public class MyCollectionUtils {

    /**
     * 获取集合中某个属性的值集
     *
     * @param list      集合
     * @param fieldName 属性名称
     * @param <T>       泛型
     * @return 值集
     */
    public static <T> List<T> getFieldValueList(List<?> list, String fieldName) {
        List<T> result = new ArrayList<>();
        if (ObjectUtils.isEmpty(list)) {
            return result;
        }
        try {
            for (Object obj : list) {
                Class<?> clazz = obj.getClass();
                //获取指定名字的私有域
                Field field = clazz.getDeclaredField(fieldName);
                //打开私有访问
                field.setAccessible(true);
                result.add((T) field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 向目标集合中设置源集合数据
     * 当目标判断字段值==源判断字段值时，就把相应的源数据set到目标集合中
     *
     * @param targetList           目标集合
     * @param sourceList           源集合
     * @param targetFieldName      目标字段
     * @param sourceFieldName      源字段,当源字段为空时，则获取当前源对象
     * @param targetJudgeFieldName 目标判断字段
     * @param sourceJudgeFieldName 源判断字段
     * @param flag                 true/false:覆盖/追加
     */
    public static void setFieldValue(List<?> targetList, List<?> sourceList, String targetFieldName, String sourceFieldName, String targetJudgeFieldName, String sourceJudgeFieldName, boolean flag) {
        if (ObjectUtils.isEmpty(targetList) || ObjectUtils.isEmpty(sourceList) || StringUtils.isEmpty(targetFieldName) || StringUtils.isEmpty(targetJudgeFieldName) || StringUtils.isEmpty(sourceJudgeFieldName)) {
            return;
        }
        try {
            for (Object target : targetList) {
                Class<?> targetClass = target.getClass();
                //目标判断字段
                Field targetJudgeField = targetClass.getDeclaredField(targetJudgeFieldName);
                targetJudgeField.setAccessible(true);
                if (ObjectUtils.isEmpty(targetJudgeField.get(target))) {
                    continue;
                }
                //目标字段
                Field targetField = targetClass.getDeclaredField(targetFieldName);
                targetField.setAccessible(true);
                //字段数据类型
                Class<?> type = targetField.getType();
                List<Object> list = null;
                Set<Object> set = null;
                Object obj = null;
                if (type.equals(List.class)) {
                    list = new ArrayList<>();
                }
                if (type.equals(Set.class)) {
                    set = new HashSet<>();
                }
                for (Object source : sourceList) {
                    Class<?> sourceClass = source.getClass();
                    //源判断字段
                    Field sourceJudgeField = sourceClass.getDeclaredField(sourceJudgeFieldName);
                    sourceJudgeField.setAccessible(true);
                    //源值
                    Object value = sourceJudgeField.get(source);
                    if (ObjectUtils.isEmpty(value)) {
                        continue;
                    }
                    //目标值
                    Object targetValue = targetJudgeField.get(target);
                    if (ObjectUtils.isEmpty(value)) {
                        continue;
                    }
                    if (value.toString().equals(targetValue.toString())) {
                        if (list != null) {
                            list.add(source);
                            continue;
                        }
                        if (set != null) {
                            set.add(source);
                            continue;
                        }
                        if (StringUtils.isEmpty(sourceFieldName)) {
                            obj = source;
                        } else {
                            //目标字段
                            Field sourceField = sourceClass.getDeclaredField(sourceFieldName);
                            sourceField.setAccessible(true);
                            obj = sourceField.get(source);
                        }
                    }
                }
                Object targetValue = targetField.get(target);
                if (!ObjectUtils.isEmpty(list)) {
                    if (flag || ObjectUtils.isEmpty(targetValue)) {
                        targetField.set(target, list);
                    } else {
                        targetField.set(target, list.addAll((Collection<?>) targetValue));
                    }
                    continue;
                }
                if (!ObjectUtils.isEmpty(set)) {
                    if (flag || ObjectUtils.isEmpty(targetValue)) {
                        targetField.set(target, set);
                    } else {
                        targetField.set(target, set.addAll((Collection<?>) targetValue));
                    }
                    continue;
                }
                if (!ObjectUtils.isEmpty(obj)) {
                    targetField.set(target, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 比较两个list中不同的元素
     *
     * @param list1 集合1
     * @param list2 集合2
     * @return 结果
     */
    private static List<String> getDifference(List<String> list1, List<String> list2) {
        List<String> diff = new ArrayList<>();
        long start = System.currentTimeMillis();
        Map<String, Integer> map = new HashMap<>(list1.size() + list2.size());
        List<String> maxList = list1;
        List<String> minList = list2;
        if (list2.size() > list1.size()) {
            maxList = list2;
            minList = list1;
        }
        for (String string : maxList) {
            map.put(string, 1);
        }
        for (String string : minList) {
            Integer count = map.get(string);
            if (count != null) {
                map.put(string, ++count);
                continue;
            }
            map.put(string, 1);
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        System.out.println("方法4 耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
        return diff;

    }
}
