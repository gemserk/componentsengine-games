/**
 * 
 */
package com.gemserk.games.towerofdefense.components;

public class AngleUtils {
	/**
	 * Returns the difference between two angles in degrees
	 * @param currentAngle
	 * @param desiredAngle
	 * @return
	 */
	public double angleDifference(double currentAngle, double desiredAngle) {
		double diffAngle = desiredAngle-currentAngle;
		
		if (diffAngle > 180)
			diffAngle -= 360;
		else if (diffAngle < -180)
			diffAngle += 360;
		return diffAngle;
	}
	
}