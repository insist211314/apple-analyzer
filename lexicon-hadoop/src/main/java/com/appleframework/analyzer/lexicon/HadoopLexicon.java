package com.appleframework.analyzer.lexicon;

import com.appleframework.analyzer.cfg.ConfigProperties;
import com.appleframework.analyzer.utils.HadoopUtils;
import com.appleframework.analyzer.utils.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2016/11/23.
 */
public class HadoopLexicon implements RemoteLexicon {

    private HadoopUtils hadoopClient;

    public HadoopLexicon(){
        String hdfs_url = ConfigProperties.getHdfsUrl();
        String hdfs_user = ConfigProperties.getHdfsUser();
        try {
            this.hadoopClient = new HadoopUtils(hdfs_url, hdfs_user);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getLexicons() {
        String remote_dic_path = ConfigProperties.getRemoteDicPath();
        String local_dic_path = ConfigProperties.getLocalDicPath();
        try {
            if(hadoopClient!=null)
                hadoopClient.downloadDir(remote_dic_path, local_dic_path);
        }catch(Exception e){
            System.err.println("HADOOP同步分词词库失败!");
            e.printStackTrace();
        }
        return listFiles(local_dic_path);
    }

    @Override
    public List<String> getStopWordLexicons() {

        String remote_stop_dic_path = ConfigProperties.getRemoteStopDicPath();
        String local_stop_dic_path = ConfigProperties.getLocalStopDicPath();

        try {
            if(hadoopClient!=null)
                hadoopClient.downloadDir(remote_stop_dic_path, local_stop_dic_path);
        }catch(Exception e){
            System.err.println("HADOOP同步停用词词库失败!");
            e.printStackTrace();
        }
        return listFiles(local_stop_dic_path);
    }

    private List<String> listFiles(String dirPath){
    	
        List<String> paths = new ArrayList<>();
        File dir = new File(dirPath);
        if(!dir.exists()){
        	dir.mkdirs();
        	return paths;
        }
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        for(File f : files){
            paths.add(f.getPath());
        }
        return paths;
    }

    private String getProperty(Properties props, String key){
        String val = props.getProperty(key);
        if(StringUtils.isNullOrEmpty(val))
        	throw new RuntimeException(key+"不能为空,请在IKAnalyzer.cfg.xml中配置!");
        return val;
    }
}
