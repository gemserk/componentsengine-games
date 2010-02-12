package com.gemserk.games.towerofdefense;

import com.gemserk.componentsengine.entities.Entity;

public interface InstantiationTemplate {

	public abstract Entity get();

	public abstract Entity get(Object... parameters);

}