package com.mcl.tools;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * 批处理
 * @auth caiguowei
 * @date 2020/5/9
 */
public class BatchUtils {

    public static void main(String[] args) throws Exception {

        List<Integer> as = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            as.add(i);
        }
        //sync(as,33,(a)->test(a));
        Future<List<Integer>> future = asyncReturn(as, 33, (a) -> testReturn(a));
        System.out.println(future.get());

    }
    // 测试
    private static void test(List<Integer> a){
        System.out.println(a);
    }
    // 测试
    private static List<Integer> testReturn(List<Integer> a){
        List<Integer> result = new ArrayList<>();
        a.forEach(n->{
            result.add(n+100000);
        });
        return result;
    }

    /**
     * 同步批处理
     * @param data  要处理的数据集合
     * @param partSize  每一批次的元素个数
     * @param func  业务处理
     * @param <T>
     */
    public static <T> void sync(List<T> data, int partSize, Consumer<List<T> > func){
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        List<List<T>> partition = Lists.partition(data, partSize);
        for (List<T> part : partition) {
            func.accept(part);
        }
    }
    /**
     * 同步批处理
     * @param data  要处理的数据集合
     * @param partSize  每一批次的元素个数
     * @param func  业务处理
     * @param <T>
     */
    public static <T,R> List<R> syncReturn(List<T> data, int partSize, Function<List<T>,List<R>> func){
        if (CollectionUtils.isEmpty(data)) {
            return Collections.EMPTY_LIST;
        }
        List<List<T>> partition = Lists.partition(data, partSize);
        List<R> result = Lists.newArrayList();
        for (List<T> part : partition) {
            List<R> apply = func.apply(part);
            if (!CollectionUtils.isEmpty(apply)){
                result.addAll(apply);
            }
        }
        return result;
    }

    /**
     * 异步（后台线程）批处理
     * @param data  要处理的数据集合
     * @param partSize  每一批次的元素个数
     * @param func  业务处理
     * @param <T>
     */
    public static <T> void async(List<T> data, int partSize, Consumer<List<T> > func){
        ThreadPooler.executeAsyncRunnerOnDaemon(()->{
            sync(data,partSize,func);
        });
    }

    /**
     * 异步（后台线程）批处理
     * @param data  要处理的数据集合
     * @param partSize  每一批次的元素个数
     * @param func  业务处理
     * @param <T>
     */
    public static <T,R> Future<List<R>> asyncReturn(List<T> data, int partSize, Function<List<T>,List<R>> func){
        try {
            Future<List<R>> future = ThreadPooler.submitAsyncCallerOnDaemon(() -> syncReturn(data, partSize, func));
            return future;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
