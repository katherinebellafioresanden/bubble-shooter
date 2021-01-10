package src;

import java.awt.*;

public class BubbleShooter
{
	// ------DATA-------
	private int x, y; // center of the bubble shooter
	private double angle = Math.PI / 2; // angle is initialized pointing straight up
	
	
	private static final int ARROW_LENGTH = 150; // for drawing the shooter arrow
	private static final int ARROW_SECTIONS = 6;
	
	
	// ------METHODS-------
	
	public BubbleShooter(int xIn, int yIn)
	{
		x = xIn;
		y = yIn;

	}
	
	public double getAngle()
	{
		return angle;
	}
	
	public void updateAngle(double newAngle)
	{
		angle = newAngle;
	}
	
	public void updateAngle(int xMouse, int yMouse)
	{
		// update angle to reflect the x and y coordinates of the mouse that have been passed in
		
		double xLeg = xMouse - x;
		double yLeg = y - yMouse; // yLeg should always be positive
		
		if (xLeg == 0)
		{
			angle = Math.PI / 2; // straight up 90 degrees
		}
		else if (xLeg < 0) // the mouse is in the second quadrant 
		{
			angle = Math.atan(yLeg / xLeg) + Math.PI;
		}
		else // the mouse is in the first quadrant
		{
			angle = Math.atan(yLeg / xLeg);
		}
		
		
	}
	
	public void draw(Graphics g)
	{

		g.setColor(Color.BLACK);
		if (BubbleShooterGame.BACKGROUND_COLOR.equals(Color.BLACK))
		{
			g.setColor(Color.WHITE);
		}
		
		int sectionLength = ARROW_LENGTH / (ARROW_SECTIONS * 2);
	
		for (int i = 0; i < ARROW_SECTIONS; i++)
		{
			double startX = x + (2 * sectionLength) * Math.cos(angle) * i;
			double startY = y - (2 * sectionLength) * Math.sin(angle) * i;
			double stopX = startX + sectionLength * Math.cos(angle);
			double stopY = startY - sectionLength * Math.sin(angle);
			
			g.drawLine((int) (startX), (int) (startY), (int) (stopX), (int) (stopY));
		}

	}


}
