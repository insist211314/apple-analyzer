package com.appleframework.analyzer.lexicon;

import com.appleframework.analyzer.utils.StringUtils;

/**
 * Created by Administrator on 2016/11/21.
 */
public class RemoteLexiconFactory {

    public static RemoteLexicon getRemoteLexicon(String serviceName) {
        if(StringUtils.isNullOrEmpty(serviceName)){
            new RuntimeException("serviceName不能为null!");
        }
        Class clazz = null;
        try {
            clazz = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("RemoteLexicon服务未找到! serviceName=" + serviceName);
        }

        if(!RemoteLexicon.class.isAssignableFrom(clazz)){
            throw new RuntimeException(serviceName + "必须实现RemoteLexicon接口!");
        }

        try {
            return (RemoteLexicon)clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
