package com.gemserk.games.towerofdefense;


import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent;
import com.gemserk.componentsengine.commons.components.SuperMovementComponent;
import com.gemserk.componentsengine.components.Component;
import com.gemserk.componentsengine.components.ComponentManager;
import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;
import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.resources.ResourceLoader;
import com.gemserk.componentsengine.scene.BuilderUtils;
import com.gemserk.componentsengine.world.World;
import com.gemserk.games.towerofdefense.components.FaceTargetComponent;
import com.gemserk.games.towerofdefense.components.SelectTargetWithinRangeComponent;
import com.gemserk.games.towerofdefense.components.WavesSpawnerComponent;
import com.gemserk.games.towerofdefense.components.WeaponComponent;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class TowerOfDefenseComponentLoader implements ResourceLoader {

	@Inject
	ComponentManager componentManager;

	@Inject
	Injector injector;

	@Inject
	Input input;

	public static  class InstructionRenderComponent extends ReflectionComponent {
		private InstructionRenderComponent(String id) {
			super(id);
		}

		public void handleMessage(SlickRenderMessage message) {
			Graphics g = message.getGraphics();
			g.pushTransform();
			{
			g.setColor(Color.black);
			g.drawString("Press 'w' to send the next wave", 100,50);
			}
			g.popTransform();
		}
	}

	public static class GroovyConsoleExecutorComponent extends ReflectionComponent{
		@Inject GroovyClosureRunner closureRunner;

		@Inject World world;
		@Inject BuilderUtils utils;
		
		
		
		public GroovyConsoleExecutorComponent(String id) {
			super(id);
		}
		
		public void handleMessage(UpdateMessage message) {
			
			utils.addCustomUtil("world", world);
			utils.addCustomUtil("entitypredicates", new EntityPredicates());
			
			
			
			closureRunner.process();
		}
	}
	

	@Override
	public void load() {

		Component[] components = { new PathRendererComponent("pathrenderer"), // 
				new CircleRenderableComponent("circlerenderer"),// 
				new SuperMovementComponent("movement"),//
				new FollowPathComponent("followpath"),//
				new ImageRenderableComponent("imagerenderer"),//
				new WavesSpawnerComponent("creator"),//
				new FaceTargetComponent("faceTarget"),//
				new SelectTargetWithinRangeComponent("selectTarget"),//
				new WeaponComponent("shooter"),//
				new HitComponent("bullethit"),//
				new InstructionRenderComponent("instructions"),//
				new GroovyConsoleExecutorComponent("groovyconsole")
		};

		for (com.gemserk.componentsengine.components.Component component : components) {
			injector.injectMembers(component);
		}

		componentManager.addComponents(components);

	}
}