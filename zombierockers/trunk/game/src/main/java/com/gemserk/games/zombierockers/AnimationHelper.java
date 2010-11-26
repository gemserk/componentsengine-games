package com.gemserk.games.zombierockers;

import org.newdawn.slick.Animation;

import com.gemserk.resources.Resource;

public class AnimationHelper {

	final Resource<Animation> animation;

	final float frameSize;

	float framePosition;

	public Animation getAnimation() {
		return animation.get();
	}

	public AnimationHelper(Resource<Animation> animation, float frameSize) {
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
			getAnimation().setCurrentFrame(getPreviousFrame(getAnimation().getFrame(), getAnimation().getFrameCount()));
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
			getAnimation().setCurrentFrame(getNextFrame(getAnimation().getFrame(), getAnimation().getFrameCount()));
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