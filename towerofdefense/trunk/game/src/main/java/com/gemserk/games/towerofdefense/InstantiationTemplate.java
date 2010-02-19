package com.gemserk.games.towerofdefense;

import com.gemserk.componentsengine.entities.Entity;

public interface InstantiationTemplate {

	Entity get();

	Entity get(Object... parameters);

}