package towerofdefense.entities;
import com.gemserk.games.towerofdefense.PathRendererComponent;


builder.entity {
	
	tags("path")
	
	property("path", parameters.path)
	
	component(new PathRendererComponent("pathrenderer")){
		property("lineColor", parameters.lineColor)
		propertyRef("path", "path")		
	}
	
	
	
}

