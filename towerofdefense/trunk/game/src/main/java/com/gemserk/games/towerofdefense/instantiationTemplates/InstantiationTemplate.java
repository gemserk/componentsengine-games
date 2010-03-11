package com.gemserk.games.towerofdefense.instantiationTemplates;

import com.gemserk.componentsengine.entities.Entity;

public interface InstantiationTemplate {

	Entity get();

	Entity get(Object... parameters);

}