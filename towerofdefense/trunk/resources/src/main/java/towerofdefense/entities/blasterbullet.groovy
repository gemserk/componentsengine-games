package towerofdefense.entities;

import com.gemserk.componentsengine.commons.components.ComponentFromListOfClosures;
import com.gemserk.componentsengine.messages.UpdateMessage;

builder.entity {
	
	parent("towerofdefense.entities.bullet", parameters)
	
	property("sound", utils.resources.sounds.sound("towerofdefense.sounds.blasterbullet"))
	property("alreadySounded", false)
	
	component(new ComponentFromListOfClosures("sounder when create", [{UpdateMessage message ->
		if (!entity.alreadySounded)
			entity.sound.play(1.0f, 0.3f)
	}]
	))
}
