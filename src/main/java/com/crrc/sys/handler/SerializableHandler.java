package com.crrc.sys.handler;

import com.crrc.sys.annotation.TimeFormatMethod;
import com.crrc.sys.entity.BaseEntity;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * 实体序列化处理器
 * 功能：处理BaseEntity及其子类的JSON序列化，支持循环引用检测、时间格式处理、小数精度控制等
 */
@Slf4j
public class SerializableHandler {

    /**
     * 实体序列化器
     * 继承JsonSerializer<BaseEntity>，实现自定义序列化逻辑
     */
    public static class EntitySerializer extends JsonSerializer<BaseEntity> {

        /**
         * 主序列化方法
         * @param entity 待序列化的实体对象
         * @param gen JSON生成器
         * @param serializers 序列化提供者
         * @throws IOException 序列化过程中可能抛出的IO异常
         */
        @Override
        public void serialize(BaseEntity entity, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            // 使用Set记录已处理的对象，避免循环引用
            Set<Object> processedObjects = new HashSet<>();
            serializeEntity(entity, gen, serializers, processedObjects);
        }

        /**
         * 序列化实体对象核心方法
         * @param entity 待序列化的实体对象
         * @param gen JSON生成器
         * @param serializers 序列化提供者
         * @param processedObjects 已处理对象集合（用于循环引用检测）
         * @throws IOException 序列化过程中可能抛出的IO异常
         */
        private void serializeEntity(BaseEntity entity, JsonGenerator gen, SerializerProvider serializers,
                                     Set<Object> processedObjects) throws IOException {
            // 空值或循环引用检测
            if (entity == null || processedObjects.contains(entity)) {
                gen.writeNull();
                return;
            }
            processedObjects.add(entity);

            gen.writeStartObject();

            // 序列化BaseEntity基础字段
            serializeBaseEntityFields(entity, gen, serializers, processedObjects);

            // 序列化子类自定义字段
            serializeSubclassFields(entity, gen, serializers, processedObjects);

            // 序列化动态字段
            serializeDynamicFields(entity, gen, serializers, processedObjects);

            gen.writeEndObject();
        }

        /**
         * 序列化BaseEntity的基础字段
         */
        private void serializeBaseEntityFields(BaseEntity entity, JsonGenerator gen,
                                               SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            serializeField(gen, "createBy", entity.getCreateBy(), serializers, processedObjects);
            serializeField(gen, "createTime", entity.getCreateTime(), serializers, processedObjects);
            serializeField(gen, "updateBy", entity.getUpdateBy(), serializers, processedObjects);
            serializeField(gen, "updateTime", entity.getUpdateTime(), serializers, processedObjects);
            serializeField(gen, "createPerson", entity.getCreatePerson(), serializers, processedObjects);
            serializeField(gen, "updatePerson", entity.getUpdatePerson(), serializers, processedObjects);
        }

