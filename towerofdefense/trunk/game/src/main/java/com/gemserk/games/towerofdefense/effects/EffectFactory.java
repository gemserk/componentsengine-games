package com.gemserk.games.towerofdefense.effects;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class EffectFactory {

	public static void generateRandomValues(int vertexCount, float[] xVector, float[] aVector, float maxGap, float maxAmplitude) {
		Random random = new Random();

		for (int i = 0; i < vertexCount; i++) {
			xVector[i] = random.nextFloat() * maxGap;
			aVector[i] = random.nextFloat() * maxAmplitude;
		}
	}

	public static ExplosionEffect explosionEffect(int count, int x, int y, float minAngle, float maxAngle, int time, float minLength, float maxLength) {
		ArrayList<LineEffect> particles = new ArrayList<LineEffect>();

		for (int i = 0; i < count; i++)
			particles.add(lineEffect(x, y, minAngle, maxAngle, time, minLength, maxLength));

		return new ExplosionEffect(particles);
	}

	public static LineEffect lineEffect(int x, int y, float minAngle, float maxAngle, int expansionTime, float minLength, float maxLength) {
		Random random = new Random();

		maxAngle = -maxAngle;
		minAngle = -minAngle;

		float length = random.nextFloat() * maxLength + minLength;
		int totalTime = expansionTime;
		float width = 2.0f;

		LineEffectParticle lineParticleEffect = new LineEffectParticle(length, totalTime);

		float r = random.nextFloat() * 0.0f + 1.0f;
		float g = random.nextFloat() * 0.1f + 0.2f;
		float b = random.nextFloat() * 0.1f + 0.2f;
		Color color = new Color(r, g, b, 1);

		float angle = random.nextFloat() * maxAngle + minAngle;
		LineEffectRenderer lineParticleEffectRenderer = new LineEffectRenderer(new Vector2f(x, y), angle, width, color);

		LineEffect effect = new LineEffect(lineParticleEffect, lineParticleEffectRenderer);
		return effect;
	}

}