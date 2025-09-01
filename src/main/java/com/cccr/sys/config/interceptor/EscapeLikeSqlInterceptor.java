package com.cccr.sys.config.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.cccr.sys.annotation.EnableEscapeLikeSymbol;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 特殊字符拦截处理
 *
 * @author huangsen
 * @date 2023/7/10 19:43
 */
public class EscapeLikeSqlInterceptor implements InnerInterceptor {

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 按?分割字符串表达式
     */
    public static final String PLACEHOLDER_REGEX = "\\?";

    /**
     * 按.分割字符串表达式
     */
    public static final String DOT_REGEX = "\\.";

    /**
     * like操作通配符
     */
    public static final char LIKE_WILDCARD_CHARACTER = '%';

    /**
     * like操作通常会存在的占位符形式
     */
    public static final String PLACEHOLDER = " ? ";

    /**
     * 条件构造器生成sql特有的参数名前缀
     */
    public static final String WRAPPER_PARAMETER_PROPERTY = "ew.paramNameValuePairs.";

    /**
     * like语句在sql中的字符串
     */
    private static final String LIKE_SQL = " like ";

    /**
     * SQL特殊字符
     */
    private static final String SQL_SPECIAL_CHARACTER = "_%*@|&()[]\"'\\";

    /**
     * and操作符
     */
    public static final String AND = "and";

    /**
     * or操作符
     */
    public static final String OR = "or";


    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (check(ms) && needEscape(boundSql.getSql())) {
            escapeSql(boundSql);
        }
    }

    private boolean check(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        if (ObjectUtils.isEmpty(entityClass)) {
            return false;
        }
        //获取实体类的注解
        Annotation[] annotations = entityClass.getAnnotations();
        for (Annotation annotation : annotations) {
            //如果有添加@EnableEscapeLikeSymbol注解，开启转义字符处理
            if (annotation instanceof EnableEscapeLikeSymbol) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转义sql语句中的特殊字符
     * <p>
     * select * from aaa where a like '%_xxx%'
     *
     * @param boundSql 绑定SQL对象
     */
    @SuppressWarnings("unchecked")
    private void escapeSql(BoundSql boundSql) {
        String[] split = boundSql.getSql().split(PLACEHOLDER_REGEX);
        Object parameter = boundSql.getParameterObject();
        Set<String> processedProperty = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            // like 通常在末尾
            if (CharSequenceUtil.lastIndexOfIgnoreCase(split[i], LIKE_SQL) > -1) {
                if (parameter instanceof Map) {
                    // 拿到此位置的"?"对应的参数名
                    String property = boundSql.getParameterMappings().get(i).getProperty();
                    // 防止重复转义
                    if (processedProperty.contains(property)) {
                        continue;
                    }
                    Map<Object, Object> parameterMap = (Map<Object, Object>) parameter;
                    if (containWrapper(property)) {
                        // 条件构造器构造sql方式
                        handleWrapperEscape(property, parameterMap);
                    } else {
                        // 自定义sql方式
                        handleCustomSqlEscape(property, parameterMap);
                    }
                    processedProperty.add(property);
                } else if (parameter instanceof String) {
                    BeanUtil.setFieldValue(boundSql.getParameterObject(), "value", addSalashes(((String) parameter)).toCharArray());
                }

                builder.append(split[i]).append(PLACEHOLDER).append(" escape  '\\'");
            } else {
                if (i != split.length - 1) {
                    builder.append(split[i]).append(PLACEHOLDER);
                } else {
                    builder.append(split[i]);
                }
            }
        }
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(builder.toString());
    }

    private void handleCustomSqlEscape(String property, Map<Object, Object> parameterObject) {
        if (cascadeParameter(property)) {
            // 级联形式的参数,比如参数是对象中的某个字段的值:filter.name
            String[] keys = property.split(DOT_REGEX, 2);
            Object parameterBean = parameterObject.get(keys[0]);
            Object parameterValue = BeanUtil.getProperty(parameterBean, keys[1]);
            if (parameterValue instanceof String) {
                BeanUtil.setProperty(parameterBean, keys[1], addSalashes((CharSequence) parameterValue));
            }
        } else {
            // 普通参数名
            parameterObject.computeIfPresent(property, (key, value) -> {
                if (value instanceof String) {
                    return addSalashes((CharSequence) value);
                }
                return value;
            });
        }
    }

    private static String addSalashes(CharSequence content) {
        if (CharSequenceUtil.isEmpty(content)) {
            return CharSequenceUtil.str(content);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (CharSequenceUtil.contains(SQL_SPECIAL_CHARACTER, c)) {
                sb.append('\\');
            }
            sb.append(c);
        }

        return sb.toString();
    }

    private static String addSalashes(CharSequence content, char trimFix) {
        if (content.charAt(0) == trimFix) {
            content = content.subSequence(1, content.length());
        }
        if (content.charAt(content.length() - 1) == trimFix) {
            content = content.subSequence(0, content.length() - 1);
        }

        return addSalashes(content);
    }

    /**
     * 参数名是否是对象的嵌套表达式
     *
     * @param property 属性值
     * @return boolean
     */
    private boolean cascadeParameter(String property) {
        return CharSequenceUtil.contains(property, DOT);
    }


    /**
     * 处理通过条件构造器构造sql的转义
     *
     * @param property        参数名
     * @param parameterObject 此条sql的参数map
     */
    private void handleWrapperEscape(String property, Map<Object, Object> parameterObject) {
        String[] keys = property.split(DOT_REGEX);
        Object ew = parameterObject.get(keys[0]);
        if (ew instanceof AbstractWrapper) {
            Map<String, Object> paramNameValuePairs = ((AbstractWrapper<?, ?, ?>) ew).getParamNameValuePairs();
            Object paramValue = paramNameValuePairs.get(keys[2]);
            if (paramValue instanceof String) {
                paramNameValuePairs.put(keys[2], String.format("%%%s%%", addSalashes((String) paramValue, LIKE_WILDCARD_CHARACTER)));
            }
        }


    }

    /**
     * SQL是否需要转义
     *
     * @param sql SQL语句
     */
    private boolean needEscape(String sql) {
        return containLike(sql) && containPlaceholder(sql);
    }

    private boolean containLike(String sql) {
        return CharSequenceUtil.containsIgnoreCase(sql, LIKE_SQL);
    }

    private boolean containPlaceholder(String sql) {
        return CharSequenceUtil.contains(sql, PLACEHOLDER);
    }


    /**
     * 参数名是否是条件构造器生成
     *
     * @param property 参数属性
     * @return boolean
     */
    private boolean containWrapper(String property) {
        return CharSequenceUtil.contains(property, WRAPPER_PARAMETER_PROPERTY);
    }

    /**
     * 获取Mapper泛型的class对象
     *
     * @param ms MappedStatement
     * @return Mapper泛型的class对象，如果返回值为null，说明没有继承BaseMapper
     */
    private Class<?> getEntityClass(MappedStatement ms) {
        try {
            //获取Mapper的包名+类名
            String className = ms.getId().substring(0, ms.getId().lastIndexOf("."));
            //创建Class对象
            Class<?> clazz = Class.forName(className);
            //获取该对象继承的接口
            Type[] genericParameterTypes = clazz.getGenericInterfaces();
            for (Type type : genericParameterTypes) {
                //获取BaseMapper
                if (type.getTypeName().startsWith(BaseMapper.class.getTypeName())) {
                    //泛型
                    Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                    //因为BaseMapper只有有1个泛型，所以此处直接去第一个返回就行了
                    return Class.forName(types[0].getTypeName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
