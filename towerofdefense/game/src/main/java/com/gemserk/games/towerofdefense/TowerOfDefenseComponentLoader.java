package com.gemserk.games.towerofdefense;

import org.newdawn.slick.Input;

import com.gemserk.componentsengine.components.ComponentManager;
import com.gemserk.componentsengine.resources.ResourceLoader;
import com.gemserk.games.towerofdefense.components.BarRendererComponent;
import com.gemserk.games.towerofdefense.components.BulletCollisionComponent;
import com.gemserk.games.towerofdefense.components.DefenseComponent;
import com.gemserk.games.towerofdefense.components.DefenseTriggerComponent;
import com.gemserk.games.towerofdefense.components.EntityRendererComponent;
import com.gemserk.games.towerofdefense.components.FaceTargetComponent;
import com.gemserk.games.towerofdefense.components.FireComponent;
import com.gemserk.games.towerofdefense.components.FollowEntityComponent;
import com.gemserk.games.towerofdefense.components.FollowPathComponent;
import com.gemserk.games.towerofdefense.components.FollowPathRenderComponent;
import com.gemserk.games.towerofdefense.components.ImageRenderableComponent;
import com.gemserk.games.towerofdefense.components.MovementComponent;
import com.gemserk.games.towerofdefense.components.RadiusRendererComponent;
import com.gemserk.games.towerofdefense.components.RemoveWhenNearComponent;
import com.gemserk.games.towerofdefense.components.RestartSceneComponent;
import com.gemserk.games.towerofdefense.components.SelectTargetWithinRangeComponent;
import com.gemserk.games.towerofdefense.components.editor.AddItemComponent;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TowerOfDefenseComponentLoader implements ResourceLoader {

	@Inject
	ComponentManager componentManager;

	@Inject
	Injector injector;

	@Inject
	Input input;

	@Override
	public void load() {

		com.gemserk.componentsengine.components.Component[] components = {

				// [rendering]
				new EntityRendererComponent("renderer"),
				new DefenseComponent("defense1"),
				new DefenseComponent("defense2"),
				new BarRendererComponent("containerrenderer"),
				new FollowPathRenderComponent("followpath-renderer"),
				new RadiusRendererComponent("radiusrenderer"),
				new ImageRenderableComponent("imagerenderer"),

				// [game_mechanics]
				new MovementComponent("movement"),
				new FollowPathComponent("followpath"),
				new FireComponent("shoot"),
				new FollowEntityComponent("simplepath"),
				new RemoveWhenNearComponent("removewhennear"),
				new BulletCollisionComponent("bulletcollision"),

				new SelectTargetWithinRangeComponent(
						"selectTargetWithinRangeComponent"),
				new FaceTargetComponent("faceTargetComponent"),
				new DefenseTriggerComponent("defense.trigger"),

				// [editor]
				new AddItemComponent("editor.addItem"),

				new RestartSceneComponent("restartComponent") };

		for (com.gemserk.componentsengine.components.Component component : components) {
			injector.injectMembers(component);
		}

		componentManager.addComponents(components);

	}

}