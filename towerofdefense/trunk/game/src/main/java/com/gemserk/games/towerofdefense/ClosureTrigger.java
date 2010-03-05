package com.gemserk.games.towerofdefense;

import groovy.lang.Closure;

public class ClosureTrigger implements Trigger {

	Closure closure;

	public ClosureTrigger(Closure closure) {
		this.closure = closure;
	}

	@Override
	public void trigger(Object... parameters) {
		closure.call(parameters);
	}

	@Override
	public void trigger() {
		trigger(new Object[] {});
	}

}
