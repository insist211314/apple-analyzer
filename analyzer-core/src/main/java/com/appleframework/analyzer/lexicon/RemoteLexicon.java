package com.appleframework.analyzer.lexicon;

import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2016/11/21.
 */
public interface RemoteLexicon {

    /**
     * 获取分词库绝对路径列表
     * @return
     */
    public List<String> getLexicons();

    /**
     * 获取停用词库绝对路径列表
     * @return
     */
    public List<String> getStopWordLexicons();

}
