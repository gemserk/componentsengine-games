package towerofdefense.scenes

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.RuntimeErrorException 
import org.newdawn.slick.geom.Vector2f;
import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.games.towerofdefense.ChainedValueFromClosure;
import com.gemserk.games.towerofdefense.Path 
import com.gemserk.games.towerofdefense.waves.CompositeWave;
import com.gemserk.games.towerofdefense.waves.SimpleWave;
import com.gemserk.games.towerofdefense.waves.Wave;

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
	
	public void path(final float minX, final float minY, Closure closure){
		final List<Vector2f> points = new ArrayList<Vector2f>();
		
		closure.setDelegate(new Object(){
					public void point(float x, float y){
						points.add(new Vector2f((float)minX + x, (float)minY + y));
					}
				});
		closure.call();
		sceneParameters.path = new Path(points);
		
	}
	
	
	public void critters(def parameters, Closure closure){
		def multipliers = [:]
		parameters.each {key, value ->
			if(key.endsWith("Factor")) {
				multipliers[key.replaceAll("Factor","")] = value
			}
		}
		
		CritterBuilder critterBuilder = new CritterBuilder();
		critterBuilder.crittersDefinition = crittersDefinition
		critterBuilder.multipliers = multipliers
		closure.setDelegate(critterBuilder);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call();
		critters = critterBuilder.critters
	}
	
	public class CritterBuilder{
		CrittersDefinition crittersDefinition
		def multipliers
		Map<String,Object> critters = new HashMap<String, Object>();
		
		
		public void critter(Map<String,Object> parameters){
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
						def originalValue = (Float)params[(key)]
						waveNumber.times {
							originalValue = originalValue * multipliersValues[0]
						}
						params[(key)] = (Float)originalValue
					}
					return params
				});
				return critter;
			}
			critters.put(id, critterProvider)
		}
		
	}
	
	
	public void waves(def parameters, Closure closure){
		def wavePeriod = parameters.delayBetweenWaves
		if(wavePeriod==null)
			throw new RuntimeErrorException("must define delayBetweenWaves")
		
		sceneParameters.wavePeriod = wavePeriod
		
		def delayBetweenSpawns = parameters.delayBetweenSpawns
		
		
		
		
		WaveBuilder waveBuilder = new WaveBuilder();
		waveBuilder.critters = critters
		waveBuilder.defaultDelayBetweenSpawns = delayBetweenSpawns
		closure.setDelegate(waveBuilder);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		
		closure.call();
		def waves = waveBuilder.getWaves()
		
		sceneParameters.waves = waves
	}
	
	
	class WaveBuilder{
		List<Wave> waves = new ArrayList<Wave>();
		Map<String,Closure> critters
		
		boolean secondLevel = false;
		def waveNumber = 0
		def defaultDelayBetweenSpawns
		
		public void addWave(Wave wave){
			waves.add(wave)
			if(!secondLevel){
				waveNumber++
			}
		}
		
		
		public void wave(Map<String, Object> parameters){
			def rate = parameters.rate ?: defaultDelayBetweenSpawns
			if(rate == null)
				throw new RuntimeException("if there is no delayBetweenSpawns defined you must define rate for every wave")
			
			def critterId = parameters.id
			def critterProvider = critters[(critterId)]
			def instantiationTemplate = critterProvider(waveNumber)
			Wave wave = new SimpleWave(rate, parameters.quantity, instantiationTemplate )
			addWave(wave)
		}
		
		public void wave(Closure closure){
			if(secondLevel)
				throw new RuntimeException("Only one level of nested waves")
			secondLevel = true
			WaveBuilder waveBuilder = new WaveBuilder()
			waveBuilder.secondLevel = true
			waveBuilder.waveNumber = waveNumber
			waveBuilder.critters = critters
			waveBuilder.defaultDelayBetweenSpawns = defaultDelayBetweenSpawns
			closure.setDelegate(waveBuilder);
			closure.setResolveStrategy(Closure.DELEGATE_FIRST)
			closure.call();
			CompositeWave wave = new CompositeWave(waveBuilder.getWaves())
			secondLevel = false
			addWave(wave)
		}
		
		public List<Wave> getWaves() {
			return waves;
		}
	}
	
	public void towers(Closure closure){
		def towerBuilder = new TowerBuilder()
		towerBuilder.towersDefinitions = towersDefinitions
		closure.setDelegate(towerBuilder)
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call();
		sceneParameters.put("towerDescriptions",towerBuilder.towers)
	}
	
	public class TowerBuilder {
		TowersDefinitions towersDefinitions
		Map<String, Map<String,Object>> towers = new HashMap<String, Map<String,Object>>();
		
		public void tower(Map<String,Object> parameters){
			def type = parameters.type.toString()
			parameters.remove("type")
			
			Map<String, Object> towerDefinition = this.towersDefinitions.tower(type);
			towerDefinition.putAll(parameters);
			towers.put(type, towerDefinition);
		}
		
	}
	
	
	
	
}
