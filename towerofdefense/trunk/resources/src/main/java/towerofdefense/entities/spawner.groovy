package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent 
import com.gemserk.games.towerofdefense.components.WavesSpawnerComponent;
import com.gemserk.games.towerofdefense.waves.Waves;

builder.entity {
	
	tags("spawner")
	
	property("position", parameters.position)
	
	component(new CircleRenderableComponent("circlerenderer")){
		property("radius", 10.0f)
		propertyRef("position", "position")
	}
	
	component(new WavesSpawnerComponent("creator")){
		property("waves", parameters.waves)
		propertyRef("position", "position")
	}
	
	genericComponent(id:"nextWaveHandler", messageId:"nextWave"){ message ->	
		Waves waves = entity."creator.waves"
		if(!waves.isLastWaveStarted())
			waves.nextWave()	
	}
	
}
