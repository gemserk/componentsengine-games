package dudethatsmybullet.scenes

class ScenesDefinitions {
	
	static def scenes(def utils){
		
		def level01 = [
			turrets: [
					[position:utils.vector(200,300)],
					[position:utils.vector(600,300)],
					[position:utils.vector(400,150)],
					[position:utils.vector(400,450)],
				]
		]
		
		def level02 = [
			turrets: [
					[position:utils.vector(200,150)],
					[position:utils.vector(200,450)],
					[position:utils.vector(600,450)],
					[position:utils.vector(600,150)],
				]
		]
		
		
		def levels = [level01,level02]
		return levels
	}
	
	
}
