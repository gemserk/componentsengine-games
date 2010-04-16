package com.gemserk.games.jylonwars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.effects.EffectFactory;
import com.gemserk.componentsengine.effects.ExplosionEffect;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.messages.GenericMessage;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.properties.Properties;

public class ExplosionComponent extends FieldsReflectionComponent {

	@EntityProperty(readOnly = true, required = false)
	Collection<ExplosionEffect> explosions = new ArrayList<ExplosionEffect>();

	@EntityProperty(readOnly = true)
	Color startColor;

	@EntityProperty(readOnly = true)
	Color endColor;

	@EntityProperty(readOnly = true, required = false)
	int particlesCount = 50;

	@EntityProperty(readOnly = true, required = false)
	int time = 1000;

	@EntityProperty(readOnly = true, required = false)
	float width = 3.0f;

	@EntityProperty(readOnly = true, required = false)
	float maxDistance = 320f;

	@EntityProperty(readOnly = true, required = false)
	float minDistance = 50f;

	public ExplosionComponent(String id) {
		super(id);
	}

	public void handleMessage(UpdateMessage message) {

		int delta = message.getDelta();

		Iterator<ExplosionEffect> iterator = explosions.iterator();
		while (iterator.hasNext()) {
			ExplosionEffect explosion = iterator.next();
			explosion.update(delta);

			if (explosion.isDone())
				iterator.remove();
		}

	}

	public void handleMessage(SlickRenderMessage message) {
		SlickCallable.enterSafeBlock();
		for (ExplosionEffect explosionEffect : explosions) {
			explosionEffect.render();
		}
		SlickCallable.leaveSafeBlock();
	}

	public void handleMessage(GenericMessage message) {

		if (message.getId().equals("critterdead"))
			handleCritterDeadMessage(message);

	}

	protected void handleCritterDeadMessage(GenericMessage message) {
		Entity critter = Properties.getValue(message, "critter");
		Vector2f position = Properties.getValue(critter, "position");
		explosions.add(EffectFactory.explosionEffect(particlesCount, (int) position.x, (int) position.y, 0f, 360f, time, 10.0f, minDistance, maxDistance, width));
	}

}