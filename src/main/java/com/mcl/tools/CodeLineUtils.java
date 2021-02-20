package com.mcl.tools;

import org.springframework.util.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码行数统计
 * @Author MCl
 * @Date 2020-05-11 03:11
 */
public class CodeLineUtils {

    // 需要统计的项目路径
//    private static final String path = "C:\\Users\\j\\IdeaProjects\\kulark-meeting";
    private static final String path = "C:\\Users\\j\\IdeaProjects\\appserver";
    // 需要计算代码行数的文件类型
    private static final String subfix = ".java";

    public static void main(String[] args){

        List<String> fileNames = listFiles(new File(path));
        fileNames = fileNames.stream().filter(name->name.endsWith(subfix)).collect(Collectors.toList());
        Integer count = 0;
        for (String name : fileNames) {
            List<String> lines = FileUtil.readFileAsLineList(name);
            List<String> noBlanks = lines.stream().filter(line -> !StringUtils.isEmpty(line)).collect(Collectors.toList());
            count += noBlanks.size();
        }
        System.out.println(subfix+"文件个数:"+fileNames.size()+"\n代码总行数:"+count+"\n平均每个文件代码行数: "+(BigDecimal.valueOf(count).divide(BigDecimal.valueOf(fileNames.size()),2, RoundingMode.CEILING)));
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
