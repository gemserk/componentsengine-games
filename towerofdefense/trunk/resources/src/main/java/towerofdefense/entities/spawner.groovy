package towerofdefense.entities;

import com.gemserk.games.towerofdefense.waves.Waves;

builder.entity {
	
	tags("spawner")
	
	property("position", parameters.position)
	
	component("circlerenderer"){
		property("radius", 10.0f)
		propertyRef("position", "position")
	}
	
	component("creator"){
		property("waves", parameters.waves)
		propertyRef("position", "position")
	}
	
	genericComponent(id:"nextWaveHandler", messageId:"nextWave"){ message ->	
		Waves waves = message.entity."creator.waves"
		if(!waves.isLastWaveStarted())
			waves.nextWave()	
	}
	
}
