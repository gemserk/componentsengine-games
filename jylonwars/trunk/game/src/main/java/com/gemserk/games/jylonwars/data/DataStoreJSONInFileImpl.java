package com.gemserk.games.jylonwars.data;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import net.sf.json.JSONArray;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class DataStoreJSONInFileImpl implements DataStore {

	File storage;

	public DataStoreJSONInFileImpl(File storage) {
		this.storage = storage;
	}

	@Override
	public Collection<Data> get(final Set<String> tags) {
		
		try {
			ensureFileExists();
			String readData = getFileContent();

			Collection<Data> dataCollection = parseData(readData);
			return Collections2.filter(dataCollection, new Predicate<Data>() {
				@Override
				public boolean apply(Data data) {
					return data.getTags().containsAll(tags);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("couldnt read storage: " + storage,e);
		}
	}

	@Override
	public String submit(Data data) {
		try {
			data.setId(Integer.toHexString(System.identityHashCode(data)));

			Collection<Data> previousData = get(new HashSet<String>());
			previousData.add(data);

			String dataToStore = serializeData(previousData);
			writeFileContent(dataToStore);

			return data.getId();
		} catch (IOException e) {
			throw new RuntimeException("couldnt write to  storage: " + storage,e);
		}
	}

	String getFileContent() throws IOException {
		return FileUtils.readFileToString(storage);
	}

	void writeFileContent(String value) throws IOException {
		FileUtils.writeStringToFile(storage, value);
	}
	
	void ensureFileExists() throws IOException{
		if(!storage.exists()){
			writeFileContent("[]");
		}
	}

	Collection<Data> parseData(String data) {
		JSONArray jobjectNew = JSONArray.fromObject(data);
		return JSONArray.toCollection(jobjectNew, Data.class);
	}

	String serializeData(Collection<Data> dataCollection) {
		JSONArray jobject = JSONArray.fromObject(dataCollection);
		String jsonData = jobject.toString(1);
		return jsonData;
	}

}
