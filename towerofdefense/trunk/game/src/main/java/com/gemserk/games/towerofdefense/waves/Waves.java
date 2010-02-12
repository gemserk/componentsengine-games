package com.gemserk.games.towerofdefense.waves;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.gemserk.games.towerofdefense.InstantiationTemplate;

public class Waves {

	List<Wave> waves = new ArrayList<Wave>();

	int currentWave = 0;

	public Waves setWaves(List<Wave> waves) {
		this.waves = waves;
		return this;
	}

	public List<InstantiationTemplate> generateTemplates(int delta) {
		List<InstantiationTemplate> instantiationTemplates = new LinkedList<InstantiationTemplate>();
		for (Wave wave : waves) {
			InstantiationTemplate instantiationTemplate = wave.generateTemplates(delta);
			if (instantiationTemplate != null)
				instantiationTemplates.add(instantiationTemplate);
		}
		return instantiationTemplates;
	}


	public int getCurrent() {
		return currentWave;
	}

	public int getTotal() {
		return waves.size();
	}

	public void nextWave() {

		if (isLastWaveStarted())
			throw new RuntimeException("no next wave available");
		
		currentWave++;
		waves.get(currentWave - 1).start();

	}

	public boolean isLastWaveStarted() {
		return currentWave == getTotal();
	}

}