        /**
         * 序列化子类自定义字段
         */
        private void serializeSubclassFields(BaseEntity entity, JsonGenerator gen,
                                             SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            Class<?> clazz = entity.getClass();
            // 向上遍历继承链直到BaseEntity
            while (clazz != null && clazz != BaseEntity.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        // 特殊处理时间类型字段（排除createTime和updateTime）
                        if (isTimeType(field.getType()) && !"createTime".equals(field.getName())
                                && !"updateTime".equals(field.getName())) {
                            value = handleTimeField(field, value);
                        }
                        // 特殊处理小数类型字段
                        if (isDecimalType(field.getType())) {
                            value = handleDecimalField(field, value);
                        }
                        serializeField(gen, field.getName(), value, serializers, processedObjects);
                    } catch (IllegalAccessException e) {
                        log.error("字段访问失败: {}", field.getName(), e);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        /**
         * 序列化动态字段
         */
        private void serializeDynamicFields(BaseEntity entity, JsonGenerator gen,
                                            SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            for (Map.Entry<String, Object> entry : entity.getDynamicFields().entrySet()) {
                serializeField(gen, entry.getKey(), entry.getValue(), serializers, processedObjects);
            }
        }

        /**
         * 小数类型处理
         * @param field 字段对象
         * @param value 字段值
         * @return 处理后的值
         */
        private Object handleDecimalField(Field field, Object value) {
            if (value == null) {
                return null;
            }
            // 可扩展添加@DecimalFormat等注解处理
            // 当前版本保留原始值
            return value;
        }

        /**
         * 时间类型处理
         * @param field 字段对象（可能为null）
         * @param value 时间值
         * @return 格式化后的时间字符串
         */
        private Object handleTimeField(Field field, Object value) {
            if (value == null) {
                return "";
            }

            // 获取注解配置的时间格式
            String timeFormat = (field != null && field.getAnnotation(TimeFormatMethod.class) != null)
                    ? field.getAnnotation(TimeFormatMethod.class).format()
                    : null;

            try {
                // 处理不同类型的时间对象
                if (value instanceof TemporalAccessor) {
                    return formatTemporalAccessor((TemporalAccessor) value, timeFormat);
                } else if (value instanceof Date) {
                    return formatDate((Date) value, timeFormat);
                } else if (value instanceof Calendar) {
                    return formatCalendar((Calendar) value, timeFormat);
                }
            } catch (DateTimeException e) {
                log.warn("时间格式处理失败，字段: {}, 值: {}, 异常: {}",
                        field != null ? field.getName() : "nested", value, e.getMessage());
                return value; // 回退到原始值
            }

            return value;
        }

        /**
         * 格式化TemporalAccessor类型时间
         */
        private String formatTemporalAccessor(TemporalAccessor value, String timeFormat) {
            DateTimeFormatter formatter = (timeFormat != null && !timeFormat.isEmpty())
                    ? DateTimeFormatter.ofPattern(timeFormat)
                    : getDefaultFormatter(value.getClass());

            if (value instanceof LocalDateTime) {
                return formatter.format((LocalDateTime) value);
            } else if (value instanceof LocalDate) {
                return formatter.format((LocalDate) value);
            } else if (value instanceof LocalTime) {
                return formatter.format((LocalTime) value);
            } else if (value instanceof Instant) {
                return formatter.format(((Instant) value).atZone(ZoneId.systemDefault()));
            }
            return value.toString();
        }

        /**
         * 格式化Date类型时间
         */
        private String formatDate(Date value, String timeFormat) {
            DateTimeFormatter formatter = (timeFormat != null && !timeFormat.isEmpty())
                    ? DateTimeFormatter.ofPattern(timeFormat)
                    : DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return formatter.format(value.toInstant().atZone(ZoneId.systemDefault()));
        }

        /**
         * 格式化Calendar类型时间
         */
        private String formatCalendar(Calendar value, String timeFormat) {
            DateTimeFormatter formatter = (timeFormat != null && !timeFormat.isEmpty())
                    ? DateTimeFormatter.ofPattern(timeFormat)
                    : DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            return formatter.format(value.toInstant().atZone(ZoneId.systemDefault()));
        }

        /**
         * 序列化字段通用方法
         */
        private void serializeField(JsonGenerator gen, String fieldName, Object value,
                                    SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            if (value == null) {
                gen.writeNullField(fieldName);
            } else if (value instanceof Long) {
                gen.writeStringField(fieldName, value.toString());
            } else if (value instanceof Collection) {
                serializeCollection(gen, fieldName, (Collection<?>) value, serializers, processedObjects);
            } else if (value.getClass().isArray()) {
                serializeArrayField(gen, fieldName, value, serializers, processedObjects);
            } else if (value instanceof Map) {
                serializeMap(gen, fieldName, (Map<?, ?>) value, serializers, processedObjects);
            } else if (!(value instanceof BaseEntity) && !isSimpleType(value.getClass())) {
                serializeComplexObject(gen, value, serializers, processedObjects, fieldName);
            } else {
                serializeSimpleValue(gen, fieldName, value);
            }
        }

        /**
         * 序列化集合类型
         */
        private void serializeCollection(JsonGenerator gen, String fieldName, Collection<?> collection,
                                         SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            gen.writeArrayFieldStart(fieldName);
            for (Object item : collection) {
                serializeValue(gen, item, serializers, new HashSet<>(processedObjects), null);
            }
            gen.writeEndArray();
        }

        /**
         * 序列化数组字段
         */
        private void serializeArrayField(JsonGenerator gen, String fieldName, Object array,
                                         SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            gen.writeArrayFieldStart(fieldName);
            serializeArray(gen, array, serializers, new HashSet<>(processedObjects));
            gen.writeEndArray();
        }

        /**
         * 序列化Map类型
         */
        private void serializeMap(JsonGenerator gen, String fieldName, Map<?, ?> map,
                                  SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            gen.writeObjectFieldStart(fieldName);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                serializeField(gen, entry.getKey().toString(), entry.getValue(),
                        serializers, new HashSet<>(processedObjects));
            }
            gen.writeEndObject();
        }

        /**
         * 序列化复杂对象
         */
        private void serializeComplexObject(JsonGenerator gen, Object value,
                                            SerializerProvider serializers, Set<Object> processedObjects,
                                            String fieldName) throws IOException {
            serializeValue(gen, value, serializers, new HashSet<>(processedObjects), fieldName);
        }

        /**
         * 序列化简单值
         */
        private void serializeSimpleValue(JsonGenerator gen, String fieldName, Object value)
                throws IOException {
            if (isTimeType(value.getClass())) {
                Object formattedTime = handleTimeField(null, value);
                gen.writeObjectField(fieldName, formattedTime);
            } else if (isDecimalType(value.getClass())) {
                Object formattedDecimal = handleDecimalField(null, value);
                gen.writeObjectField(fieldName, formattedDecimal);
            } else {
                gen.writeObjectField(fieldName, value);
            }
        }

        /**
         * 通用值序列化方法
         * @param isObjectAlone 标记是否为独立对象（用于控制JSON结构）
         */
        private void serializeValue(JsonGenerator gen, Object value, SerializerProvider serializers,
                                    Set<Object> processedObjects, String isObjectAlone) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else if (value instanceof Long) {
                gen.writeString(value.toString());
            } else if (value instanceof Collection) {
                serializeCollectionValue(gen, (Collection<?>) value, serializers, processedObjects);
            } else if (value.getClass().isArray()) {
                serializeArrayValue(gen, value, serializers, processedObjects);
            } else if (value instanceof Map) {
                serializeMapValue(gen, (Map<?, ?>) value, serializers, processedObjects);
            } else if (!(value instanceof BaseEntity) && !isSimpleType(value.getClass())) {
                serializeCustomObject(gen, value, serializers, processedObjects, isObjectAlone);
            } else {
                serializeSimpleTypeValue(gen, value);
            }
        }

