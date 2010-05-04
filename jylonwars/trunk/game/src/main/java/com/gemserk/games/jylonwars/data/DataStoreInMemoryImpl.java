package com.gemserk.games.jylonwars.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class DataStoreInMemoryImpl implements DataStore {
	
	Collection<Data> dataCollection = new ArrayList<Data>();

	@Override
	public Collection<Data> get(Set<String> tags) {
		return dataCollection;
	}

	@Override
	public String submit(Data data) {
		data.setId(Integer.toHexString(System.identityHashCode(data)));
		dataCollection.add(data);
		return data.getId();
	}

}
