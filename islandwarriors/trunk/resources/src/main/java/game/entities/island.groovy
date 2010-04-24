package game.entities

import com.gemserk.componentsengine.commons.components.CircleRenderableComponent;
import com.gemserk.componentsengine.commons.components.IncrementValueComponent 
import com.gemserk.componentsengine.commons.components.LabelComponent 


builder.entity("island-${Math.random()}") {
	
	tags("island")
	
	property("position",parameters.position)
	property("radius",50f)
	
	property("units",10f)
	
	component(new CircleRenderableComponent("image")){
		propertyRef("position","position")
		propertyRef("radius","radius")
//		operties.property(id, "position");
//		radiusProperty = Properties.property(id, "radius");
//		lineColorProperty = Properties.property(id, "lineColor");
//		fillColorProperty = Properties.property(id, "fillColor");
	}
	
	
	component(new IncrementValueComponent("unitsincrementor")) {
		propertyRef("value", "units")
		property("maxValue",10000f)
		property("increment", (float)(1/1000f))
		property("loop",false)
	}
	
	
	
	component(new LabelComponent("units")){
		propertyRef("position","position")
		property("message","{0,number,integer}")
		property("value",{entity.units })
	}
}
