package com.gemserk.games.jylonwars.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Sets;

public class CopyOfDataStoreJSONInFileImplTest2 {

		
	public static void main(String[] args) throws IOException {

		Collection<Data> datas = new ArrayList<Data>();
		
		for (int i = 0; i < 10; i++) {
			
			Data data = new Data();
			data.setId("elid-" + i);
			data.setTags(Sets.newHashSet("tag1","tag2"));
			data.setValues(new HashMap<String, Object>(){{
				put("key1","value1");
				put("key2","value2");
			}});
			
			datas.add(data);
		}
		
		File file = new File(System.getProperty("user.home") + "/.gemserk/jylonwars/storage.data");
		
		DataStoreJSONInFileImpl datastore = new DataStoreJSONInFileImpl(file);
		
		for (Data data : datas) {
			datastore.submit(data);
		}
		
		
		Collection<Data> datasNew = datastore.get(Sets.newHashSet("tag1"));
		
		
		for (Data data : datasNew) {
			System.out.println("DATA:" + data.getId());
			System.out.println("TAGS: " + data.getTags());
			System.out.println("VALUES: " + data.getValues());
			System.out.println();
		}
		
		
	}

}
