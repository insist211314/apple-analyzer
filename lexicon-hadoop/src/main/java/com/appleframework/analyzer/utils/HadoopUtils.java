package com.appleframework.analyzer.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */
public class HadoopUtils {

    private FileSystem fs=null;

    public HadoopUtils(String hdfsUrl, String user){
        try {
            Configuration conf = new Configuration();
            fs = FileSystem.get(new URI(hdfsUrl), conf, user);
        }catch (Exception e){
            throw new RuntimeException("初始化HadoopUtils失败!", e);
        }
    }

    /**
     * 获取hdfs目录下的所有文件列表
     * @param path hdfs目录
     * @return     文件列表
     * @throws IOException
     */
    public List<FileStatus> listFileAll(Path... path) throws IOException {
        FileStatus[] listStatus = fs.listStatus(path);
        List<FileStatus> fileStatusList = new ArrayList<>();
        List<Path> dirs = new ArrayList<>();
        for(FileStatus status:listStatus){
            if(status.isDirectory()){
                dirs.add(status.getPath());
                continue;
            }
            fileStatusList.add(status);
        }
        if(CollectionUtils.isNotEmpty(dirs))
            fileStatusList.addAll(listFileAll(dirs.toArray(new Path[dirs.size()])));
        return fileStatusList;
    }

    /**
     * 下载hdfs的目录，如果目录中的文件在本地磁盘已存在，则不进行下载
     * @param hdfsPath   hdfs目录路径
     * @param localPath  本地目录路径
     * @return 本地文件列表
     */
    public void downloadDir(String hdfsPath, String localPath) {
        try {
            List<FileStatus> fileStatusList = listFileAll(new Path(hdfsPath));
            for (FileStatus status : fileStatusList) {
                Path path = status.getPath();
                String downFilePath = getlocalDir(path.getParent().toString(), hdfsPath, localPath) + path.getName();
                File f = new File(downFilePath);
                if (f.exists() && f.length()==status.getLen()) {
                    continue;
                }
                fs.copyToLocalFile(false, path, new Path(downFilePath), true);
            }
        }catch(Exception e){
            throw new RuntimeException("下载目录失败!", e);
        }
    }

    private String getlocalDir(String fileDirPath, String hdfsPath, String localPath){
        if(fileDirPath.indexOf(hdfsPath)+hdfsPath.length() == fileDirPath.length()){
            return localPath;
        }
        String dir = localPath + fileDirPath.substring(fileDirPath.indexOf(hdfsPath) + hdfsPath.length());
        File f = new File(dir);
        if(!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

}
