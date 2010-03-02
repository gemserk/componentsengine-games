package com.gemserk.games.towerofdefense;

import org.junit.Test;

import com.gemserk.componentsengine.templates.*;

public class InstantiationTemplateImplTest {

	@Test
	public void test() { 
		
		EntityTemplate entityTemplate = new DefaultEntityTemplate();
		
		new InstantiationTemplateImpl(entityTemplate, new GenericProvider() {
			
			@Override
			public <T> T get(Object... objects) {
				return null;
			}
			
			@Override
			public <T> T get() {
				return null;
			}
		});
		
	}
	
}
