package dosdewinia.scenes
import com.gemserk.componentsengine.predicates.EntityPredicates;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 

import com.gemserk.componentsengine.predicates.EntityPredicates;
import com.gemserk.componentsengine.render.ClosureRenderObject 
import org.newdawn.slick.Graphics 
import org.newdawn.slick.Input;

import com.gemserk.componentsengine.commons.components.ExplosionComponent 
import com.gemserk.componentsengine.commons.components.ImageRenderableComponent 
import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplateImpl 
import com.gemserk.componentsengine.messages.ChildrenManagementMessageFactory 
import com.gemserk.games.dosdewinia.Target;
import com.google.common.base.Predicates 

builder.entity {
	
	
	def traversableColor = utils.color(1,1,1,1)
	def traversable = { terrainMap, position ->
		def x = (int)position.x
		def y = (int)position.y
		if(x < 0 || y < 0 || x > terrainMap.width || y > terrainMap.height)
			return false
		
		def terrainColor = terrainMap.getColor((int)position.x, (int)position.y)
		return terrainColor == traversableColor
	}
	
	
	property("bounds",utils.rectangle(0,0,800,600))
	property("terrainMap",utils.resources.image("terrainMap"))
	
	component(new ImageRenderableComponent("imagerenderer")) {
		propertyRef("image", "terrainMap")
		property("color", utils.color(1,1,1,1))
		property("position", utils.vector(400,300))
		property("direction", utils.vector(1,0))
		property("layer", -1000)
	}
	
	
	
	
	
	component(utils.components.genericComponent(id:"resumeGameHandler", messageId:"resumeGame"){ message ->
		messageQueue.enqueue(utils.genericMessage("resume"){})	
	})
		
	input("inputmapping"){
		keyboard {
			press(button:"return",eventId:"resumeGame")
			press(button:"space",eventId:"resumeGame")
			press(button:"escape",eventId:"resumeGame")
		}
	}
}