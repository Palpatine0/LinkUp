package com.enchanted.util;

import java.math.BigDecimal;

public class ConversionUtils {

    public static Object convertValueToRequiredType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            if (value instanceof Integer) {
                return ((Integer) value) != 0;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
        }

        if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            } else if (value instanceof Boolean) {
                return ((Boolean) value) ? 1 : 0;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }

        if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            if (value instanceof String) {
                return Long.parseLong((String) value);
            } else if (value instanceof Number) {
                return ((Number) value).longValue();
            }
        }

        if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            if (value instanceof String) {
                return Double.parseDouble((String) value);
            } else if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }


        if (targetType.equals(BigDecimal.class)) {
            if (value instanceof String) {
                return new BigDecimal((String) value);
            } else if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            }
        }

        if (targetType.equals(String.class)) {
            return String.valueOf(value);
        }

        // Add more cases as needed
        return value;
    }
}
