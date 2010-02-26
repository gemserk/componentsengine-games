package com.gemserk.games.towerofdefense;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class PlayingGameState extends GemserkGameState {

	@Override
	public int getID() {
		return 0;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
		super.init(container, stateBasedGame);
		game.loadScene("towerofdefense.scenes.scene1");
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.setBackground(new Color(0.15f, 0.15f, 0.15f));
		super.render(container, game, g);
	}

}