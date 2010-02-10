package towerofdefense.scenes;

builder.scene("todh.scenes.scene1") {
	
	images("assets/images.properties")
	
	input("playerInputMapping","towerofdefense.input.inputmapping")
	
	//	controller(com.gemserk.games.towerofdefense.TowerOfDefenseController.class)
	
	components(com.gemserk.games.towerofdefense.TowerOfDefenseComponentLoader.class);
	
	entity(template:"towerofdefense.entities.path", id:"path")	{
		path=[
		utils.vector(0, 300), 		      
		utils.vector(670, 300)		
		]
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
	}

	entity(template:"towerofdefense.entities.base", id:"base")	{
		position=utils.vector(700,300)
		direction=utils.vector(-1,0)
		radius=30f
			
		lineColor=utils.color(0.0f, 0.0f, 0.0f, 1.0f)
		fillColor=utils.color(0.0f, 0.0f, 0.0f, 0.2f)
	}

	//	entity(template:"towerofdefense.entities.tower", id:"tower3")	{
	//		position=utils.vector(700, 260)
	//		direction=utils.vector(-1, 0)
	//		color=utils.color(0.0f, 1.0f, 0.0f, 1.0f)
	//		radius=80.0f
	//		template="towerofdefense.entities.bullet"
	//		reloadTime=1000
	//		damage=25.0f
	//	}
	
}