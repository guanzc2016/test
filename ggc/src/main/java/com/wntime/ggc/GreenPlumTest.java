package com.wntime.ggc;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import io.pivotal.gemfire.gpdb.operations.OperationException;
import io.pivotal.gemfire.gpdb.service.GpdbService;
import io.pivotal.gemfire.gpdb.service.ImportConfiguration;
import io.pivotal.gemfire.gpdb.service.ImportResult;

public class GreenPlumTest {

	private String host = "192.168.119.134";
	private int port = 12345;
	private String regionName = "Parent";

	ClientCache cache = null;
	Region<String, String> region = null;

	
	public static void main(String[] args) {
		GreenPlumTest t = new GreenPlumTest();
		t.importData();
	}
	
	public void createRrion() {
		cache = new ClientCacheFactory().addPoolLocator(host, port).setPoolSubscriptionEnabled(true)
				.setPoolSubscriptionRedundancy(1).setPoolReadTimeout(100000).setPdxReadSerialized(true)
				.set("log-level", "info").create();
		region = cache.<String, String> createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU)
				.create(regionName);
	}

	/**
	 * greenplum导出数据到gemfire
	 */
	public void importData() {
		createRrion();
		try {
			ImportConfiguration configuration = ImportConfiguration.builder(region).build();
			ImportResult importResult = GpdbService.importRegion(configuration);
			int importCount = importResult.getImportedCount();
			System.out.println("数量="+importCount);
		} catch (OperationException e) {
			e.printStackTrace();
		}

	}
	
	
}
