/**
 * 
 */
package com.gemserk.games.towerofdefense.waves;

import java.util.List;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;

public class CompositeWave implements Wave{

	private final List<Wave> innerWaves;
	int currentWave = 0;
	
	public CompositeWave(List<Wave> innerWaves) {
		this.innerWaves = innerWaves;
	}

	@Override
	public InstantiationTemplate generateTemplates(int delta) {
		if(isDone())
			return null;
		Wave wave = innerWaves.get(currentWave);
		InstantiationTemplate template =  wave.generateTemplates(delta);
		
		if(wave.isDone())
			currentWave++;
		
		return template;
	}

	@Override
	public void start() {
		for (Wave wave : this.innerWaves) {
			wave.start();
		}
	}

	@Override
	public boolean isDone() {
		return currentWave >= innerWaves.size();
	}
	
}