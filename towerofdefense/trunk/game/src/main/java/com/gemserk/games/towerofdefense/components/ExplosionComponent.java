package com.gemserk.games.towerofdefense.components;

import java.util.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.effects.EffectFactory;
import com.gemserk.componentsengine.effects.ExplosionEffect;
import com.gemserk.componentsengine.entities.Entity;
import com.gemserk.componentsengine.entities.Root;
import com.gemserk.componentsengine.messages.*;
import com.gemserk.componentsengine.properties.Properties;
import com.google.inject.Inject;

public class ExplosionComponent extends FieldsReflectionComponent {

	@Inject
	@Root
	Entity rootEntity;

	Collection<ExplosionEffect> explosions = new ArrayList<ExplosionEffect>();

	Color startColor = new Color(0.8f, 0f, 0f, 1f);

	Color endColor = new Color(1f, 0.5f, 0.5f, 0f);

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

		if (message.getId().equals("hit"))
			handleHitMessage(message);

		if (message.getId().equals("critterdead"))
			handleCritterDeadMessage(message);

	}

	protected void handleCritterDeadMessage(GenericMessage message) {
		Entity critter = Properties.getValue(message, "critter");
		Vector2f position = Properties.getValue(critter, "position");
		explosions.add(EffectFactory.explosionEffect(50, (int) position.x, (int) position.y, 0f, 360f, 300, 10.0f, 30f, 80f, 1.0f));
	}

	protected void handleHitMessage(GenericMessage message) {
		Entity source = Properties.getValue(message, "source");

		if (!source.getTags().contains("bullet"))
			return;

		ArrayList<Entity> targets = Properties.getValue(message, "targets");

		for (Entity target : targets) {
			Float damage = (Float) Properties.getValue(source, "damage");
			int count = (int) Math.ceil(damage);

			Vector2f position = Properties.getValue(target, "position");
			explosions.add(EffectFactory.explosionEffect(count, (int) position.x, (int) position.y, 0f, 360f, 300, 5.0f, 20f, 30f, 1.0f, startColor, endColor));
		}
	}

}