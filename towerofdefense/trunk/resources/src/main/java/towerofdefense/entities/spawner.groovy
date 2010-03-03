package towerofdefense.entities;

import com.gemserk.games.towerofdefense.components.WavesSpawnerComponent;
import com.gemserk.games.towerofdefense.waves.Waves;

builder.entity {
	
	tags("spawner")
	
	property("position", parameters.position)
	
	component(new WavesSpawnerComponent("creator")){
		property("waves", parameters.waves)
		propertyRef("position", "position")
		property("entity", {entity.parent})
	}
	
	genericComponent(id:"nextWaveHandler", messageId:"nextWave"){ message ->	
		Waves waves = entity."creator.waves"
		if(!waves.isLastWaveStarted())
			waves.nextWave()	
	}
	
}
