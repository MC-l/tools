package com.mcl.tools;


import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @auth caiguowei
 * @date 2020/4/23
 */
public class CollecUtils extends CollectionUtils {

    public static List returnEmptyListIfEmpty(List collection){
        return isEmpty(collection) ? Collections.EMPTY_LIST : collection;
    }

    public static boolean isNotEmpty(Collection collection){
        return !isEmpty(collection);
    }

    public static <T> boolean isEmpty(T[] arr){
        return arr == null || arr.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] arr){
        return !isEmpty(arr);
    }
}
