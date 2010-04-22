package com.gemserk.games.towerofdefense.springmesh;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.SlickCallable;

import com.gemserk.componentsengine.annotations.EntityProperty;
import com.gemserk.componentsengine.commons.components.FieldsReflectionComponent;
import com.gemserk.componentsengine.messages.SlickRenderMessage;
import com.gemserk.componentsengine.messages.UpdateMessage;

public class SpringMeshComponent extends FieldsReflectionComponent {

	@EntityProperty
	SpringMesh springMesh;

	@EntityProperty 
	QuadMesh2d quadMesh2d;
	
	public void setSpringMesh(SpringMesh springMesh) {
		this.springMesh = springMesh;
	}
	
	public void setQuadMesh2d(QuadMesh2d quadMesh2d) {
		this.quadMesh2d = quadMesh2d;
	}

	public SpringMeshComponent(String id) {
		super(id);
	}

	public void handleMessage(UpdateMessage updateMessage) {
		int i = 0;
		int delta = updateMessage.getDelta();
		springMesh.update(delta);
		for (SpringMeshPoint springMeshPoint : springMesh.getSpringMeshPoints()) {
			springMeshPoint.update(delta);
			Vector2f position = springMeshPoint.getPosition();
			quadMesh2d.setPoint(i++, position.x, position.y);
		}
	}

	public void handleMessage(SlickRenderMessage message) {
		SlickCallable.enterSafeBlock();
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		quadMesh2d.render();
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		SlickCallable.leaveSafeBlock();
	}
}
