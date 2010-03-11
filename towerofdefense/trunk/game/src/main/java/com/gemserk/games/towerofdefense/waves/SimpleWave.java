package com.gemserk.games.towerofdefense.waves;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;

public class SimpleWave implements Wave {

	final int rate;
	final int quantity;
	final InstantiationTemplate instantiationTemplate;
	int timeToNext;
	int quantityLeft;
	boolean started = false;

	public SimpleWave(int rate, int quantity, InstantiationTemplate instantiationTemplate) {
		this.rate = rate;
		this.quantity = quantity;
		this.instantiationTemplate = instantiationTemplate;
		this.timeToNext = rate;
		this.quantityLeft = quantity;
	}

	public InstantiationTemplate generateTemplates(int delta) {
		if(!started)
			return null;
		
		if(quantityLeft==0)
			return null;
		
		
		timeToNext -=delta;
		if(timeToNext > 0)
			return null;
		
		
		
		quantityLeft --;
		timeToNext +=rate;
		return instantiationTemplate;
	}

	public void start() {
		this.started = true;		
	}
	
	@Override
	public boolean isDone() {
			return quantityLeft == 0;
	}

}
