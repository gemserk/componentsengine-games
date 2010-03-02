package com.gemserk.games.towerofdefense.waves;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gemserk.games.towerofdefense.InstantiationTemplate;

@RunWith(JMock.class)
public class CompositeWaveTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	
	@Test
	public void test(){
		List<Wave> innerWaves = new ArrayList<Wave>();
		CompositeWave wave = new CompositeWave(innerWaves);
		InstantiationTemplate template = wave.generateTemplates(Integer.MAX_VALUE);
		assertNull(template);
		assertTrue(wave.isDone());
	}
	
	@Test
	public void test2(){
		List<Wave> innerWaves = new ArrayList<Wave>();
		final Wave mock = mockery.mock(Wave.class);
		innerWaves.add(mock);
		CompositeWave wave = new CompositeWave(innerWaves);
		final int delta = Integer.MAX_VALUE;

		mockery.checking(new Expectations() {
			{
				oneOf(mock).generateTemplates(delta);will(returnValue(null));
				oneOf(mock).isDone();will(returnValue(false));
			}
		});
		
		InstantiationTemplate template = wave.generateTemplates(delta);
		assertNull(template);
		assertFalse(wave.isDone());
	}
	
	@Test
	public void test3(){
		List<Wave> innerWaves = new ArrayList<Wave>();
		final Wave mock1 = mockery.mock(Wave.class,"mock1");
		final Wave mock2 = mockery.mock(Wave.class,"mock2");
		innerWaves.add(mock1);
		innerWaves.add(mock2);
		CompositeWave wave = new CompositeWave(innerWaves);
	
		mockery.checking(new Expectations() {
			{
				oneOf(mock1).start();
				oneOf(mock2).start();
			}
		});
		
		wave.start();
	}
	
	@Test
	public void test4(){
		List<Wave> innerWaves = new ArrayList<Wave>();
		final Wave mock1 = mockery.mock(Wave.class,"mock1");
		final Wave mock2 = mockery.mock(Wave.class,"mock2");
		innerWaves.add(mock1);
		innerWaves.add(mock2);
		CompositeWave wave = new CompositeWave(innerWaves);
		
		final InstantiationTemplate template = mockery.mock(InstantiationTemplate.class);
		final int delta = 100;
		
		mockery.checking(new Expectations() {
			{
			
				oneOf(mock1).generateTemplates(delta);will(returnValue(template));
				oneOf(mock1).isDone();will(returnValue(false));
			}
		});
		
		InstantiationTemplate returnedTemplate = wave.generateTemplates(delta);
		assertThat(wave.currentWave, equalTo(0));
		assertThat(returnedTemplate, sameInstance(template));
		
	}
	
	@Test
	public void test5(){
		List<Wave> innerWaves = new ArrayList<Wave>();
		final Wave mock1 = mockery.mock(Wave.class,"mock1");
		final Wave mock2 = mockery.mock(Wave.class,"mock2");
		innerWaves.add(mock1);
		innerWaves.add(mock2);
		CompositeWave wave = new CompositeWave(innerWaves);
		
		final InstantiationTemplate template = mockery.mock(InstantiationTemplate.class);
		final int delta = 100;
		
		mockery.checking(new Expectations() {
			{
			
				oneOf(mock1).generateTemplates(delta);will(returnValue(template));
				oneOf(mock1).isDone();will(returnValue(true));
			}
		});
		
		InstantiationTemplate returnedTemplate = wave.generateTemplates(delta);
		assertThat(wave.currentWave, equalTo(1));
		assertThat(returnedTemplate, sameInstance(template));
		
	}
	
	
	
}
