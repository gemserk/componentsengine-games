package com.gemserk.games.towerofdefense.timers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gemserk.componentsengine.timers.CountDownTimer;



public class CountDownTimerTest {
	
	@Test
	public void test() {
		CountDownTimer countDownTimer = new CountDownTimer(100);
		assertFalse(countDownTimer.isRunning());
	}

	@Test
	public void test3() {
		CountDownTimer countDownTimer = new CountDownTimer(100);
		countDownTimer.reset();
		assertTrue(countDownTimer.isRunning());
		boolean fired = countDownTimer.update(200);
		assertTrue(fired);
		assertFalse(countDownTimer.isRunning());
	}


}
