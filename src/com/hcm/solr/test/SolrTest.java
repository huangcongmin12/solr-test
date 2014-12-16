package com.hcm.solr.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.Before;
import org.junit.Test;

import com.hcm.solr.entity.Message;

public class SolrTest {

	private static Log logger = LogFactory.getLog(SolrTest.class);

	private static final String URL = "http://127.0.0.1:8086/solr";

	private HttpSolrServer server = null;

	@Before
	public void init() {
		// 创建 server
		server = new HttpSolrServer(URL);
	}

	/**
	 * 添加文档
	 */
	@Test
	public void addDoc() {

		SolrInputDocument doc = new SolrInputDocument();

		doc.addField("id", "11");
		doc.addField("title", "this is my document !!");

		try {

			UpdateResponse response = server.add(doc);
			// 提交
			server.commit();

			logger.info("########## Query Time :" + response.getQTime());
			logger.info("########## Elapsed Time :" + response.getElapsedTime());
			logger.info("########## Status :" + response.getStatus());

		} catch (SolrServerException | IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * 添加多个文档
	 */
	@Test
	public void addDocs() {

		String[] titles = new String[] { "aaaaaaa", "bbbbbbb", "ccccccc", "dddddd", "eeeeee" };

		List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		int i = 0;
		for (String str : titles) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField("id", i++);
			doc.addField("title", str);
			docs.add(doc);
		}

		try {

			UpdateResponse response = server.add(docs);
			server.commit();

			logger.info("########## Query Time :" + response.getQTime());
			logger.info("########## Elapsed Time :" + response.getElapsedTime());
			logger.info("########## Status :" + response.getStatus());

		} catch (SolrServerException | IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * 添加一个Entity到索引库
	 */
	@Test
	public void addBean() {

		Message msg = new Message("1001", "What is Fluentd?", new String[] { "Fluentd is an open source data collector for unified logging layer",
				"Fluentd allows you to unify data collection and consumption for a better use and understanding of data.",
				"Fluentd decouples data sources from backend systems by providing a unified logging layer in between.",
				"Fluentd proves you can achieve programmer happiness and performance at the same time. A great example of Ruby beyond the Web.",
				"Fluentd to differentiate their products with better use of data." });

		try {

			UpdateResponse response = server.addBean(msg);
			server.commit();

			logger.info("########## Query Time :" + response.getQTime());
			logger.info("########## Elapsed Time :" + response.getElapsedTime());
			logger.info("########## Status :" + response.getStatus());

		} catch (SolrServerException | IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * 添加多个Entity到索引库
	 */
	@Test
	public void addBeans() {

		List<Message> msgs = new ArrayList<Message>();

		Message msg = new Message("1001", "What is Fluentd?", new String[] { "Fluentd is an open source data collector for unified logging layer",
				"Fluentd allows you to unify data collection and consumption for a better use and understanding of data.",
				"Fluentd decouples data sources from backend systems by providing a unified logging layer in between.",
				"Fluentd proves you can achieve programmer happiness and performance at the same time. A great example of Ruby beyond the Web.",
				"Fluentd to differentiate their products with better use of data." });

		Message msg2 = new Message("1002", "What is Fluentd?", new String[] { "Fluentd is an open source data collector for unified logging layer",
				"Fluentd allows you to unify data collection and consumption for a better use and understanding of data.",
				"Fluentd decouples data sources from backend systems by providing a unified logging layer in between.",
				"Fluentd proves you can achieve programmer happiness and performance at the same time. A great example of Ruby beyond the Web.",
				"Fluentd to differentiate their products with better use of data." });

		msgs.add(msg);
		msgs.add(msg2);

		try {

			UpdateResponse response = server.addBeans(msgs);
			server.commit();

			logger.info("########## Query Time :" + response.getQTime());
			logger.info("########## Elapsed Time :" + response.getElapsedTime());
			logger.info("########## Status :" + response.getStatus());

		} catch (SolrServerException | IOException e) {
			logger.error("", e);
		}
	}

	/**
	 * 删除索引
	 */
	@Test
	public void deleteDoc() {
		try {
			server.deleteById("0");
			server.commit();
		} catch (SolrServerException | IOException e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 更新索引<br>
	 * solr索引库不同于数据库，没有更新的功能。如果想更新，先通过id删除对应的文档，再添加新的文档。
	 */
	@Test
	public void updateDoc() {
		// ... ...
	}

	/**
	 * 查询
	 */
	@Test
	public void testQuery() {
		String queryStr = "*:*";
		SolrQuery params = new SolrQuery(queryStr);
		params.set("rows", 10);
		try {
			QueryResponse response = server.query(params);
			SolrDocumentList list = response.getResults();
			logger.info("########### 总共 ： " + list.getNumFound() + "条记录");
			for (SolrDocument doc : list) {
				logger.info("######### id : " + doc.get("id") + "  title : " + doc.get("title"));
			}
		} catch (SolrServerException e) {
			logger.error("", e);
		}
	}

	/**
	 * 简单查询(分页)
	 */
	@Test
	public void querySimple() {
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", "this my");
		params.set("q.op", "and");
		params.set("start", 0);
		params.set("rows", 5);
		params.set("fl", "*,score");
		try {
			QueryResponse response = server.query(params);
			SolrDocumentList list = response.getResults();
			logger.info("########### 总共 ： " + list.getNumFound() + "条记录");
			for (SolrDocument doc : list) {
				logger.info("######### id : " + doc.get("id") + "  title : " + doc.get("title"));
			}
		} catch (SolrServerException e) {
			logger.error("", e);
		}
	}

	/**
	 * 查询(分页,高亮)
	 */
	@Test
	public void queryCase() {
		String queryStr = "title:this";
		SolrQuery params = new SolrQuery(queryStr);
		params.set("start", 0);
		params.set("rows", 5);

		// 启用高亮组件, 设置高亮
		params.setHighlight(true) 							
			.addHighlightField("title") 					
			.setHighlightSimplePre("<span class=\"red\">")
			.setHighlightSimplePost("</span>")
			.setHighlightSnippets(2)
			.setHighlightFragsize(1000)
			.setStart(0)
			.setRows(10)
			.set("hl.useFastVectorHighlighter", "true")
			.set("hl.fragsize", "200");
		
		
		params.setHighlight(true);
		// 高亮字段
		params.addHighlightField("title");
		params.set("hl.useFastVectorHighlighter", "true");
		params.set("hl.fragsize", "200");
		// 高亮关键字前缀，如不设置，则默认输出<em>xxx</em>
		params.setHighlightSimplePre("<span class=\"red\">");
		// 高亮关键字后缀
		params.setHighlightSimplePost("</span>");
		// 结果分片数，默认为1
		params.setHighlightSnippets(2);
		// 每个分片的最大长度，默认为100
		params.setHighlightFragsize(1000);

		try {
			QueryResponse response = server.query(params);
			SolrDocumentList list = response.getResults();
			logger.info("########### 总共 ： " + list.getNumFound() + "条记录");
			for (SolrDocument doc : list) {
				logger.info("######### id : " + doc.get("id") + "  title : " + doc.get("title"));
			}

			Map<String, Map<String, List<String>>> map = response.getHighlighting();
			Iterator<String> iterator = map.keySet().iterator();

			while (iterator.hasNext()) {
				String key = iterator.next();
				Map<String, List<String>> values = map.get(key);
				logger.info("############################################################");
				logger.info("############ id : " + key);

				for (Map.Entry<String, List<String>> entry : values.entrySet()) {
					String subKey = entry.getKey();
					List<String> subValues = entry.getValue();

					logger.info("############ subKey : " + subKey);
					for (String str : subValues) {
						logger.info("############ subValues : " + str);
					}
				}
				
			}
			
		} catch (SolrServerException e) {
			logger.error("", e);
		}
	}

}
