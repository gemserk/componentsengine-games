package com.gemserk.games.towerofdefense;

import groovy.lang.Closure;

public class ValueFromClosure implements GenericProvider {
	
	Closure closure;
	
	public ValueFromClosure(Closure closure) {
		this.closure = closure;	
	}
		
	public <T> T get(){
		return get(new Object[] { });
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object... objects) {
		return (T) closure.call(objects);
	}

}
