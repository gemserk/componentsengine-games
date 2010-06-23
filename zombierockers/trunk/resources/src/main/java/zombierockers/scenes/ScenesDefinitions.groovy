package zombierockers.scenes

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

class ScenesDefinitions {
	
	static def scenes(def utils){
		// TODO: find better way to define balls, now it is not understandable
		
		def collisionMap = { path ->
			BufferedImage image = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
			def height = image.getHeight()
			def width = image.getWidth()
			return [collides:{x, y ->
				int xint = (int)x
				int yint = (int)y
				if(xint < 0 || xint > width || yint < 0 || yint > height)
					return false
					
				int value = image.getRGB(xint,yint)
				return value == -1 ? true : false
			}]
		}
		
		
		
		
		
		def allBallDefinitions = [
		[type:"red", animation:"ballanimation", color:utils.color(1,0,0)],
		[type:"blue", animation:"ballanimation", color:utils.color(0,0,1)],
		[type:"green", animation:"ballanimation", color:utils.color(0,1,0)],
		[type:"yellow", animation:"ballanimation", color:utils.color(1,1,0)],
		[type:"violet", animation:"ballanimation", color:utils.color(1,0,1)],
		[type:"white", animation:"ballanimation", color:utils.color(1,1,1)],
		]
		
		def ballDefinition = { type ->
			allBallDefinitions.find({it.type == type})
		}
		
		def ballDefinitions = {types ->
			def definitions = [:]
			types.each {type -> definitions[type] = ballDefinition(type) }
			return definitions
		}
		
		
		def level01 = [background:"level01", path:"levels/level01/path.svg", ballsQuantity:40, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:700f, minSpeedFactor:0.2f, maxSpeed:0.04f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green"])
				]
		
		def level02 = [background:"level02", path:"levels/level02/path.svg",ballsQuantity:60, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:800f, minSpeedFactor:0.2f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green"])
				]	
		
		def level03 = [background:"level03", path:"levels/level03/path.svg",ballsQuantity:80, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:800f, minSpeedFactor:0.2f, maxSpeed:0.04f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green"])
				]			
		
		def level04 = [background:"level04", path:"levels/level04/path.svg",ballsQuantity:100, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green","white"])
				]		
		
		def level05 = [background:"level05", path:"levels/level05/path.svg",ballsQuantity:100, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green","white"])
				]	
		
		def level06 = [background:"level06", path:"levels/level06/path.svg",ballsQuantity:100, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green","white"]),
				placeables:[[image:"level06-tunnel",position:utils.vector(448,439),layer:10]],
				collisionMap:collisionMap("levels/level06/collisionMap.png")
				]	
		def levels = [level06,level01, level02, level03, level04, level05]
		return levels
	}
	
	
}
