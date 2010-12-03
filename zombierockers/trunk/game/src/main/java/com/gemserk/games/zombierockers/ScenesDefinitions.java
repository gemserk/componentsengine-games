package com.gemserk.games.zombierockers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class ScenesDefinitions {

	static class LevelBuilder {

		HashMap<String, Object> level = new HashMap<String, Object>();

		void background(String bg) {
			level.put("background", bg);
		}

		void path(String path) {
			level.put("path", path);
		}

		Map<String, Object> build() {
			return level;
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
				background("background");
				path("levels/level01/path.svg");
				level.put("ballsQuantity", 40);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.3f);
						put("accelerationStopPoint", 500f);
						put("minSpeedFactor", 0.2f);
						put("maxSpeed", 0.035f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType));
				level.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level01_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();

		Map<String, Object> level02 = new LevelBuilder() {
			{
				background("level02");
				path("levels/level02/path.svg");
				level.put("ballsQuantity", 60);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 800f);
						put("minSpeedFactor", 0.2f);
						put("maxSpeed", 0.05f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType));
				level.put("placeables", new ArrayList());
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();
		
		Map<String, Object> level03 = new LevelBuilder() {
			{
				background("level03");
				path("levels/level03/path.svg");
				level.put("ballsQuantity", 80);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 800f);
						put("minSpeedFactor", 0.2f);
						put("maxSpeed", 0.04f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType));
				level.put("placeables", new ArrayList());
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();
		
		Map<String, Object> level04 = new LevelBuilder() {
			{
				background("level04");
				path("levels/level04/path.svg");
				level.put("ballsQuantity", 100);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 1300f);
						put("minSpeedFactor", 0.3f);
						put("maxSpeed", 0.05f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				level.put("placeables", new ArrayList());
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();
		
		Map<String, Object> level05 = new LevelBuilder() {
			{
				background("level05");
				path("levels/level05/path.svg");
				level.put("ballsQuantity", 100);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 1300f);
						put("minSpeedFactor", 0.3f);
						put("maxSpeed", 0.05f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				level.put("placeables", new ArrayList());
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();
		
		Map<String, Object> level06 = new LevelBuilder() {
			{
				background("level06");
				path("levels/level06/path.svg");
				level.put("ballsQuantity", 100);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 1300f);
						put("minSpeedFactor", 0.3f);
						put("maxSpeed", 0.05f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				level.put("placeables", new ArrayList());
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();
		
		Map<String, Object> level07 = new LevelBuilder() {
			{
				background("level07");
				path("levels/level07/path.svg");
				level.put("ballsQuantity", 100);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 1300f);
						put("minSpeedFactor", 0.3f);
						put("maxSpeed", 0.05f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				level.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level07-tunnel");
						put("position", new Vector2f(305f, 206f));
						put("layer", -2000);
					}});
				}});
				level.put("subPathDefinitions", new SubPathDefinitions(Arrays.asList(subPathDefinition(1374.4f, 1639.9f, -1500, 7)), defaultSubPathDefinition));
			}
		}.build();

		Map<String, Object> level08 = new LevelBuilder() {
			{
				background("level08");
				path("levels/level08/path.svg");
				level.put("ballsQuantity", 100);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 1300f);
						put("minSpeedFactor", 0.3f);
						put("maxSpeed", 0.05f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				level.put("placeables", new ArrayList());
				level.put("subPathDefinitions", new SubPathDefinitions(Arrays.asList(subPathDefinition(412.8999f, 706.1004f, 15, 7)), defaultSubPathDefinition));
				level.put("alphaMasks", new HashMap() {
					{
						put(15, "level08_alphaMask");
					}
				});
			}
		}.build();
		
		Map<String, Object> level09 = new LevelBuilder() {
			{
				background("level09");
				path("levels/level09/path.svg");
				level.put("ballsQuantity", 100);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.5f);
						put("accelerationStopPoint", 1300f);
						put("minSpeedFactor", 0.3f);
						put("maxSpeed", 0.05f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType, whiteBallType));
				level.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level09_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();
		
		Map<String, Object> level10 = new LevelBuilder() {
			{
				background("level09");
				path("levels/level10/path.svg");
				level.put("ballsQuantity", 40);
				level.put("pathProperties", new HashMap<String, Object>() {
					{
						put("speed", 0.04f);
						put("acceleratedSpeed", 0.3f);
						put("accelerationStopPoint", 500f);
						put("minSpeedFactor", 0.2f);
						put("maxSpeed", 0.035f);
						put("speedWhenReachBase", 0.4f);
					}
				});
				level.put("ballDefinitions", ballDefinitions(redBallType, blueBallType, greenBallType));
				level.put("placeables", new ArrayList(){{
					add(new HashMap<String, Object>(){{
						put("image", "level10_path");
						put("position", new Vector2f(400f, 300f));
						put("layer", 0);
					}});
				}});
				level.put("subPathDefinitions", new SubPathDefinitions(defaultSubPathDefinition));
			}
		}.build();

		return Arrays.asList(level01, level09, level03, level04, level05, level06, level07, level08, level10);
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
				if (subPathDefinition.start <= distanceFromOrigin && subPathDefinition.end > distanceFromOrigin)
					return subPathDefinition;

			return defaultElement;
		}

	}
}
