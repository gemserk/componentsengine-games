package com.gemserk.games.towerofdefense.gamestates;

import org.newdawn.slick.Input;

import com.gemserk.componentsengine.components.ComponentManager;
import com.gemserk.componentsengine.resources.ResourceLoader;
import com.gemserk.games.towerofdefense.components.BulletCollisionComponent;
import com.gemserk.games.towerofdefense.components.FireComponent;
import com.gemserk.games.towerofdefense.components.FollowEntityComponent;
import com.gemserk.games.towerofdefense.components.FollowPathComponent;
import com.gemserk.games.towerofdefense.components.ImageRenderableComponent;
import com.gemserk.games.towerofdefense.components.MovementComponent;
import com.gemserk.games.towerofdefense.components.RemoveWhenNearComponent;
import com.gemserk.games.towerofdefense.components.editor.AddItemComponent;
import com.gemserk.games.towerofdefense.renderers.BarRendererComponent;
import com.gemserk.games.towerofdefense.renderers.DefenseComponent;
import com.gemserk.games.towerofdefense.renderers.EntityRendererComponent;
import com.gemserk.games.towerofdefense.renderers.FollowPathRenderComponent;
import com.gemserk.games.towerofdefense.renderers.RadiusRendererComponent;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TodhComponentLoader implements ResourceLoader {

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