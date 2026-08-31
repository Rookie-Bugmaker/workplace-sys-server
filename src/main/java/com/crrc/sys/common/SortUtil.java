//package com.crrc.sys.common;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
//import com.github.yulichang.wrapper.MPJLambdaWrapper;
//
//import java.lang.invoke.SerializedLambda;
//import java.lang.reflect.Method;
//import java.util.*;
//
/// **
// * 通用排序工具类
// * 兼容 LambdaQueryWrapper 和 MPJLambdaWrapper
// * 通过白名单机制保证 SQL 安全，列名由 SFunction 方法引用自动推导
// *
// * @author claude
// */
//public class SortUtil {
//
//    /**
//     * mybatis-plus-join 框架生成的主表别名
//     */
//    private static final String MPJ_MAIN_ALIAS = "t";
//
//    private static final Set<String> ALLOWED_ORDERS = new HashSet<>(Arrays.asList("asc", "desc"));
//
//    /**
//     * 获取 mybatis-plus-join 主表别名
//     */
//    public static String mainTableAlias() {
//        return MPJ_MAIN_ALIAS;
//    }
//
//    // ===================== MPJLambdaWrapper 排序 =====================
//
//    /**
//     * 对 MPJLambdaWrapper 应用动态排序
//     * 会先清除 wrapper 中已有的 ORDER BY，再应用新排序
//     *
//     * @param wrapper        查询包装器
//     * @param sortField      排序字段（前端传入，实体属性名驼峰）
//     * @param sortOrder      排序方向 asc/desc
//     * @param columnMap      白名单映射：前端属性名 -> 数据库列名（可含表别名前缀，如 "t.create_date" 或 "t2.full_name"）
//     * @param defaultSortField 默认排序列名（不含 ORDER BY 关键字，如 "t.create_date"）
//     */
//    public static <T> void applySort(MPJLambdaWrapper<T> wrapper,
//                                     String sortField,
//                                     String sortOrder,
//                                     Map<String, String> columnMap,
//                                     SFunction<T, ?> defaultSortField) {
//        clearWrapperOrderBy(wrapper);
//
//        String sortColumn = null;
//        String resolvedOrder = resolveOrder(sortOrder);
//
//        if (sortField != null && !sortField.trim().isEmpty()) {
//            sortColumn = columnMap.get(sortField);
//        }
//        if (sortColumn == null) {
//            sortColumn = columnMap.get(getPropertyName(defaultSortField));
//        }
//
//        // 使用 orderBy 而非 apply，避免 ORDER BY 被拼接到 WHERE 子句中
//        wrapper.orderBy(true, "asc".equalsIgnoreCase(resolvedOrder), sortColumn);
//    }
//
//    /**
//     * 对 MPJLambdaWrapper 应用多字段动态排序
//     *
//     * @param wrapper        查询包装器
//     * @param sortFields     排序字段，逗号分隔（如 "createDate,userName"）
//     * @param sortOrders     排序方向，逗号分隔（如 "desc,asc"）
//     * @param columnMap      白名单映射
//     * @param defaultSortField 默认排序列名
//     */
//    public static <T> void applySorts(MPJLambdaWrapper<T> wrapper,
//                                      String sortFields,
//                                      String sortOrders,
//                                      Map<String, String> columnMap,
//                                      SFunction<T, ?> defaultSortField) {
//        clearWrapperOrderBy(wrapper);
//
//        if (sortFields == null || sortFields.trim().isEmpty()) {
//            wrapper.orderBy(true, true, columnMap.get(getPropertyName(defaultSortField)));
//            return;
//        }
//
//        String[] fields = sortFields.split(",");
//        String[] orders = sortOrders != null ? sortOrders.split(",") : new String[0];
//
//        boolean applied = false;
//        for (int i = 0; i < fields.length; i++) {
//            String field = fields[i].trim();
//            String dbColumn = columnMap.get(field);
//            if (dbColumn == null) continue;
//            String order = i < orders.length ? orders[i].trim() : "DESC";
//            if (!ALLOWED_ORDERS.contains(order.toLowerCase())) order = "DESC";
//            wrapper.orderBy(true, "asc".equalsIgnoreCase(order), dbColumn);
//            applied = true;
//        }
//
//        if (!applied) {
//            wrapper.orderBy(true, true, columnMap.get(getPropertyName(defaultSortField)));
//        }
//    }
//
//    /**
//     * 清除 wrapper 中已有的 ORDER BY 子句
//     * 兼容 mybatis-plus-join 1.5.4 + mybatis-plus 3.5.5
//     */
//    private static void clearWrapperOrderBy(Object wrapper) {
//        try {
//            // 方式1：尝试调用 clearOrderBy() 方法
//            Method clearMethod = wrapper.getClass().getMethod("clearOrderBy");
//            clearMethod.invoke(wrapper);
//            return;
//        } catch (NoSuchMethodException ignored) {
//            // 方式2：通过反射清除 orderBy 集合
//            try {
//                // MPJLambdaWrapper -> MPJBaseWrapper -> QueryWrapper -> AbstractWrapper
//                Class<?> clazz = wrapper.getClass();
//                while (clazz != null && !clazz.getName().contains("AbstractWrapper")) {
//                    clazz = clazz.getSuperclass();
//                }
//                if (clazz != null) {
//                    Method clearMethod = clazz.getMethod("clearOrderBy");
//                    clearMethod.invoke(wrapper);
//                    return;
//                }
//            } catch (Exception e2) {
//                // 忽略
//            }
//        } catch (Exception e) {
//            // 忽略
//        }
//    }
//
//    // ===================== LambdaQueryWrapper 排序 =====================
//
//    /**
//     * 对 LambdaQueryWrapper 应用动态排序
//     *
//     * @param wrapper        查询包装器
//     * @param sortField      排序字段（前端传入，实体属性名驼峰）
//     * @param sortOrder      排序方向 asc/desc
//     * @param columnMap      白名单映射：字段名 -> SFunction 方法引用
//     * @param defaultColumn  默认排序列的 SFunction 方法引用
//     */
//    public static <T> void applySort(LambdaQueryWrapper<T> wrapper,
//                                     String sortField,
//                                     String sortOrder,
//                                     Map<String, SFunction<T, ?>> columnMap,
//                                     SFunction<T, ?> defaultColumn) {
//        if (sortField == null || sortField.trim().isEmpty()) {
//            wrapper.orderByDesc(defaultColumn);
//            return;
//        }
//        SFunction<T, ?> func = columnMap.get(sortField);
//        if (func == null) {
//            wrapper.orderByDesc(defaultColumn);
//        } else if ("asc".equalsIgnoreCase(sortOrder)) {
//            wrapper.orderByAsc(func);
//        } else {
//            wrapper.orderByDesc(func);
//        }
//    }
//
//    /**
//     * 对 LambdaQueryWrapper 应用多字段动态排序
//     */
//    public static <T> void applySorts(LambdaQueryWrapper<T> wrapper,
//                                      String sortFields,
//                                      String sortOrders,
//                                      Map<String, SFunction<T, ?>> columnMap,
//                                      SFunction<T, ?> defaultColumn) {
//        if (sortFields == null || sortFields.trim().isEmpty()) {
//            wrapper.orderByDesc(defaultColumn);
//            return;
//        }
//
//        String[] fields = sortFields.split(",");
//        String[] orders = sortOrders != null ? sortOrders.split(",") : new String[0];
//
//        boolean applied = false;
//        for (int i = 0; i < fields.length; i++) {
//            String field = fields[i].trim();
//            SFunction<T, ?> func = columnMap.get(field);
//            if (func == null) continue;
//            String order = i < orders.length ? orders[i].trim() : "DESC";
//            if (!ALLOWED_ORDERS.contains(order.toLowerCase())) order = "DESC";
//            if ("asc".equalsIgnoreCase(order)) {
//                wrapper.orderByAsc(func);
//            } else {
//                wrapper.orderByDesc(func);
//            }
//            applied = true;
//        }
//
//        if (!applied) {
//            wrapper.orderByDesc(defaultColumn);
//        }
//    }
//
//    // ===================== 白名单 Map 构建工具 =====================
//
//    /**
//     * 为指定实体构建带表别名的排序白名单 Map（用于 MPJLambdaWrapper 多表联查）
//     * 用法：SortUtil.sortMapWithAlias(SortUtil.mainTableAlias(),
//     *       SysUserDBEntity::getUserId, SysUserDBEntity::getUserName)
//     *
//     * @param alias   表别名（mybatis-plus-join 主表为 "t"）
//     * @param columns 允许排序的列（SFunction 方法引用，类型安全）
//     */
//    @SafeVarargs
//    public static <T> Map<String, String> sortMapWithAlias(String alias,
//                                                           SFunction<T, ?>... columns) {
//        Map<String, String> map = new LinkedHashMap<>();
//        for (SFunction<T, ?> column : columns) {
//            String dbColumn = resolveColumn(column);
//            map.put(getPropertyName(column), alias + "." + dbColumn);
//        }
//        return map;
//    }
//
//    /**
//     * 为指定实体构建不带表别名的排序白名单 Map（用于 LambdaQueryWrapper）
//     * 用法：Map<String, SFunction<XxxEntity, ?>> map = SortUtil.sortMap(
//     *       XxxEntity::getField1, XxxEntity::getField2)
//     */
//    @SafeVarargs
//    public static <T> Map<String, SFunction<T, ?>> sortMap(SFunction<T, ?>... columns) {
//        Map<String, SFunction<T, ?>> map = new LinkedHashMap<>();
//        for (SFunction<T, ?> column : columns) {
//            map.put(getPropertyName(column), column);
//        }
//        return map;
//    }
//
//    /**
//     * 手动构建排序白名单 Map（支持多表任意列，包括子表列）
//     * 用法：Map<String, String> map = SortUtil.sortMapManual(
//     *       "userId", "t.user_id",
//     *       "fullName", "t2.full_name",
//     *       "createDate", "t.create_date")
//     *
//     * @param entries 交替的 key-value 对（key=前端字段名，value=数据库列名含别名）
//     */
//    public static Map<String, String> sortMapManual(String... entries) {
//        Map<String, String> map = new LinkedHashMap<>();
//        for (int i = 0; i < entries.length; i += 2) {
//            map.put(entries[i], entries[i + 1]);
//        }
//        return map;
//    }
//
//    // ===================== 内部方法 =====================
//
//    private static String resolveOrder(String sortOrder) {
//        return (sortOrder != null && ALLOWED_ORDERS.contains(sortOrder.toLowerCase()))
//                ? sortOrder.toUpperCase() : "DESC";
//    }
//
//    /**
//     * 从 SFunction 方法引用解析属性名（通过 writeReplace 获取 SerializedLambda）
//     */
//    private static <T> String getPropertyName(SFunction<T, ?> func) {
//        try {
//            Method replaceMethod = func.getClass().getDeclaredMethod("writeReplace");
//            replaceMethod.setAccessible(true);
//            SerializedLambda lambda = (SerializedLambda) replaceMethod.invoke(func);
//            String methodName = lambda.getImplMethodName();
//            return getPropertyNameFromMethod(methodName);
//        } catch (Exception e) {
//            return "unknown";
//        }
//    }
//
//    /**
//     * 从 SFunction 方法引用解析数据库列名（驼峰转下划线）
//     */
//    private static <T> String resolveColumn(SFunction<T, ?> func) {
//        return camelToUnderline(getPropertyName(func));
//    }
//
//    private static String getPropertyNameFromMethod(String methodName) {
//        if (methodName.startsWith("get") && methodName.length() > 3) {
//            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
//        }
//        if (methodName.startsWith("is") && methodName.length() > 2) {
//            return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
//        }
//        return methodName;
//    }
//
//    /**
//     * 驼峰转下划线：userName -> user_name
//     */
//    private static String camelToUnderline(String camelCase) {
//        if (camelCase == null || camelCase.isEmpty()) {
//            return camelCase;
//        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < camelCase.length(); i++) {
//            char c = camelCase.charAt(i);
//            if (Character.isUpperCase(c)) {
//                if (i > 0) sb.append('_');
//                sb.append(Character.toLowerCase(c));
//            } else {
//                sb.append(c);
//            }
//        }
//        return sb.toString();
//    }
//}
