package com.gemserk.games.towerofdefense;

import groovy.lang.Closure;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
public class GroovyClosureRunner {

	AtomicReference<FutureTask> futureTaskReference = new AtomicReference<FutureTask>();

	public Object execute(final Closure closure) throws InterruptedException, ExecutionException {
		FutureTask futureTask = new FutureTask(new Callable() {

			@Override
			public Object call() throws Exception {
				return closure.call();
			}

		});
		
		futureTaskReference.set(futureTask);
		
		return futureTask.get();
	}
	
	
	public void process(){
		FutureTask futureTask = futureTaskReference.getAndSet(null);
		if(futureTask!= null)
			futureTask.run();
	}
	
	
}

