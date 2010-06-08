package com.gemserk.games.zombierockers;

import org.newdawn.slick.Animation;

public class AnimationHelper {

	final Animation animation;

	final float frameSize;

	float framePosition;

	public Animation getAnimation() {
		return animation;
	}

	public AnimationHelper(Animation animation, float frameSize) {
		this.animation = animation;
		this.frameSize = frameSize;
		this.framePosition = frameSize / 2f;
	}
	
	public void add(float distance) {
		if (distance < 0 )
			rewind(-distance);
		else 
			forward(distance);
	}

	/**
	 * Rewind one frame of the animation
	 * 
	 * @param distance
	 */
	public void rewind(float distance) {
		framePosition -= distance;

		while (framePosition < 0) {
			animation.setCurrentFrame(getPreviousFrame(animation.getFrame(), animation.getFrameCount()));
			framePosition += frameSize;
		}
	}

	/**
	 * Forward one frame of the animation
	 * 
	 * @param distance
	 *            the distance to advance
	 */
	public void forward(float distance) {
		framePosition += distance;

		while (framePosition > frameSize) {
			animation.setCurrentFrame(getNextFrame(animation.getFrame(), animation.getFrameCount()));
			framePosition -= frameSize;
		}
	}

	private int getPreviousFrame(int currentFrame, int totalFrames) {
		if (currentFrame > 0)
			return currentFrame - 1;
		else
			return totalFrames - 1;
	}

	private int getNextFrame(int currentFrame, int totalFrames) {
		if (currentFrame + 1 < totalFrames)
			return currentFrame + 1;
		else
			return 0;
	}

}