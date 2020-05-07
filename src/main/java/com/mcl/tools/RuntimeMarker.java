package com.mcl.tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 系统运行时环境标记
 * @auth caiguowei
 * @date 2020/4/21
 */
public class RuntimeMarker {

    // 时间标记集合
    private static ThreadLocal<LinkedList<Long>> timeset = new ThreadLocal<>();
    // 内存标记集合
    private static ThreadLocal<LinkedList<Long>> memoryset = new ThreadLocal<>();

    /**
     * 调用其他方法前，序调此方法初始化
     */
    public static void inti() {
        timeset.set(new LinkedList<>());
        memoryset.set(new LinkedList<>());
    }

    /**
     * 标记当前时刻（毫秒）
     */
    public static void markTime(){
        List<Long> longs = timeset.get();
        longs.add(System.currentTimeMillis());
    }

    /**
     * 获取所有标记点之间的时间间隔
     * @return
     */
    public static List<Long> getTimeDelta(){
        List<Long> longs = timeset.get();
        List<Long> deltas = new ArrayList<>();
        for (int i = 1; i < longs.size(); i++) {
            deltas.add(longs.get(i) - longs.get(i-1));
        }
        return deltas;
    }

    /**
     * 获取总耗时
     * @return
     */
    public static Long getTotalTime(){
        List<Long> longs = timeset.get();
        Long end = longs.get(longs.size() - 1);
        Long start = longs.get(0);
        return end - start;
    }

    /**
     * 打印耗时报告
     */
    public static void printTimeReport(){
        System.out.println("耗时（"+RuntimeMarker.getTotalTime()+" ms）:"+RuntimeMarker.getTimeDelta());
    }

}
