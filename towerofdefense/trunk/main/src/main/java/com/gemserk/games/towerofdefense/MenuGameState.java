package com.gemserk.games.towerofdefense;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MenuGameState extends GemserkGameState {

	@Override
	public int getID() {
		return 1;
	}

	@Override
	public void init(final GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
		super.init(container, stateBasedGame);
		game.loadScene("towerofdefense.scenes.menu");
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setBackground(new Color(0f, 0f, 0f));
		super.render(container, game, g);
	}

}