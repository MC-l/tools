package com.mcl.tools;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author MCl
 * @Date 2018-10-18 21:22
 */
public class DataMaker {

    private static enum DataType{
        BOOLEAN,
        BYTE,
        CHAR,
        DATE,
        DOUBLE,
        FLOAT,
        INT,
        LONG,
        SHORT,
        STRING;

        private static final Map<Class, DataType> dataTypeMap = new HashMap<>();

        static {
            dataTypeMap.put(Boolean.class, DataType.BOOLEAN);
            dataTypeMap.put(Byte.class, DataType.BYTE);
            dataTypeMap.put(Character.class, DataType.CHAR);
            dataTypeMap.put(Short.class, DataType.SHORT);
            dataTypeMap.put(Integer.class, DataType.INT);
            dataTypeMap.put(Long.class, DataType.LONG);
            dataTypeMap.put(Float.class, DataType.FLOAT);
            dataTypeMap.put(Double.class, DataType.DOUBLE);
            dataTypeMap.put(String.class, DataType.STRING);
            dataTypeMap.put(Date.class, DataType.DATE);
        }

        public static DataType getDataType(Field field) {
            if (field == null) {
                throw new RuntimeException("Unknow the type of this field.");
            }
            return dataTypeMap.get(field.getGenericType());

        }

        public static DataType getDataType(Class<?> clazz, String fieldName) {
            try{
                return getDataType(clazz.getDeclaredField(fieldName));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
    }

    public static <T> T make(Class<T> clazz){
        Field[] fields = clazz.getDeclaredFields();
        T t = null;
        try {
            t = clazz.getConstructor().newInstance();
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(t,"cai");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        return t;
    }
}
