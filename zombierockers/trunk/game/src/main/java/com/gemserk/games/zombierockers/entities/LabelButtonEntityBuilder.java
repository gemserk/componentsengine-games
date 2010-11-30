package com.gemserk.games.zombierockers.entities;

import com.gemserk.componentsengine.templates.EntityBuilder;

public class LabelButtonEntityBuilder extends EntityBuilder {

	@Override
	public void build() {
		
		parent("gemserk.gui.label", parameters);
		parent("gemserk.gui.button", parameters);

	}
}