package com.gemserk.games.towerofdefense.waves;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gemserk.games.towerofdefense.instantiationTemplates.InstantiationTemplate;

@RunWith(JMock.class)
public class WaveTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private InstantiationTemplate instantiationTemplate;

	@Before
	public void setUp(){
		instantiationTemplate = mockery.mock(InstantiationTemplate.class);
	}
	
	@Test
	public void test10(){
		int rate = 100;
		int quantity = 10;
		SimpleWave wave = new SimpleWave(rate,quantity,instantiationTemplate);
		assertThat(wave.timeToNext, equalTo(rate));
		InstantiationTemplate returnedTemplate = wave.generateTemplates(1000);
		assertThat(returnedTemplate,nullValue());
		assertThat(wave.timeToNext, equalTo(rate));
	}
		
	@Test
	public void test15(){
		int rate = 100;
		int quantity = 10;
		Wave wave = new SimpleWave(rate,quantity,instantiationTemplate);
		wave.start();
		InstantiationTemplate returnedTemplate = wave.generateTemplates(20);
		assertThat(returnedTemplate,nullValue());
	}
	
	@Test
	public void test17(){
		int rate = 100;
		int quantity = 10;
		SimpleWave wave = new SimpleWave(rate,quantity,instantiationTemplate);
		wave.start();
		InstantiationTemplate returnedTemplate = wave.generateTemplates(120);
		assertThat(returnedTemplate,notNullValue());
		assertThat(wave.quantityLeft, equalTo(9));
	}
	
	@Test
	public void test19(){
		int rate = 100;
		int quantity = 0;
		Wave wave = new SimpleWave(rate,quantity,instantiationTemplate);
		wave.start();
		InstantiationTemplate returnedTemplate = wave.generateTemplates(120);
		assertThat(returnedTemplate,nullValue());
	}
}
