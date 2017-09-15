package com.appleframework.analyzer.lexicon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;

public class HadoopTest1 {

    FileSystem fs=null;

    //@Before 是junit 测试框架
    @Before
    public void init() throws Exception{

        //读取classpath下的core/hdfs-site.xml 配置文件，并解析其内容，封装到conf对象中
        //如果没有会读取hadoop里的配置文件
        Configuration conf=new Configuration();
//        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

        //也可以在代码中对conf中的配置信息进行手动设置，会覆盖掉配置文件中的读取的值
        //conf.set("fs.defaultFS", "hdfs://hello110:9000/");

        //根据配置信息，去获取一个具体文件系统的客户端操作实例对象
        fs=FileSystem.get(new URI("hdfs://192.168.1.229/"), conf, "hdfs");

    }



    @Test
    public void listFiles() throws Exception{

        //RemoteIterator 远程迭代器
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/dic"), true);

        while(listFiles.hasNext()){
            LocatedFileStatus file = listFiles.next();
            Path path = file.getPath();
            //String fileName=path.getName();
            System.out.println(path.toString());
        }

        System.out.println("-----------------华丽的分割线----------------");

//        FileStatus[] listStatus = fs.listStatus(new Path("/"));
//
//        for(FileStatus status:listStatus){
//            String name=status.getPath().getName();
//            System.out.println(name+(status.isDirectory()?" is Dir":" is File"));
//        }
    }
    @Test
    public void listFiles2() throws IOException {
//        File f = new File("E:\\dic\\世界足球联赛所有球队.txt");
//        System.out.println(f);

        List<FileStatus> files = listFileAll(new Path("/solr/stopdic"));
        for(FileStatus status : files){
            System.out.println(status.getPath());
        }
    }
    //12101
//134217728
    public List<FileStatus> listFileAll(Path... path) throws IOException {

        FileStatus[] listStatus = fs.listStatus(path);
        List<FileStatus> files = new ArrayList<>();
        List<Path> dirs = new ArrayList<>();
        for(FileStatus status:listStatus){
            if(status.isDirectory())
                dirs.add(status.getPath());
            files.add(status);
        }
        if(CollectionUtils.isNotEmpty(dirs))
            files.addAll(listFileAll(dirs.toArray(new Path[dirs.size()])));
        return files;
    }





    private String dic_dir = "/solr/dic";
    private String local_dic = "E:/dic";

    @Test
    public void download2() throws IOException {

        List<String> filePaths = new ArrayList<>();
        //RemoteIterator 远程迭代器
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(dic_dir), true);
        while(listFiles.hasNext()){
            LocatedFileStatus file = listFiles.next();
            Path path = file.getPath();
            String downFilePath = downloadDir(path.getParent().toString()) + "/" + path.getName();
            if(new File(downFilePath).exists()){
                continue;
            }
            fs.copyToLocalFile(false, path, new Path(downFilePath), true);
            filePaths.add(downFilePath);
        }
    }

    public String downloadDir(String filePath){

        if(filePath.indexOf(dic_dir)+dic_dir.length() == filePath.length()){
            return local_dic;
        }
        String dir = local_dic + filePath.substring(filePath.indexOf(dic_dir) + dic_dir.length());
        File f = new File(dir);
        if(!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }


    //比较低层的写法
    @Test
    public void upload() throws Exception{

        Path path = new Path("hdfs://hello110:9000/testdata/testFs.txt");
        FSDataOutputStream os = fs.create(path);

        FileInputStream is = new FileInputStream("d:/testFS.txt");

        IOUtils.copy(is, os);
    }

    //封装好的方法
    @Test
    public void upload2() throws Exception{
        //hdfs dfs -copyFromLocal 从本地拷贝命令
        fs.copyFromLocalFile(new Path("D:/ext.dic"), new Path("/solr/dic/ext.dic"));
    }

    @Test
    public void upload3()throws Exception{
        //hdfs dfs -copyFromLocal 从本地拷贝命令
        fs.copyFromLocalFile(new Path("D:/dic"), new Path("/solr/"));
    }

    @Test
    public void download() throws Exception{

        //hdfs dfs -copyToLocal 拷贝到本地命令


    }

    @Test
    public void mkdir() throws Exception{

        fs.mkdirs(new Path("/solr/dic"));

    }

    @Test
    public void rm() throws Exception{
        //true：如果是目录也会删除。false，遇到目录则报错
        fs.delete(new Path("/solr/dic"), true);
    }
}
