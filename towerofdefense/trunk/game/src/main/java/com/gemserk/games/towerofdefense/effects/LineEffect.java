package com.gemserk.games.towerofdefense.effects;

public class LineEffect {

	LineEffectParticle lineParticleEffect;

	LineEffectRenderer lineParticleEffectRenderer;

	public LineEffect(LineEffectParticle lineParticleEffect, LineEffectRenderer lineParticleEffectRenderer) {
		this.lineParticleEffect = lineParticleEffect;
		this.lineParticleEffectRenderer = lineParticleEffectRenderer;
	}

	public void update(int delta) {
		lineParticleEffect.update(delta);
	}

	public void render() {
		lineParticleEffectRenderer.render(lineParticleEffect);
	}

	public boolean isDone() {
		return lineParticleEffect.isDone();
	}

}