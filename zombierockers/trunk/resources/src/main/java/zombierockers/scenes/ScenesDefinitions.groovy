package zombierockers.scenes

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.gemserk.games.zombierockers.SubPathDefinition;

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
				return value == -1 ? 1 : 0
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
		
		
		def segmentsMetadataConvertor = { elements, defaultElement -> 
			return [getSubPathDefinition: { pathTraversal ->
				def distanceFromOrigin = pathTraversal.distanceFromOrigin
				def element = elements.find { it.start <= distanceFromOrigin && it.end > distanceFromOrigin }
				return element ?: defaultElement
			}]
		}
		
		def subPathDefinition = { start, end, metadata -> 
			return new SubPathDefinition(start, end, metadata)
		}
		
		def defaultElement = subPathDefinition(0, 100000, [layer:10, collisionMask:1])
		
		def level01 = [background:"level01", path:"levels/level01/path.svg", ballsQuantity:40, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:700f, minSpeedFactor:0.2f, maxSpeed:0.04f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green"]),
				placeables:[],
				subPathDefinitions:segmentsMetadataConvertor([], defaultElement)
				]
		
		def level02 = [background:"level02", path:"levels/level02/path.svg",ballsQuantity:60, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:800f, minSpeedFactor:0.2f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green"]),
				placeables:[],
				subPathDefinitions:segmentsMetadataConvertor([], defaultElement)
				]	
		
		def level03 = [background:"level03", path:"levels/level03/path.svg",ballsQuantity:80, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:800f, minSpeedFactor:0.2f, maxSpeed:0.04f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green"]),
				placeables:[],
				subPathDefinitions:segmentsMetadataConvertor([], defaultElement)
				]			
		
		def level04 = [background:"level04", path:"levels/level04/path.svg",ballsQuantity:100, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green","white"]),
				placeables:[],
				subPathDefinitions:segmentsMetadataConvertor([], defaultElement)
				]		
		
		def level05 = [background:"level05", path:"levels/level05/path.svg",ballsQuantity:100, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green","white"]),
				placeables:[],
				subPathDefinitions:segmentsMetadataConvertor([], defaultElement)
				]	
		
		def level06 = [background:"level06", path:"levels/level06/path.svg",ballsQuantity:100, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green","white"]),
				placeables:[],
				//placeables:[[image:"level06-tunnel",position:utils.vector(448,439),layer:10]],
				//collisionMap:collisionMap("levels/level06/collisionMap.png"),
				subPathDefinitions:segmentsMetadataConvertor([], defaultElement)
				]	
		
		def level07 = [background:"level07", path:"levels/level07/path.svg",ballsQuantity:100, 
				pathProperties:[speed:0.04f, acceleratedSpeed:0.5f, accelerationStopPoint:1300f, minSpeedFactor:0.3f, maxSpeed:0.05f, speedWhenReachBase:0.4f],
				ballDefinitions:ballDefinitions(["red","blue","green","white"]),
				placeables:[[image:"level07-tunnel",position:utils.vector(305f,206f),layer:-2000]],
				//collisionMap:collisionMap("levels/level07/collisionMap.png"),
				subPathDefinitions:segmentsMetadataConvertor([subPathDefinition(1374.4f,1639.9f, [layer:-1500, collisionMask:7]) ], defaultElement)
				]	
				
		def levels = [level01, level02, level03, level04, level05,level07,level06]
		return levels
	}
	
	
}
