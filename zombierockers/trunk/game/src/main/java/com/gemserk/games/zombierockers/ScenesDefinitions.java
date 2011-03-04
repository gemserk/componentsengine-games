package com.gemserk.games.zombierockers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import com.gemserk.componentsengine.commons.path.PathTraversal;
import com.gemserk.componentsengine.commons.path.SubPathDefinition;

public class ScenesDefinitions {

	static class LevelBuilder {

		HashMap<String, Object> properties = new HashMap<String, Object>();
		
		ArrayList<PathBuilder> pathBuilders = new ArrayList<PathBuilder>();
		
		void name(String name) {
			properties.put("name", name);
		}
		
		void background(String bg) {
			properties.put("background", bg);
		}

		void path(PathBuilder pathBuilder){ 
			pathBuilders.add(pathBuilder);
		}

		Map<String, Object> build() {
			
			ArrayList<Map<String, Object>> pathProperties = new ArrayList<Map<String,Object>>();
			
			for (PathBuilder path : pathBuilders) {
				pathProperties.add(path.build());
			}
			
			properties.put("paths", pathProperties);
			
			return properties;
		}

	}
	
	static class PathBuilder {
		
		Map<String, Object> properties = new HashMap<String, Object>();
		
		void path(String path) {
			properties.put("path", path);
		}
		
		Map<String, Object> build() {
			return properties;
		}
		
	}

	final static Map<String, Object> redBallType = new HashMap<String, Object>() {
		{
			put("type", "red");
			put("animation", "ballanimation");
			put("color", new Color(1f, 0f, 0f));
			put("image", "redball");
		}
	};

	final static Map<String, Object> blueBallType = new HashMap<String, Object>() {
		{
			put("type", "blue");
			put("animation", "ballanimation");
			put("color", new Color(0f, 0f, 1f));
			put("image", "blueball");
		}
	};

	final static Map<String, Object> greenBallType = new HashMap<String, Object>() {
		{
			put("type", "green");
			put("animation", "ballanimation");
			put("color", new Color(0f, 1f, 0f));
			put("image", "greenball");
		}
	};

	final static Map<String, Object> whiteBallType = new HashMap<String, Object>() {
		{
			put("type", "white");
			put("animation", "ballanimation");
			put("color", new Color(1f, 1f, 1f));
			put("image", "whiteball");
		}
	};

	static Map ballDefinitions(Map... maps) {
		HashMap hashMap = new HashMap();
		for (Map map : maps) {
			hashMap.put(map.get("type"), map);
		}
		return hashMap;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> levels() {

		final SubPathDefinition defaultSubPathDefinition = new SubPathDefinition(0, 1000000, new HashMap<String, Object>() {
			{
				put("layer", 10);
				put("collisionMask", 1);
			}
		});

		Map<String, Object> level01 = new LevelBuilder() {
			{
				name("level01");
				background("background");
				
				path(new PathBuilder(){{
					path("levels/level01/path.svg");
					properties.put("ballsQuantity", 40);
					properties.put("pathProperties", new HashMap<String, Object>() {
						{
							put("speed", 0.04f);
							put("acceleratedSpeed", 0.3f);
							put("accelerationStopPoint", 500f);
							put("minSpeedFactor", 0.2f);
							put("maxSpeed", 0.035f);
							put("speedWhenReachBase", 0.4f);
						}
					});
					properties.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
				}});
				properties.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType));
				properties.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level01_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
			}
		}.build();

