package com.appleframework.analyzer.cfg;

/**
 * Created by Administrator on 2016/11/23.
 */
public class ConfigProperties {

    private static Long resetDicInterval;

    private static String hdfsUrl;
    private static String hdfsUser;
    private static String remoteDicPath;
    private static  String localDicPath;
    private static String remoteStopDicPath;
    private static  String localStopDicPath;

    //配置属性——扩展字典
    private static String extDict;
    //配置属性——扩展字典
    private static String remoteDictClass;
    //配置属性——扩展停止词典
    private static String extStopWords;


    public static String getExtStopWords() {
        return extStopWords;
    }

    public static Long getResetDicInterval() {
        return resetDicInterval;
    }

    public static String getRemoteStopDicPath() {
        return remoteStopDicPath;
    }

    public void setRemoteStopDicPath(String remoteStopDicPath) {
        ConfigProperties.remoteStopDicPath = remoteStopDicPath;
    }

    public static String getLocalStopDicPath() {
        return localStopDicPath;
    }

    public void setLocalStopDicPath(String localStopDicPath) {
        ConfigProperties.localStopDicPath = localStopDicPath;
    }

    public void setResetDicInterval(Long resetDicInterval) {
        ConfigProperties.resetDicInterval = resetDicInterval;
    }

    public void setExtStopWords(String extStopWords) {
        ConfigProperties.extStopWords = extStopWords;
    }

    public static String getRemoteDictClass() {
        return remoteDictClass;
    }

    public void setRemoteDictClass(String remoteDictClass) {
        ConfigProperties.remoteDictClass = remoteDictClass;
    }

    public static String getExtDict() {
        return extDict;
    }

    public void setExtDict(String extDict) {
        ConfigProperties.extDict = extDict;
    }

    public static String getLocalDicPath() {
        return localDicPath;
    }

    public void setLocalDicPath(String localDicPath) {
        ConfigProperties.localDicPath = localDicPath;
    }

    public static String getRemoteDicPath() {
        return remoteDicPath;
    }

    public void setRemoteDicPath(String remoteDicPath) {
        ConfigProperties.remoteDicPath = remoteDicPath;
    }

    public static String getHdfsUser() {
        return hdfsUser;
    }

    public void setHdfsUser(String hdfsUser) {
        ConfigProperties.hdfsUser = hdfsUser;
    }

    public static String getHdfsUrl() {
        return hdfsUrl;
    }

    public void setHdfsUrl(String hdfsUrl) {
        ConfigProperties.hdfsUrl = hdfsUrl;
    }
}
