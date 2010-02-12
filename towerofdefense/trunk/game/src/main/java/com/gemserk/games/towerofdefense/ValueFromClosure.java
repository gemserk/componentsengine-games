package com.gemserk.games.towerofdefense;

import groovy.lang.Closure;

public class ValueFromClosure implements GenericProvider {
	
	Closure closure;
	
	public ValueFromClosure(Closure closure) {
		this.closure = closure;	
	}
		
	/* (non-Javadoc)
	 * @see com.gemserk.games.towerofdefense.GenericProvider#get()
	 */
	public <T> T get(){
		return (T) closure.call();
	}

}
