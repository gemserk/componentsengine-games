package com.gemserk.games.zombierockers;

import net.sf.json.JSONArray;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.slick.gamestates.GemserkGameState;
import com.gemserk.componentsengine.utils.EntityDumper;
import com.google.inject.Key;

public class LimitedDeltaUpdateGameState extends GemserkGameState {
	
	protected static final Logger logger = LoggerFactory.getLogger(LimitedDeltaUpdateGameState.class);

	public LimitedDeltaUpdateGameState(int id) {
		super(id);
	}

	public LimitedDeltaUpdateGameState(int id, String defaultScene) {
		super(id, defaultScene);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		try {
			super.render(container, game, g);
		} catch (Exception e) {
			Entity rootEntity = injector.getInstance(Key.get(Entity.class, Root.class));
			JSONArray jobject = JSONArray.fromObject(new EntityDumper().dumpEntity(rootEntity));
			logger.info(jobject.toString(4));
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		try {
			int minimumTargetFPS = 30;
			int maximumDelta = (int) (1000f / minimumTargetFPS);
			if (delta > maximumDelta)
				delta = maximumDelta;
			super.update(container, game, delta);
		} catch (Exception e) {
			Entity rootEntity = injector.getInstance(Key.get(Entity.class, Root.class));
			JSONArray jobject = JSONArray.fromObject(new EntityDumper().dumpEntity(rootEntity));
			logger.info(jobject.toString(4));
			throw new RuntimeException(e);
		}

	}

}