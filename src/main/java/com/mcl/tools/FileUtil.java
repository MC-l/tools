package com.mcl.tools;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @Author MCl
 * @Date 2018-11-17 03:11
 */
public final class FileUtil {

    private static List<String> videoTypes = Arrays.asList("avi","wmv","mpeg","mp4","mov","mkv","flv","f4v","m4v","rmvb","rm","3gp","dat","ts","mts","vob");
    private static List<String> imgTypes = Arrays.asList("bmp","jpg","jpeg","png","tif","gif","pcx","tga","exif","fpx","svg","psd","cdr","pcd","dxf","ufo","eps","ai","raw","WMF","webp");

    public static boolean isVideo(MultipartFile file){
        String extention = getExtention(file.getOriginalFilename()).toLowerCase();
        return videoTypes.contains(extention);
    }

    public static boolean isImg(MultipartFile file){
        String extention = getExtention(file.getOriginalFilename()).toLowerCase();
        return imgTypes.contains(extention);
    }

    /**
     * 创建路径
     * @param partPaths e.g. genPath("a\b\","c/d","e") return "a/b/c/d/e/"
     * @return
     */
    public static String genPath(String ...partPaths){
        StringBuilder sb = new StringBuilder();
        for (String partPath : partPaths) {
            partPath = partPath.replace("\\",File.separator);
            sb.append(partPath);
            if (!partPath.endsWith(File.separator)){
                sb.append(File.separator);
            }
        }
        return sb.toString().replaceAll("/{2,}","/");
    }

    public static String genPathNoEndSeparator(String ...partPaths){
        String path = genPath(partPaths);
        return path.substring(0,path.length()-1);
    }

    /**
     * 创建文件夹(如果不存在)
     * @param folderAbsPath
     */
    public static void createFolderIfNotExisted(String folderAbsPath){
        File folder = new File(folderAbsPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * 获取文件主名
     * @param fileName
     * @return
     */
    public static String getBaseName(String fileName){
        if (StringUtils.isEmpty(fileName)) {
            new RuntimeException("文件名不能为空");
        }

        int idx = fileName.lastIndexOf(".");
        if (idx == -1) {
            return fileName;
        }
        return fileName.substring(0,idx);
    }

    /**
     * 获取文件扩展名
     * @param fileName
     * @return
     */
    public static String getExtention(String fileName){
        boolean isOk = !StringUtils.isEmpty(fileName) && fileName.lastIndexOf(".") > -1;
        return isOk ? fileName.substring(fileName.lastIndexOf(".")+1):"";
    }

    /**
     * 获取文件新名字
     * @param fileOldName
     * @return
     */
    public static String getNewName(String fileOldName){
        String ext = getExtention(fileOldName);
        String newBaseName = UUIDUtil.getUUId();
        return StringUtils.isEmpty(ext) ? newBaseName + ext : newBaseName + "." + ext;
    }



    public static List<String> readFileAsLineList(String filePath){

        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(new File(filePath)));
            List<String> result = new ArrayList<>();
            String line = null;
            while ((line = br.readLine()) != null){
                result.add(line);
            }
            return result;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public static String readFileAsOneLine(String filePath){
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(new File(filePath)));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            return sb.toString();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public static String replaceText(String fileText, Map<String,String> replaceMap){
        Iterator<Map.Entry<String, String>> iterator = replaceMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            fileText = fileText.replace(entry.getKey(),entry.getValue());
        }
        return fileText;
    }

    public static void writeLinesAsFile(String serviceFileText, String filePath) {
        BufferedWriter bw = null;
        try{
            File file = new File(filePath);

            if (file.exists()) {
                FileUtils.forceDelete(file);
            }

            if(!file.getParentFile().exists()){
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



            bw = new BufferedWriter(new FileWriter(file));
            bw.write(serviceFileText);
            bw.flush();
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            if (bw != null){
                try {
                    bw.close();
                } catch (IOException e) {

                }
            }
        }

    }
}
