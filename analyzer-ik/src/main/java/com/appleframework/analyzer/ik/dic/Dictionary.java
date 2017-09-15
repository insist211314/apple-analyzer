/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 * 
 */
package com.appleframework.analyzer.ik.dic;

import com.appleframework.analyzer.cfg.ConfigProperties;
import com.appleframework.analyzer.ik.cfg.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词典管理类,单子模式
 */
public class Dictionary extends Thread {

	/*
	 * 词典单子实例
	 */
	private static Dictionary singleton;
	
	/*
	 * 主词典对象
	 */
	private DictSegment _MainDict;
	
	/*
	 * 停止词词典 
	 */
	private DictSegment _StopWordDict;
	/*
	 * 量词词典
	 */
	private DictSegment _QuantifierDict;

	private Map<String, Long> filesLeng = new HashMap<>();
	
	/**
	 * 配置对象
	 */
	private Configuration cfg;
	
	private Dictionary(Configuration cfg){
		this.cfg = cfg;
		//建立一个主词典实例
		_MainDict = new DictSegment((char)0);
		//建立一个主词典实例
		_StopWordDict = new DictSegment((char)0);

		this.loadMainDict(false);
		this.loadStopWordDict(false);
		this.loadQuantifierDict();
	}

	@Override
	public void run() {
		do{
			try {
				Thread.sleep(ConfigProperties.getResetDicInterval());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.loadMainDict(true);
			this.loadStopWordDict(true);
		}while (true);
	}
	
	/**
	 * 词典初始化
	 * 由于IK Analyzer的词典采用Dictionary类的静态方法进行词典初始化
	 * 只有当Dictionary类被实际调用时，才会开始载入词典，
	 * 这将延长首次分词操作的时间
	 * 该方法提供了一个在应用加载阶段就初始化字典的手段
	 * @return Dictionary
	 */
	public static Dictionary initial(Configuration cfg){
		if(singleton == null){
			synchronized(Dictionary.class){
				if(singleton == null){
					singleton = new Dictionary(cfg);
					singleton.start();
					return singleton;
				}
			}
		}
		return singleton;
	}
	
	/**
	 * 获取词典单子实例
	 * @return Dictionary 单例对象
	 */
	public static Dictionary getSingleton(){
		if(singleton == null){
			throw new IllegalStateException("词典尚未初始化，请先调用initial方法");
		}
		return singleton;
	}
	
	/**
	 * 批量加载新词条
	 * @param words Collection<String>词条列表
	 */
	public void addWords(Collection<String> words){
		if(words != null){
			for(String word : words){
				if (word != null) {
					//批量加载词条到主内存词典中
					singleton._MainDict.fillSegment(word.trim().toLowerCase().toCharArray());
				}
			}
		}
	}
	
	/**
	 * 批量移除（屏蔽）词条
	 * @param words
	 */
	public void disableWords(Collection<String> words){
		if(words != null){
			for(String word : words){
				if (word != null) {
					//批量屏蔽词条
					singleton._MainDict.disableSegment(word.trim().toLowerCase().toCharArray());
				}
			}
		}
	}
	
	/**
	 * 检索匹配主词典
	 * @param charArray
	 * @return Hit 匹配结果描述
	 */
	public Hit matchInMainDict(char[] charArray){
		return singleton._MainDict.match(charArray);
	}
	
	/**
	 * 检索匹配主词典
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return Hit 匹配结果描述
	 */
	public Hit matchInMainDict(char[] charArray , int begin, int length){
		return singleton._MainDict.match(charArray, begin, length);
	}
	
	/**
	 * 检索匹配量词词典
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return Hit 匹配结果描述
	 */
	public Hit matchInQuantifierDict(char[] charArray , int begin, int length){
		return singleton._QuantifierDict.match(charArray, begin, length);
	}
	
	
	/**
	 * 从已匹配的Hit中直接取出DictSegment，继续向下匹配
	 * @param charArray
	 * @param currentIndex
	 * @param matchedHit
	 * @return Hit
	 */
	public Hit matchWithHit(char[] charArray , int currentIndex , Hit matchedHit){
		DictSegment ds = matchedHit.getMatchedDictSegment();
		return ds.match(charArray, currentIndex, 1 , matchedHit);
	}
	
	
	/**
	 * 判断是否是停止词
	 * @param charArray
	 * @param begin
	 * @param length
	 * @return boolean
	 */
	public boolean isStopWord(char[] charArray , int begin, int length){			
		return singleton._StopWordDict.match(charArray, begin, length).isMatch();
	}	
	
	/**
	 * 加载主词典及扩展词典
	 */
	private void loadMainDict(Boolean isFixedTime){

		if(!isFixedTime) {
			//读取主词典文件
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(cfg.getMainDictionary());
			if (is == null) {
				throw new RuntimeException("Main Dictionary not found!!!");
			}

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
				String theWord = null;
				do {
					theWord = br.readLine();
					if (theWord != null && !"".equals(theWord.trim())) {
						_MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
					}
				} while (theWord != null);

			} catch (IOException ioe) {
				System.err.println("Main Dictionary loading exception.");
				ioe.printStackTrace();

			} finally {
				try {
					if (is != null) {
						is.close();
						is = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//加载扩展词典
		this.loadExtDict(isFixedTime);
		this.loadRemoteDict(isFixedTime);
	}

	/**
	 * 加载用户配置的扩展词典到主词库表
	 */
	private void loadExtDict(Boolean isFixedTime){
		//加载扩展词典配置
		List<String> extDictFiles  = cfg.getExtDictionarys();
		if(extDictFiles != null){

			for(String extDictName : extDictFiles) {

				File f = new File(this.getClass().getClassLoader().getResource("").getPath() + extDictName);
				if (f == null || !f.exists())
					continue;

				if (isFixedTime) {
					if (filesLeng.containsKey(f.getPath()) && filesLeng.get(f.getPath()) == f.length())
						continue;
					filesLeng.put(f.getPath(), f.length());
				} else {
					filesLeng.put(f.getPath(), f.length());
				}

				//读取扩展词典文件
				System.out.println("加载扩展词典：" + extDictName);
				InputStream is = null;
				try {
					is = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
					String theWord = null;
					do {
						theWord = br.readLine();
						if (theWord != null && !"".equals(theWord.trim())) {
							//加载扩展词典数据到主内存词典中
							//System.out.println(theWord);
							_MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
						}
					} while (theWord != null);

				} catch (IOException ioe) {
					System.err.println("Extension Dictionary loading exception.");
					ioe.printStackTrace();

				} finally {
					try {
						if (is != null) {
							is.close();
							is = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}		
	}

	private void loadRemoteDict(Boolean isFixedTime){
		//加载远程词典配置
		List<String> remoteDictFiles  = cfg.getRemoteDictionarys();
		if(remoteDictFiles != null){
			InputStream is = null;
			for(String remoteDictName : remoteDictFiles){

				File f = new File(remoteDictName);
				if (f == null || !f.exists()) {
					continue;
				}
				if (isFixedTime) {
					if (filesLeng.containsKey(f.getPath()) && filesLeng.get(f.getPath()) == f.length())
						continue;
					filesLeng.put(f.getPath(), f.length());
				} else {
					filesLeng.put(f.getPath(), f.length());
				}

				try {
					//读取扩展词典文件
					System.out.println("加载远程词典：" + remoteDictName);

					is = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
					String theWord = null;
					do {
						theWord = br.readLine();
						if (theWord != null && !"".equals(theWord.trim())) {
							//加载扩展词典数据到主内存词典中
							//System.out.println(theWord);
							_MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
						}
					} while (theWord != null);

				} catch (IOException ioe) {
					System.err.println("Extension Dictionary loading exception.");
					ioe.printStackTrace();

				}finally{
					try {
						if(is != null){
							is.close();
							is = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	private void loadRemoteStopWordDict(Boolean isFixedTime){
		//加载远程词典配置
		List<String> remoteStopDictFiles  = cfg.getRemoteStopWordDictionarys();
		if(remoteStopDictFiles != null){
			InputStream is = null;
			for(String remoteStopDictName : remoteStopDictFiles){

				File f = new File(remoteStopDictName);
				if (f == null || !f.exists()) {
					continue;
				}
				if (isFixedTime) {
					if (filesLeng.containsKey(f.getPath()) && filesLeng.get(f.getPath()) == f.length())
						continue;
					filesLeng.put(f.getPath(), f.length());
				} else {
					filesLeng.put(f.getPath(), f.length());
				}

				try {
					//读取远程词典文件
					System.out.println("加载远程停用词典：" + remoteStopDictName);

					is = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
					String theWord = null;
					do {
						theWord = br.readLine();
						if (theWord != null && !"".equals(theWord.trim())) {
							//加载远程停止词典数据到内存中
							_StopWordDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
						}
					} while (theWord != null);

				} catch (IOException ioe) {
					System.err.println("Extension Dictionary loading exception.");
					ioe.printStackTrace();

				}finally{
					try {
						if(is != null){
							is.close();
							is = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 加载用户扩展的停止词词典
	 */
	private void loadStopWordDict(Boolean isFixedTime){

		//加载扩展停止词典
		List<String> extStopWordDictFiles  = cfg.getExtStopWordDictionarys();
		if(extStopWordDictFiles != null){

			for(String extStopWordDictName : extStopWordDictFiles){

				File f = new File(this.getClass().getClassLoader().getResource("").getPath() + extStopWordDictName);
				if (f == null || !f.exists())
					continue;

				if (isFixedTime) {
					if (filesLeng.containsKey(f.getPath()) && filesLeng.get(f.getPath()) == f.length())
						continue;
					filesLeng.put(f.getPath(), f.length());
				} else {
					filesLeng.put(f.getPath(), f.length());
				}
				InputStream is = null;
				System.out.println("加载扩展停止词典：" + extStopWordDictName);
				try {
					is = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
					String theWord = null;
					do {
						theWord = br.readLine();
						if (theWord != null && !"".equals(theWord.trim())) {
							//System.out.println(theWord);
							//加载扩展停止词典数据到内存中
							_StopWordDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
						}
					} while (theWord != null);
					
				} catch (IOException ioe) {
					System.err.println("Extension Stop word Dictionary loading exception.");
					ioe.printStackTrace();
					
				}finally{
					try {
						if(is != null){
		                    is.close();
		                    is = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		this.loadRemoteStopWordDict(isFixedTime);
	}
	
	/**
	 * 加载量词词典
	 */
	private void loadQuantifierDict(){
		//建立一个量词典实例
		_QuantifierDict = new DictSegment((char)0);
		//读取量词词典文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(cfg.getQuantifierDicionary());
        if(is == null){
        	throw new RuntimeException("Quantifier Dictionary not found!!!");
        }
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String theWord = null;
			do {
				theWord = br.readLine();
				if (theWord != null && !"".equals(theWord.trim())) {
					_QuantifierDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
				}
			} while (theWord != null);
			
		} catch (IOException ioe) {
			System.err.println("Quantifier Dictionary loading exception.");
			ioe.printStackTrace();
			
		}finally{
			try {
				if(is != null){
                    is.close();
                    is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
