package com.mocircle.flume.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mocircle.jwinlog.EventEntryIterator;

public class RecordStatusIO {

	private static final Logger LOG = LoggerFactory.getLogger(RecordStatusIO.class);

	private String recordFile;
	private Map<String, Long> initStartRecordMap;
	private Map<String, Long> recordMap = new ConcurrentHashMap<>();

	public RecordStatusIO(String recordFile, Map<String, Long> initStartRecordMap) {
		this.recordFile = recordFile;
		this.initStartRecordMap = initStartRecordMap;
	}

	public Map<String, Long> readRecords() {
		Map<String, Long> map = loadRecordFile();
		for (String key : initStartRecordMap.keySet()) {
			if (!map.containsKey(key)) {
				map.put(key, initStartRecordMap.get(key));
			}
		}
		return map;
	}

	public void updateRecord(Collection<EventEntryIterator> iterators) {
		for (EventEntryIterator iterator : iterators) {
			recordMap.put(iterator.getChannelName(), iterator.getLastRecordId());
		}
	}

	public void commitRecord() {
		File file = new File(recordFile);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		Properties prop = new Properties();
		for (Map.Entry<String, Long> item : recordMap.entrySet()) {
			prop.put(item.getKey(), item.getValue() == null ? "" : item.getValue().toString());
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			prop.store(fos, null);
		} catch (IOException e) {
			LOG.warn("Failed to write record file", e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	private Map<String, Long> loadRecordFile() {
		Map<String, Long> records = new HashMap<>();
		File file = new File(recordFile);
		if (file.exists()) {
			Properties prop = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				prop.load(fis);
			} catch (IOException e) {
				LOG.warn("Failed to load record file", e);
			} finally {
				IOUtils.closeQuietly(fis);
			}
			for (Object key : prop.keySet()) {
				records.put(key.toString(), Long.parseLong(prop.getProperty(key.toString())));
			}
		}
		return records;
	}

}
