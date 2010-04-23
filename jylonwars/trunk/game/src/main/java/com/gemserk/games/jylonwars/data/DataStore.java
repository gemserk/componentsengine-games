package com.gemserk.games.jylonwars.data;

import java.util.Collection;
import java.util.Set;

public interface DataStore {
	
	void submit(Data data);
	
	Collection<Data> get(Set<String> tags);

}