		Map<String, Object> level02 = new LevelBuilder() {
			{
				name("level02");
				background("background");
				
				path(new PathBuilder(){{
					path("levels/level02/path.svg");
					properties.put("ballsQuantity", 60);
					properties.put("pathProperties", new HashMap<String, Object>() {
						{
							put("speed", 0.04f);
							put("acceleratedSpeed", 0.3f);
							put("accelerationStopPoint", 600f);
							put("minSpeedFactor", 0.2f);
							put("maxSpeed", 0.04f);
							put("speedWhenReachBase", 0.4f);
						}
					});
					properties.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
				}});
				
				properties.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType));
				properties.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level02_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
			}
		}.build();
		
		Map<String, Object> level03 = new LevelBuilder() {
			{
				name("level03");
				background("background");
				
				path(new PathBuilder(){{
					path("levels/level03/path.svg");
					properties.put("ballsQuantity", 80);
					properties.put("pathProperties", new HashMap<String, Object>() {
						{
							put("speed", 0.04f);
							put("acceleratedSpeed", 0.5f);
							put("accelerationStopPoint", 800f);
							put("minSpeedFactor", 0.2f);
							put("maxSpeed", 0.04f);
							put("speedWhenReachBase", 0.4f);
						}
					});
					properties.put("subPathDefinitions", new SubPathDefinitions(Arrays.asList(subPathDefinition(1081f, 1300f, 9, 1)), defaultSubPathDefinition));
				}});
				
				properties.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType));
				properties.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level03_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
			}
		}.build();
		
		Map<String, Object> level04 = new LevelBuilder() {
			{
				name("level04");
				background("background");
				
				path(new PathBuilder(){{
					path("levels/level04/path.svg");
					properties.put("ballsQuantity", 100);
					properties.put("pathProperties", new HashMap<String, Object>() {
						{
							put("speed", 0.04f);
							put("acceleratedSpeed", 0.5f);
							put("accelerationStopPoint", 800f);
							put("minSpeedFactor", 0.2f);
							put("maxSpeed", 0.04f);
							put("speedWhenReachBase", 0.4f);
						}
					});
					properties.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
				}});

				properties.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				properties.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level04_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
			}
		}.build();
		
		Map<String, Object> level05 = new LevelBuilder() {
			{
				name("level05");
				background("background");
				
				path(new PathBuilder(){{
					path("levels/level05/path.svg");
					properties.put("ballsQuantity", 120);
					properties.put("pathProperties", new HashMap<String, Object>() {
						{
							put("speed", 0.04f);
							put("acceleratedSpeed", 0.5f);
							put("accelerationStopPoint", 800f);
							put("minSpeedFactor", 0.2f);
							put("maxSpeed", 0.04f);
							put("speedWhenReachBase", 0.4f);
						}
					});
					properties.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
				}});

				properties.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				properties.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level05_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
			}
		}.build();
		
		Map<String, Object> level06 = new LevelBuilder() {
			{
				name("level07");
				background("background");
				
				path(new PathBuilder(){{
					path("levels/level06/path01.svg");
					properties.put("pathId", "path01");
					properties.put("ballsQuantity", 60);
					properties.put("pathProperties", new HashMap<String, Object>() {
						{
							put("speed", 0.03f);
							put("acceleratedSpeed", 0.4f);
							put("accelerationStopPoint", 400f);
							put("minSpeedFactor", 0.2f);
							put("maxSpeed", 0.03f);
							put("speedWhenReachBase", 0.4f);
						}
					});
					properties.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
				}});

				path(new PathBuilder(){{
					path("levels/level06/path02.svg");
					properties.put("pathId", "path02");
					properties.put("ballsQuantity", 40);
					properties.put("pathProperties", new HashMap<String, Object>() {
						{
							put("speed", 0.04f);
							put("acceleratedSpeed", 0.5f);
							put("accelerationStopPoint", 600f);
							put("minSpeedFactor", 0.2f);
							put("maxSpeed", 0.04f);
							put("speedWhenReachBase", 0.4f);
						}
					});
					properties.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
				}});

				properties.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				properties.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level06_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
			}
		}.build();

		return Arrays.asList(level01, level02, level03, level04, level05, level06);
	}

	@SuppressWarnings("serial")
	static SubPathDefinition subPathDefinition(float start, float end, final int layer, final int collisionMask) {
		return new SubPathDefinition(start, end, new HashMap<String, Object>() {
			{
				put("layer", layer);
				put("collisionMask", collisionMask);
			}
		});
	}

	public static class SubPathDefinitions {

		private final List<SubPathDefinition> elements;

		private final SubPathDefinition defaultElement;

		public SubPathDefinitions(SubPathDefinition defaultElement) {
			this(new ArrayList<SubPathDefinition>(), defaultElement);
		}

		public SubPathDefinitions(List<SubPathDefinition> elements, SubPathDefinition defaultElement) {
			this.elements = elements;
			this.defaultElement = defaultElement;
		}

		public SubPathDefinition getSubPathDefinition(PathTraversal pathTraversal) {
			float distanceFromOrigin = pathTraversal.getDistanceFromOrigin();

			for (SubPathDefinition subPathDefinition : elements)
				if (subPathDefinition.getStart() <= distanceFromOrigin && subPathDefinition.getEnd() > distanceFromOrigin)
					return subPathDefinition;

			return defaultElement;
		}

	}
}
