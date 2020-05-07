package com.mcl.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
     * @param transfer  属性名映射
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> List<R> copyList(List<T> from, Class<? extends R> targetClazz, Transfer transfer){
        return (List<R>) copyCollection(from, targetClazz,transfer);
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
        return copyCollection(from,targetClazz,null);
    }

    /**
     * 集合元素属性拷贝
     * @param from
     * @param targetClazz
     * @param transfer  属性名映射
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T,R> Collection<R> copyCollection(Collection<T> from, Class<? extends R> targetClazz, Transfer transfer){
        if (from == null || from.size() == 0){
            return Collections.EMPTY_LIST;
        }
        List<Field> notFinals = getNotFinalsAndSetAccessible(targetClazz);
        Class<?> elementClass = from.iterator().next().getClass();
        List<Field> fromFields = getNotFinalsAndSetAccessible(elementClass);

        Map<String,Field> fieldNameMap = toMap(fromFields.toArray(new Field[fromFields.size()]),transfer);

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
        return copy(fromObj,targetClazz,null);
    }

    /**
     * 对象的属性拷贝
     * <pre>
     * 不匹配（名称不相同）的属性则会被忽略
     * 如果名称相同，但类型不同，会抛 IllegalArgumentException 异常
     * </pre>
     * @param fromObj
     * @param targetClazz
     * @param transfer  属性名映射
     * @param <T>
     * @param <R>
     * @throws IllegalArgumentException
     * @return 返回null的情况：fromObj 为空 或者 targetClazz 不存在无参构造方法
     */
    public static <T,R> R copy(T fromObj, Class<? extends R> targetClazz, Transfer transfer){
        if (fromObj == null){
            return null;
        }

        List<Field> notFinalsOnTarget = getNotFinalsAndSetAccessible(targetClazz);
        Class<?> fromClazz = fromObj.getClass();
        List<Field> fromFields = getNotFinalsAndSetAccessible(fromClazz);
        Map<String,Field> fromFieldNameMap = toMap(fromFields.toArray(new Field[fromFields.size()]),transfer);

        if (notFinalsOnTarget.isEmpty() || fromFieldNameMap.isEmpty()){
            try {
                return targetClazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        try{
            R b = targetClazz.newInstance();
            for (int j = 0; j < notFinalsOnTarget.size(); j++) {
                Field targetField = notFinalsOnTarget.get(j);
                Field fromField = fromFieldNameMap.get(targetField.getName());
                if (fromField != null){
                    // 如果是集合属性
                    if (isCollection(fromField)){
                        // 如果是集合类型，就转换成集合
                        Collection list = (Collection)fromField.get(fromObj);
                        // 获取集合元素的真实类型
                        Class targetFieldClazz = getCollectionElementClass(targetField);

                        Collection targetVal = copyCollection(list, targetFieldClazz);

                        targetField.set(b,targetVal);
                    } else {
                        Object obj = fromField.get(fromObj);
                        if (obj == null){
                            continue;
                        }
                        if (!targetClazz.equals(fromField.getAnnotatedType())){
                            if (DateUtil.isDateType(obj)){
                                Date date = DateUtil.toDate(obj);
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
     * 获取类的集合属性的元素类型
     * 如：class User {
     *     List<Eye> eyes;
     * }
     * 方法调用： 传递 eyes 这个 Field ， 返回 Eye.class
     * @param field
     * @return
     * @throws ClassCastException   如果 该 field （集合）的泛型没指定，则抛异常
     */
    private static Class getCollectionElementClass(Field field){
        Type genericType = field.getGenericType();
        ParameterizedType type = (ParameterizedType) genericType;
        Type[] actualTypeArguments = type.getActualTypeArguments();
        Class targetFieldClazz = (Class)actualTypeArguments[0];
        return targetFieldClazz;
    }

    /**
     * 判断属性的真实类型是否是集合
     * @param field
     * @return
     */
    private static boolean isCollection(Field field){
        return Collection.class.isAssignableFrom(field.getType());
    }

    /**
     * 转成map,key=field.name, val = field
     * @param fields
     * @return
     */
    private static Map<String,Field> toMap(Field[] fields, Transfer transfer){
        if (fields == null || fields.length == 0){
            return Collections.EMPTY_MAP;
        }
        Map<String,Field> map = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            if (transfer != null){
                String to = transfer.getTo(f.getName());
                if (to != null){
                    map.put(to, f);
                    continue;
                }
            }
            map.put(f.getName(), f);
        }
        return map;
    }

    /**
     * 获取类（包含父类）中所有非 final 字段，并设置为可访问
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

        // 递归获取父类属性
        Class<?> superclass = clazz.getSuperclass();
        if (!superclass.equals(Object.class)) {
            List<Field> superFields = getNotFinalsAndSetAccessible(superclass);
            notFinals.addAll(superFields);
        }

        return notFinals;
    }

    /**
     * 属性名称转换映射
     */
    public static class Transfer{
        private List<String> froms;
        private List<String> tos;

        public static final Transfer INSTANCE = new Transfer();

        public Transfer() {
            froms = new ArrayList<>();
            tos = new ArrayList<>();
        }

        public Transfer addFromTo(String from, String to){
            Objects.requireNonNull(from,"参数 from 不能为空");
            Objects.requireNonNull(to,"参数 to 不能为空");
            froms.add(from);
            tos.add(to);
            return this;
        }

        public String getTo(String from){
            int i = froms.indexOf(from);
            if (i >= 0) {
                return tos.get(i);
            }
            return null;
        }

        public void setFroms(List<String> froms) {
            this.froms = froms;
        }

        public void setTos(List<String> tos) {
            this.tos = tos;
        }
    }

    /**
     * 是否是true
     * @param flag
     * @return
     */
    public static boolean isFalse(Boolean flag){
        return flag == null || !flag;
    }

}
