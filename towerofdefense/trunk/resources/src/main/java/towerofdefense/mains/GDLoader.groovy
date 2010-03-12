package towerofdefense.mains


import java.io.StringWriter;

import groovy.util.XmlSlurper;

class GDLoader {
	public static void main(String[] args) {
		9.times {
			def path = "/home/ruben01/Desktop/geoDefense/geoDefense.app/GAME_LEVEL_E_000${it+1}.xml"
			try{
				def scene =  parseScene(path)
				//println scene
				println transformScene(scene)
			}catch(Exception e){
				println "Error al parsear $path"
				e.printStackTrace(System.out)
			}
		}
		
	}
	
	static def transformScene(def scene){
		StringWriter stringWriter = new StringWriter(1000)
		
		stringWriter.print """
package towerofdefense.scenes;

builder.entity("world") {
		
	def utils = utils
	def sceneBuilder = new TowerOfDefenseSceneBuilder(utils)
	def builtParameters = sceneBuilder."""
		def dsl = new DSLPrinter(stringWriter)
		
		dsl.out.setIndentLevel 2
		
		dsl.scene(scene.basicInfo){
			path(minX:0,minY:30){
				scene.path.each { 
					point(it.x,it.y)
				}
			}
			critters(scene.creeps.parameters){
				scene.creeps.creeps.each { critter(it) }
			}
			waves(scene.waves.parameters){
				scene.waves.waves.each { spawnList ->
					if(spawnList.size() == 1){
						wave(spawnList[0])
					}
					else {
						wave {
							spawnList.each { wave(it) }
						}
					}
					
				}
			}
			towers {
				scene.towers.each { tower(it) }
			}
		}
		
		
		dsl.flush()
		
		stringWriter.println """
	builtParameters.sceneScript = this.getClass().getName()
	
	parent("towerofdefense.scenes.game", builtParameters)		
}"""
		
		return stringWriter.getBuffer().toString()
		
	}
	
	
	static def parseScene(def path){
		def gamelevel = new XmlSlurper().parse(new File(path))
		def scene = [:]
		def basicInfo = [:]
		scene.basicInfo = basicInfo
		gamelevel.info.with {
			basicInfo["name"] = it.@name.text()
			basicInfo["money"] = it.@initCash.text()
			basicInfo["lives"] = it.@initLives.text()
		}
		
		scene.path = []
		gamelevel.creepPath.children().each{point ->
			
			def invertpoint = { data ->
				return [x:(float)(800f - (1.7f*data["y"])), y:(float)(600f - (1.5f*data["x"]))]
			}
			
			scene.path << invertpoint([x:Float.parseFloat(point.@x.text()), y:Float.parseFloat(point.@y.text())])
		}
		
		scene.creeps = [:]
		scene.creeps.parameters = [:]
		gamelevel.creeps.with { creeps ->
			def mapping = [ waveSpeedFactor:"speedFactor",waveWealthFactor:"rewardFactor",waveHealthFactor:"healthFactor"]
			mapping.each { key, newKey ->
				def value = creeps."@$key".text()
				if(value != null && !value.equals("")){
					scene.creeps.parameters[(newKey)]=[Float.parseFloat(value)]			
				}
			}
			
			scene.creeps.creeps = []
			creeps.children().each {creep ->
				def creepType = creep.@type.text().toLowerCase()
				scene.creeps.creeps << [type:creepType, id:creepType,speed:Float.parseFloat(creep.@speed.text()),health:Float.parseFloat(creep.@health.text())]
			}
		}
		
		scene.waves = [:]
		gamelevel.creepWaves.with { creepWaves ->
			def parameters = scene.waves.parameters = [:]
			def names = ["delayBetweenWaves","delayBetweenSpawns"]			                                           
			names.each{ name ->
				parameters[(name)]=Float.parseFloat(creepWaves."@$name".text())
			}
			
			def wavesList = scene.waves.waves = []
			creepWaves.wave.each {wave ->
				def spawns = []
				wave.spawn.each { spawn ->
					spawns << [id:spawn.@type.text().toLowerCase(),count:Integer.parseInt(spawn.@count.text())]
				}
				wavesList << spawns
			}
		}
		
		def towers = scene.towers = []
		gamelevel.towers.tower.each { tower ->
			towers << [type:tower.@type.text().toLowerCase(),cost:Float.parseFloat(tower.@cost.text())]
		}
		
		return scene
		
	}
	
	
	
}
