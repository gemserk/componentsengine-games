package com.gemserk.games.jylonwars.data;

import java.util.Collection;
import java.util.Set;

public interface DataStore {
	
	/**
	 * @param data
	 * @return the id of the entry
	 */
	String submit(Data data);
	
	Collection<Data> get(Set<String> tags);

}
