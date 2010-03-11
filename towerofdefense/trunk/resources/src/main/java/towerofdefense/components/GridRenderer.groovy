package towerofdefense.components

import com.gemserk.componentsengine.components.ReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import org.newdawn.slick.Color 
import org.newdawn.slick.Graphics 
import org.newdawn.slick.geom.Line 
import org.newdawn.slick.geom.Rectangle 

class GridRenderer extends ReflectionComponent {
	
	public GridRenderer(String id) {
		super(id)
	}
	
	@Override
	public void handleMessage(SlickRenderMessage message) {
		Graphics g = message.graphics
		Rectangle bounds = entity."${id}.bounds"
		Color pColor = g.getColor();
		Integer distance = entity."${id}.distance"
		g.setColor(new Color(0.1f,0.1f,0.2f,1))
		g.pushTransform();
		Line line = new Line(bounds.x,bounds.y,bounds.maxX,bounds.y)
		((bounds.maxY / (3*distance))+1).times { number ->
			g.setLineWidth(3);
			g.draw(line);
			g.translate(0,distance)
			
			g.setLineWidth(1);
			g.draw(line);
			g.translate(0,distance)
			
			g.setLineWidth(1);
			g.draw(line);
			g.translate(0,distance)
		}
		
		g.popTransform();
		g.pushTransform();
		line = new Line(bounds.x,bounds.y,bounds.x,bounds.maxY)
		((bounds.maxX / (3*distance))+1).times { number ->
			g.setLineWidth(3);
			g.draw(line);
			g.translate(distance,0)
			
			g.setLineWidth(1);
			g.draw(line);
			g.translate(distance,0)
			
			g.setLineWidth(1);
			g.draw(line);
			g.translate(distance,0)
		}
		
		g.popTransform();
		
		g.setColor(pColor)
	}
}
