package com.mcl.tools;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * 批处理
 * @auth caiguowei
 * @date 2020/5/9
 */
public class BatchUtils {

    public static void main(String[] args) {

        List<Integer> as = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            as.add(i);
        }
        sync(as,33,(a)->test(a));
    }
    // 测试
    private static void test(List<Integer> a){
        System.out.println(a);
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

}
