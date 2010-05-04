package com.gemserk.games.jylonwars.data;

import java.util.Map;
import java.util.Set;

public class Data {
	
	String id;

	Set<String> tags;

	Map<String, Object> values;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	public Data() {

	}

	public Data(Set<String> tags, Map<String, Object> values) {
		this.tags = tags;
		this.values = values;
	}

}
