package com.gemserk.games.towerofdefense.waves;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gemserk.componentsengine.instantiationtemplates.InstantiationTemplate;

@RunWith(JMock.class)
public class WavesTest {

	Mockery mockery = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void wavesMustNotGenerateWhenEmpty() {

		Waves waves = new Waves();
		List<InstantiationTemplate> instantiationTemplates = waves.generateTemplates(100);
		assertTrue(instantiationTemplates.isEmpty());

	}

	@Test
	public void whenAWavesIsCreatedCurrentAndTotalAre0() {

		Waves waves = new Waves();
		assertThat(waves.getCurrent(), equalTo(0));
		assertThat(waves.getTotal(), equalTo(0));
	}

	@Test
	public void totalMustBeEqualToQuantityOfWaves() {

		Waves waves = new Waves();
		waves.setWaves(Arrays.asList(mockery.mock(Wave.class, "wave1"), mockery.mock(Wave.class, "wave2")));
		assertThat(waves.getTotal(), equalTo(2));
	}

	@Test
	public void currentMustBeLastActivatedWave() {
		Waves waves = new Waves();
		
		final Wave wave = mockery.mock(Wave.class, "wave1");
		waves.setWaves(Arrays.asList(wave, mockery.mock(Wave.class, "wave2")));

		mockery.checking(new Expectations() {
			{
				oneOf(wave).start();
			}
		});
		
		waves.nextWave();
		assertThat(waves.getCurrent(), equalTo(1));
		assertThat(waves.getTotal(), equalTo(2));
	}
	
	@Test(expected=RuntimeException.class)
	public void currentMustNotExceedTotalWaves() {
		Waves waves = new Waves();
		waves.setWaves(Arrays.asList(mockery.mock(Wave.class, "wave1"), mockery.mock(Wave.class, "wave2")));
		waves.currentWave=2;
		waves.nextWave();
	}
	
	public void testLastWaveStarted() {
		Waves waves = new Waves();
		waves.setWaves(Arrays.asList(mockery.mock(Wave.class, "wave1")));
		waves.currentWave=1;
		assertTrue(waves.isLastWaveStarted());
	}

	@Test
	public void mustNotGenerateTemplatesWhenInnerWavesDont() {

		final int delta = 100;
		final Wave wave = mockery.mock(Wave.class);

		Waves waves = new Waves();
		waves.setWaves(Arrays.asList(wave));

		mockery.checking(new Expectations() {
			{
				oneOf(wave).generateTemplates(delta);
				will(returnValue(null));
			}
		});

		List<InstantiationTemplate> instantiationTemplates = waves.generateTemplates(delta);

		assertTrue(instantiationTemplates.isEmpty());

	}

	@Test
	public void mustGenerateTemplateWhenInnerWavesDo() {

		final int delta = 100;
		final InstantiationTemplate instantiationTemplate = mockery.mock(InstantiationTemplate.class);
		final Wave wave = mockery.mock(Wave.class);

		Waves waves = new Waves();
		waves.setWaves(Arrays.asList(wave));

		mockery.checking(new Expectations() {
			{
				oneOf(wave).generateTemplates(delta);
				will(returnValue(instantiationTemplate));
			}
		});

		List<InstantiationTemplate> instantiationTemplates = waves.generateTemplates(delta);

		assertThat(instantiationTemplates.get(0), IsEqual.equalTo(instantiationTemplate));

	}

}