        /**
         * 序列化集合值
         */
        private void serializeCollectionValue(JsonGenerator gen, Collection<?> collection,
                                              SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            gen.writeStartArray();
            for (Object item : collection) {
                serializeValue(gen, item, serializers, new HashSet<>(processedObjects), null);
            }
            gen.writeEndArray();
        }

        /**
         * 序列化数组值
         */
        private void serializeArrayValue(JsonGenerator gen, Object array,
                                         SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            gen.writeStartArray();
            serializeArray(gen, array, serializers, new HashSet<>(processedObjects));
            gen.writeEndArray();
        }

        /**
         * 序列化Map值
         */
        private void serializeMapValue(JsonGenerator gen, Map<?, ?> map,
                                       SerializerProvider serializers, Set<Object> processedObjects)
                throws IOException {
            gen.writeStartObject();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                serializeField(gen, entry.getKey().toString(), entry.getValue(),
                        serializers, new HashSet<>(processedObjects));
            }
            gen.writeEndObject();
        }

        /**
         * 序列化自定义对象
         */
        private void serializeCustomObject(JsonGenerator gen, Object value,
                                           SerializerProvider serializers, Set<Object> processedObjects,
                                           String isObjectAlone) throws IOException {
            // 循环引用检测
            if (processedObjects.contains(value)) {
                gen.writeNull();
                return;
            }
            processedObjects.add(value);

            // 控制JSON结构
            if (StringUtils.isNotBlank(isObjectAlone)) {
                gen.writeObjectFieldStart(isObjectAlone);
            } else {
                gen.writeStartObject();
            }

            // 序列化对象字段
            for (Field field : value.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(value);
                    if (isTimeType(field.getType())) {
                        fieldValue = handleTimeField(field, fieldValue);
                    } else if (isDecimalType(field.getType())) {
                        fieldValue = handleDecimalField(field, fieldValue);
                    }
                    serializeField(gen, field.getName(), fieldValue, serializers, new HashSet<>(processedObjects));
                } catch (IllegalAccessException e) {
                    log.warn("字段访问失败: {}", field.getName(), e);
                }
            }
            gen.writeEndObject();
        }

        /**
         * 序列化简单类型值
         */
        private void serializeSimpleTypeValue(JsonGenerator gen, Object value) throws IOException {
            if (isTimeType(value.getClass())) {
                Object formattedTime = handleTimeField(null, value);
                gen.writeObject(formattedTime);
            } else if (isDecimalType(value.getClass())) {
                Object formattedDecimal = handleDecimalField(null, value);
                gen.writeObject(formattedDecimal);
            } else {
                gen.writeObject(value);
            }
        }

        /**
         * 判断是否为简单类型
         */
        private boolean isSimpleType(Class<?> clazz) {
            return clazz.isPrimitive() || clazz == String.class || clazz == Integer.class || clazz == Boolean.class
                    || clazz == Double.class || clazz == Float.class || clazz == Short.class || clazz == Byte.class
                    || clazz == Character.class || clazz == LocalDateTime.class || clazz == Date.class
                    || clazz == LocalTime.class || clazz == LocalDate.class || clazz == Instant.class
                    || clazz == Calendar.class || clazz == BigDecimal.class;
        }

        /**
         * 判断是否为时间类型
         */
        private boolean isTimeType(Class<?> clazz) {
            return clazz == LocalDateTime.class || clazz == Date.class
                    || clazz == LocalTime.class || clazz == LocalDate.class
                    || clazz == Instant.class || clazz == Calendar.class;
        }

        /**
         * 判断是否为小数类型
         */
        private boolean isDecimalType(Class<?> clazz) {
            return clazz == Double.class || clazz == Float.class || clazz == BigDecimal.class;
        }

        /**
         * 根据时间类型获取默认格式化器
         */
        private DateTimeFormatter getDefaultFormatter(Class<?> timeClass) {
            if (LocalDateTime.class.equals(timeClass)) {
                return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            } else if (LocalDate.class.equals(timeClass)) {
                return DateTimeFormatter.ISO_LOCAL_DATE;
            } else if (LocalTime.class.equals(timeClass)) {
                return DateTimeFormatter.ISO_LOCAL_TIME;
            } else if (Instant.class.equals(timeClass)) {
                return DateTimeFormatter.ISO_INSTANT;
            }
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }

        /**
         * 序列化数组内容
         */
        private void serializeArray(JsonGenerator gen, Object array, SerializerProvider serializers,
                                    Set<Object> processedObjects) throws IOException {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                Object item = Array.get(array, i);
                serializeValue(gen, item, serializers, new HashSet<>(processedObjects), null);
            }
        }
    }
}