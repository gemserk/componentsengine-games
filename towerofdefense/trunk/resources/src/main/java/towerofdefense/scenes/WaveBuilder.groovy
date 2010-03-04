package towerofdefense.scenes

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;


import com.gemserk.games.towerofdefense.waves.CompositeWave 
import com.gemserk.games.towerofdefense.waves.SimpleWave 
import com.gemserk.games.towerofdefense.waves.Wave;

class WaveBuilder{
	List<Wave> waves = new ArrayList<Wave>();
	Map<String,Closure> critters
	
	boolean secondLevel = false;
	def waveNumber = 0
	def defaultDelayBetweenSpawns
	
	def WaveBuilder(def critters, def defaultDelayBetweenSpawns){
		this.critters = critters
		this.defaultDelayBetweenSpawns = defaultDelayBetweenSpawns 
	}
	
	def WaveBuilder(def critters, def defaultDelayBetweenSpawns, def secondLevel, def waveNumber){
		this(critters, defaultDelayBetweenSpawns)
		this.secondLevel = secondLevel
		this.waveNumber = waveNumber
	}
	
	
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
		
		WaveBuilder waveBuilder = new WaveBuilder(critters,defaultDelayBetweenSpawns, true, waveNumber)
		closure.setDelegate(waveBuilder);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call();
		CompositeWave wave = new CompositeWave(waveBuilder.getWaves())
		addWave(wave)
	}
	
	public List<Wave> getWaves() {
		return waves;
	}
}
