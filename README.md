	<bean class="com.appleframework.analyzer.cfg.ConfigProperties" lazy-init="false" >
		<property name="extDict" value="${analzer.ext.dict:ext.txt;}"/>
		<property name="extStopWords" value="${analzer.ext.stop.words:topword.txt;}"/>
		<property name="resetDicInterval" value="${analzer.reset.dic.interval:10000}" />

		<property name="remoteDictClass" value="${analzer.remote.dict.class:com.appleframework.analyzer.lexicon.HadoopLexicon}"/>
		<property name="hdfsUrl" value="${analzer.hdfs.url:hdfs://192.168.1.229/}"/>
		<property name="hdfsUser" value="${analzer.hdfs.user:hdfs}"/>
		<property name="localDicPath" value="${analzer.local.dic.path:/work/data/solr/dic/}"/>
		<property name="remoteDicPath" value="${analzer.remote.dic.path:/solr/dic}"/>
		<property name="localStopDicPath" value="${analzer.local.stop.dic.path:/work/data/solr/stopdic/}"/>
		<property name="remoteStopDicPath" value="${analzer.remote.stop.dic.path:/solr/stopdic}"/>

	</bean>
