package com.gemserk.games.jylonwars.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Sets;

public class DataStoreJSONInFileImplTest {

	
	public static class DataContainer {
		Collection<Data> datas;
		
		public Collection<Data> getDatas() {
			return datas;
		}
		
		public void setDatas(Collection<Data> datas) {
			this.datas = datas;
		}
	}
	
	
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
		
		
		DataContainer dc = new DataContainer();
		dc.setDatas(datas);
		
	//	JSONObject jobject = JSONObject.fromObject(dc);
		JSONArray jobject = JSONArray.fromObject(datas);
		String jsonOrig = jobject.toString(1);
		
		System.out.println(jsonOrig);
	
//		Map classMap = new HashMap();  
//		classMap.put( "datas", Data.class );  
//		DataContainer dc2 = (DataContainer) JSONObject.toBean( JSONObject.fromObject(jsonOrig), DataContainer.class, classMap );  
		
		JSONArray jobjectNew = JSONArray.fromObject(jsonOrig);
		
		
		File file = new File("/tmp/jylonwars/config/storage.data");
		if(!file.exists()){
			System.out.println("New File Created");
			FileUtils.writeStringToFile(file, "[]");
		}
		else{
			System.out.println("Old file used");
			FileUtils.writeStringToFile(file, jsonOrig);
		}
		
		System.out.println(file);
		
		
		Collection<Data> datasNew = JSONArray.toCollection(jobjectNew, Data.class);
		
		
		for (Data data : datasNew) {
			System.out.println("DATA:" + data.getId());
			System.out.println("TAGS: " + data.getTags());
			System.out.println("VALUES: " + data.getValues());
			System.out.println();
		}
		
		
	}

}
