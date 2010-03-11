package com.gemserk.games.towerofdefense.timers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gemserk.componentsengine.timers.PeriodicTimer;

public class PeriodicTimerTest {

	@Test
	public void shouldNotFireIfNotReachedPeriod() {
		int period = 100;
		PeriodicTimer timer = new PeriodicTimer(period);
		boolean fired = timer.update(10);
		assertFalse(fired);
	}

	@Test
	public void shouldFireWhenPeriodReached() {
		int period = 100;
		PeriodicTimer timer = new PeriodicTimer(period);
		boolean fired = timer.update(110);
		assertTrue(fired);
	}

	@Test
	public void shouldFireWhenPeriodReachedInMultipleUpdates() {
		int period = 100;
		PeriodicTimer timer = new PeriodicTimer(period);
		boolean fired = timer.update(90);
		fired = timer.update(90);
		assertTrue(fired);
	}

	@Test
	public void shouldNotFireWhenPeriodNotReachedInMultipleUpdates() {
		int period = 100;
		PeriodicTimer timer = new PeriodicTimer(period);
		boolean fired = timer.update(80);
		fired = timer.update(10);
		assertFalse(fired);
	}

	@Test
	public void shouldFireWhenPeriodReachedInMultipleUpdates2() {
		int period = 100;
		PeriodicTimer timer = new PeriodicTimer(period);
		boolean fired = timer.update(210);
		assertTrue(fired);
		fired = timer.update(10);
		assertTrue(fired);
		fired = timer.update(10);
		assertFalse(fired);
	}

	@Test
	public void whenResetIfUpdateIsLessThanPeriodShouldNotFire() {
		testReset(100, false, 50, 99);
	}

	@Test
	public void whenResetIfUpdateIsLargerThanPeriosShouldFire() {
		testReset(100, true, 50, 101);
	}

	private void testReset(int period, boolean expectedFired, int firstUpdate, int secondUpdate) {
		PeriodicTimer timer = new PeriodicTimer(period);
		boolean fired = timer.update(firstUpdate);
		timer.reset();
		fired = timer.update(secondUpdate);
		assertEquals(expectedFired, fired);
	}

}
