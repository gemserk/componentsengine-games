package towerofdefense.scenes

import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.newdawn.slick.geom.Vector2f;
import com.gemserk.componentsengine.builders.BuilderUtils;
import com.gemserk.games.towerofdefense.ChainedValueFromClosure;
import com.gemserk.games.towerofdefense.InstantiationTemplate;
import com.gemserk.games.towerofdefense.Path 
import com.gemserk.games.towerofdefense.waves.CompositeWave;
import com.gemserk.games.towerofdefense.waves.SimpleWave;
import com.gemserk.games.towerofdefense.waves.Wave;

public class TowerOfDefenseSceneBuilder {
	
	Map<String,Object> sceneParameters = new HashMap<String, Object>();
	CrittersDefinition crittersDefinition = new CrittersDefinition(utils)
	TowersDefinitions towersDefinitions = new TowersDefinitions(utils);
	
	BuilderUtils utils;
	
	Map<String, InstantiationTemplate> critters
	
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
	
	
	public void critters(Closure closure){
		CritterBuilder critterBuilder = new CritterBuilder();
		critterBuilder.crittersDefinition = crittersDefinition
		closure.setDelegate(critterBuilder);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call();
		critters = critterBuilder.critters
	}
	
	public class CritterBuilder{
		CrittersDefinition crittersDefinition
		Map<String,Object> critters = new HashMap<String, Object>();
		
		public void critter(Map<String,Object> parameters){
			def type = parameters.type.toString()
			parameters.remove("type")
			def id = parameters.id ?: type
			parameters.remove("id")
			
			def critter = crittersDefinition.critter(type)
			def innerGP = critter.genericProvider
			critter.genericProvider = new ChainedValueFromClosure(innerGP, {params, entity->
				params.putAll(parameters)
				return params
			});
			
			critters.put(id, critter)
		}
		
	}
	
	
	
	
	public void waves(Closure closure){
		WaveBuilder waveBuilder = new WaveBuilder();
		waveBuilder.critters = critters
		closure.setDelegate(waveBuilder);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call();
		def waves = waveBuilder.getWaves()
		sceneParameters.waves = waves
	}
	
	class WaveBuilder{
		List<Wave> waves = new ArrayList<Wave>();
		Map<String,InstantiationTemplate> critters
		
		
		
		public void wave(Map<String, Object> parameters){
			Wave wave = new SimpleWave(parameters.rate, parameters.quantity, critters.get(parameters.id))
			waves.add(wave)
		}
		
		public void wave(Closure closure){
			WaveBuilder waveBuilder = new WaveBuilder();
			waveBuilder.critters = critters
			closure.setDelegate(waveBuilder);
			closure.setResolveStrategy(Closure.DELEGATE_FIRST)
			closure.call();
			CompositeWave wave = new CompositeWave(waveBuilder.getWaves())
			waves.add(wave);
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
