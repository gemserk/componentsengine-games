package com.gemserk.games.zombierockers;

import java.util.Map;

public class SubPathDefinition {

	final float start;
	final float end;
	final Map<String, Object> metadata;

	public float getStart() {
		return start;
	}

	public float getEnd() {
		return end;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public SubPathDefinition(float start, float end, Map<String, Object> metadata) {
		this.start = start;
		this.end = end;
		this.metadata = metadata;
	}

}
