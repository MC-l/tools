package com.mcl.tools.bailing;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @auth caiguowei
 * @date 2020/4/18
 */
public class ObjUtils {


    /**
     * 集合元素属性拷贝
     * @param from
     * @param targetClazz
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> copyList(List<T> from, Class<? extends R> targetClazz){
        return (List<R>) copyCollection(from, targetClazz);
    }



    /**
     * 集合元素属性拷贝
     * @param from
     * @param targetClazz
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> Collection<R> copyCollection(Collection<T> from, Class<? extends R> targetClazz){
        if (from == null || from.size() == 0){
            return Collections.EMPTY_LIST;
        }
        List<Field> notFinals = getNotFinalsAndSetAccessible(targetClazz);
        Field[] fromFields = from.iterator().next().getClass().getDeclaredFields();
        Map<String,Field> fieldNameMap = toMap(fromFields);

        if (notFinals.isEmpty() || fieldNameMap.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        List<R> bs = new ArrayList<>(from.size());
        try{
            Iterator<T> fItor = from.iterator();

            while (fItor.hasNext()) {
                T a = fItor.next();
                R b = targetClazz.newInstance();
                for (int j = 0; j < notFinals.size(); j++) {

                    Field targetField = notFinals.get(j);
                    Field fromField = fieldNameMap.get(targetField.getName());
                    if (fromField != null){
                        targetField.set(b, fromField.get(a));
                    }
                }
                bs.add(b);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return bs;
    }

    /**
     * 对象的属性拷贝
     * <pre>
     * 不匹配（名称不相同）的属性则会被忽略
     * 如果名称相同，但类型不同，会抛 IllegalArgumentException 异常
     * </pre>
     * @param fromObj
     * @param targetClazz
     * @param <T>
     * @param <R>
     * @throws IllegalArgumentException
     * @return 返回null的情况：fromObj 为空 或者 targetClazz 不存在无参构造方法
     */
    public static <T,R> R copy(T fromObj, Class<? extends R> targetClazz){

        if (fromObj == null){
            return null;
        }

        List<Field> notFinals = getNotFinalsAndSetAccessible(targetClazz);
        Field[] fromFields = fromObj.getClass().getDeclaredFields();
        Map<String,Field> fromFieldNameMap = toMap(fromFields);

        if (notFinals.isEmpty() || fromFieldNameMap.isEmpty()){
            try {
                return targetClazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        try{
            R b = targetClazz.newInstance();
            for (int j = 0; j < notFinals.size(); j++) {
                Field targetField = notFinals.get(j);
                Field fromField = fromFieldNameMap.get(targetField.getName());
                if (fromField != null){
                    // 如果是集合属性
                    if (Collection.class.isAssignableFrom(fromField.getClass())){
                        Collection list = (Collection)fromField.get(fromObj);
                        Collection targetVal = copyCollection(list, targetField.getClass());
                        targetField.set(b,targetVal);
                    } else {
                        Object obj = fromField.get(fromObj);
                        if (obj == null){
                            continue;
                        }
                        if (!targetClazz.equals(fromField.getAnnotatedType())){
                            if (DateUtils.isDateType(obj)){
                                Date date = DateUtils.toDate(obj);
                                targetField.set(b, date);
                                continue;
                            }
                        }
                        targetField.set(b, obj);
                    }
                }
            }
            return b;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转成map,key=field.name, val = field
     * @param fields
     * @return
     */
    private static Map<String,Field> toMap(Field[] fields){
        if (fields == null || fields.length == 0){
            return Collections.EMPTY_MAP;
        }
        Map<String,Field> map = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            map.put(f.getName(), f);
        }
        return map;
    }

    /**
     * 获取类中所有非 final 字段，并设置为可访问
     * @param clazz
     * @return
     */
    private static List<Field> getNotFinalsAndSetAccessible(Class<?> clazz){
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0){
            return Collections.EMPTY_LIST;
        }
        List<Field> notFinals = Stream.of(fields).filter(f -> !Modifier.isFinal(f.getModifiers())).collect(Collectors.toList());
        notFinals.stream().forEach(f->{
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
        });
        return notFinals;
    }

}
