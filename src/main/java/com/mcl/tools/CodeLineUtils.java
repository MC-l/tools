package com.mcl.tools;

import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码行数统计
 * @Author MCl
 * @Date 2020-05-11 03:11
 */
public class CodeLineUtils {

    public static void main(String[] args){
        String path = "/Users/mcl/IdeaProjects/hr/src/main/java/com/daxiongxx/hr";
        List<String> fileNames = listFiles(new File(path));
        Integer count = 0;
        for (String name : fileNames) {
            List<String> lines = FileUtil.readFileAsLineList(name);
            List<String> noBlanks = lines.stream().filter(line -> !StringUtils.isEmpty(line)).collect(Collectors.toList());
            count += noBlanks.size();
        }
        System.out.println(count);
    }

    public static List<String> listFiles(File dir){

        List<String> result = new ArrayList<>();
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                result.addAll(listFiles(file));
            }
        } else {
            String absolutePath = dir.getAbsolutePath();
            result.add(absolutePath);
        }

        return result;

    }
}
