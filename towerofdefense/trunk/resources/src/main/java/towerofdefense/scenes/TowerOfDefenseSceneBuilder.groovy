package towerofdefense.scenes

import groovy.lang.Closure;
import groovy.util.Expando;

import java.util.Map;
import javax.management.RuntimeErrorException 
import org.newdawn.slick.geom.Vector2f 

import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.componentsengine.commons.components.Path 
import com.gemserk.componentsengine.genericproviders.ChainedValueFromClosure 

public class TowerOfDefenseSceneBuilder {
	
	Map<String,Object> sceneParameters = new HashMap<String, Object>();
	CrittersDefinition crittersDefinition = new CrittersDefinition(utils)
	TowersDefinitions towersDefinitions = new TowersDefinitions(utils);
	
	BuilderUtils utils;
	
	Map<String, Closure> critters
	
	public TowerOfDefenseSceneBuilder(BuilderUtils utils) {
		this.utils = utils;
	}
	
	
	public Map<String,Object> scene(Map<String,Object> parameters, Closure closure){
		sceneParameters.putAll(parameters);
		closure.setDelegate(this);
		closure.call();
		return sceneParameters;
	}
	
	public void path(Map<String, Object> parameters, Closure closure){
		def minX = parameters.minX ?: 0f
		def minY = parameters.minY ?: 0f
				
		path(minX,minY,closure)
		
	}
	
	public void path(final float minX, final float minY, Closure closure){
		final List<Vector2f> points = new ArrayList<Vector2f>();
		
		def pathBuilder = new Expando()
		pathBuilder.point = {x, y ->
			points.add(new Vector2f((float)(minX + x), (float)(minY + y)));
		}
		
		closure.setDelegate(pathBuilder)
		closure.call();
		sceneParameters.path = new Path(points);
		
	}
	
	
	public void critters(def parameters, Closure closure){
		def multipliers = [speed:[0f], health:[1f], reward:[1f]]
		parameters.each {key, value ->
			if(key.endsWith("Factor")) {
				multipliers[key.replaceAll("Factor","")] = value.collect { (float)it}
			}
		}
		
		def critterBuilder = newCritterBuilder(multipliers)
		closure.setDelegate(critterBuilder);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call();
		critters = critterBuilder.definedCritters
	}
	
	def newCritterBuilder(def multipliers){
		def critterBuilder = new Expando()
		critterBuilder.definedCritters = [:]
		critterBuilder.critter = { Map<String,Object> parameters ->
			def type = parameters.type.toString()
			parameters.remove("type")
			def id = parameters.id ?: type
			parameters.remove("id")
			
			def critterProvider = { int waveNumber ->
				def critter = crittersDefinition.critter(type)
				def innerGP = critter.genericProvider
				critter.genericProvider = new ChainedValueFromClosure(innerGP, {params, entity->
					params.putAll(parameters)
					
					multipliers.each { key, multipliersValues ->
						def multiplier = multipliersValues[0]
						def originalValue = (Float)params[(key)]
						def newValue = originalValue*(multiplier*waveNumber+1)                                  
						params[(key)] = (Float)(Math.floor(newValue))
					}
					return params
				});
				return critter;
			}
			definedCritters.put(id, critterProvider)
		}
		return critterBuilder
	}
	
	public void waves(def parameters, Closure closure){
		def wavePeriod = parameters.delayBetweenWaves
		if(wavePeriod==null)
			throw new RuntimeErrorException("must define delayBetweenWaves")
		
		sceneParameters.wavePeriod = wavePeriod
		
		def delayBetweenSpawns = parameters.delayBetweenSpawns
		WaveBuilder waveBuilder = new WaveBuilder(critters, delayBetweenSpawns);
		closure.setDelegate(waveBuilder);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		
		closure.call();
		def waves = waveBuilder.getWaves()
		
		sceneParameters.waves = waves
	}
	

	
	
	public void towers(Closure closure){
		def towerBuilder = newTowerBuilder()
		closure.setDelegate(towerBuilder)
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call();
		sceneParameters.put("towerDescriptions",towerBuilder.towers)	
	}
	
	public Expando newTowerBuilder(){
		Expando towerBuilder = new Expando();
		towerBuilder.towers = new LinkedHashMap<String, Map<String,Object>>();
		towerBuilder.tower = { Map<String,Object> parameters ->
			def type = parameters.type.toString()
			parameters.remove("type")
			
			Map<String, Object> towerDefinition = towersDefinitions.tower(type);
			towerDefinition.putAll(parameters);
			towers.put(type, towerDefinition);
		}
		return towerBuilder
	}
}
