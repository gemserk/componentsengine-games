package towerofdefense.entities;

import com.gemserk.games.towerofdefense.components.WavesSpawnerComponent 
import com.gemserk.games.towerofdefense.waves.Waves 


builder.entity {
	
	tags("spawner")
	
	property("position", parameters.position)
	property("direction",parameters.direction) //it is used as starting direction for spawned critters
	
	property("sound", utils.resources.sounds.sound("towerofdefense.sounds.nextwave"))
	
	component(new WavesSpawnerComponent("creator")){
		property("waves", parameters.waves)
		propertyRef("position", "position")
		property("entity", {entity.parent})
	}
	
	component(utils.components.genericComponent(id:"nextWaveHandler", messageId:"nextWave"){ message ->	
		Waves waves = entity."creator.waves"
		if(!waves.isLastWaveStarted()) {
			waves.nextWave()
			entity.sound.play()
		}
	})
	
}
