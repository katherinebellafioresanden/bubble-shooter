package src;

import java.awt.*;
import java.util.Random;

public class Bubble
{
	// random number generator
	public static final Random GEN = new Random();

	// list of possible colors for a Bubble
	public static final int NUM_COLORS = 6;
	public static final int BLUE = 0;
	public static final int CYAN = 1;
	public static final int GREEN = 2;
	public static final int MAGENTA = 3;
	public static final int RED = 4;
	public static final int YELLOW = 5;
	
	private static Color nonexistentColor;
	
	// ------DATA-------

	// private details about each Bubble
	private double x, y; // center of Bubble
	private double dx, dy; // speed of Bubble, usually 0
	private boolean doneFlying = false; // only used for shooting bubbles
	private static int radius;
	private int color; 
	
	// details about wall Bubbles
	public static final int EXISTS = 1;
	public static final int PARTIAL = 2;
	public static final int NONEXISTENT = 3;

	public static final int PHASE_LIMIT = 10;
	public int phase = 0; // phase for partially existing bubbles to fade out
	
	private int existence = 1; 
	private boolean available = false;
	private boolean partiallyPopped = false;
	
	// ------METHODS-------

	public Bubble(double xIn, double yIn, int radiusIn)
	{
		x = xIn;
		y = yIn;
		radius = radiusIn;
		color = GEN.nextInt(NUM_COLORS);
		dx = 0;
		dy = 0;
		
	}
	
	public static void initializeNonexistentColor()
	{
		int offset = 5;
		// color for nonexistent bubbles
		if (BubbleShooterGame.BACKGROUND_COLOR.equals(Color.BLACK))
		{
			nonexistentColor = new Color(offset, offset, offset);
		}
		else
		{
			nonexistentColor = new Color(255 - offset, 255 - offset, 255 - offset);
		}

	}

	// SETTERS *****************************************************
	public void setColor(int newColor)
	{
		color = newColor;
	}

	public void setVelocity(double newDx, double newDy)
	{
		dx = newDx;
		dy = newDy;
	}

	public void stopFlying()
	{	
		doneFlying = true;
		dy = 0;
		dx = 0;
	}

	public void setExists(int newStatus)
	{
		existence = newStatus;
		if (existence == NONEXISTENT)
		{
			phase = 0; // reset phase of this bubble
		}
	}
	
	
	public void setAvailable(boolean set)
	{
		available = set;
	}
	



	// GETTERS *****************************************************	
	public boolean isDoneFlying()
	{
		return doneFlying;
	}
	
	public boolean isAvailableForLanding()
	{
		return available;
	}

	public boolean exists()
	{
		return existence == EXISTS;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}
	
	public double getDy()
	{
		return dy;
	}

	public int getColor()
	{
		return color;
	}

	public int getColorChooser()
	{
		return color;
	}
	
	public static int getRadius()
	{
		return radius;
	}




	// OTHER METHODS *************************************************
	public double distFrom(Bubble other)
	{
		double legX = other.getX() - x;
		double legY = other.getY() - y;
		double distBtwnCenters = Math.sqrt(legX * legX + legY * legY);
		
		return distBtwnCenters;
	}
//	public boolean closeEnoughTo(Bubble other, int padding)
//	{
//		return distFrom(other) <= radius + other.getRadius() + padding;
//	}
	
	public boolean isAdjacentTo(Bubble otherBubble)
	{
		// create mini inner-ball
		double temporaryReducedRadius = 0.6 * radius;
		double legX = otherBubble.getX() - x;
		double legY = otherBubble.getY() - y;
		double distBtwnCenters = Math.sqrt(legX * legX + legY * legY);

		// create dot ahead 
		double speed = Math.sqrt(dx * dx + dy * dy);
		double directionX = dx / speed;
		double directionY = dy / speed;
		double pointAheadX = x + directionX * radius;
		double pointAheadY = y - directionY * radius;

		double legAheadX = otherBubble.getX() - pointAheadX;
		double legAheadY = otherBubble.getY() - pointAheadY;

		double distFromPointAhead = Math.sqrt(legAheadX * legAheadX + legAheadY * legAheadY);

		// decide whether we are close enough to be "adjacent"
		boolean centerIsClose = distBtwnCenters < temporaryReducedRadius + radius;
		boolean pointAheadIsClose = distFromPointAhead < radius;

		if (centerIsClose || pointAheadIsClose)
		{
			return true;
		}
		return false;
	}
	


	public boolean isSameColorAs(Bubble otherBub)
	{
		if (color == otherBub.getColorChooser())
		{
			return true;
		}

		return false;
	}


	public void draw(Graphics g)
	{	
		// non existent bubbles should not be drawn
		if (existence == NONEXISTENT)
		{
			// bubble
			g.setColor(nonexistentColor); // light light grey
			g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
		}
		else // if it EXISTS or PARTIALLY EXISTS
		{
			// bubble
			Color c = chooseColor();
			g.setColor(c);
			g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);

			// sun reflection
			int miniR = radius / 5;
			g.setColor(Color.white);
			g.fillOval((int) (x - radius / 2 - miniR), (int) (y - radius / 2 - miniR), miniR * 2, miniR * 2);
			
			
			// coverup if needed
			if (existence == PARTIAL)
			{
				double coverUpRadius = computeCoverUpRadius();
				
				g.setColor(BubbleShooterGame.BACKGROUND_COLOR);
				
				g.fillOval((int) (x - coverUpRadius), 
						(int) (y - coverUpRadius), 
						(int) (2 * coverUpRadius), 
						(int) (2 * coverUpRadius));
			}
		}	

	}
	
	
	private double computeCoverUpRadius()
	{
		double goal = 1;
		double start = .2;
		double increment = (goal - start) / PHASE_LIMIT;
		double multiplier = Math.min(1, start + increment * phase);
		
		double coverUpRadius = radius * multiplier;
		
		return coverUpRadius;
	}
	
	public void displayPhaseDebug()
	{
		System.out.println("phase = " + phase);
	}
	
	public Color chooseColor()
	{
		if (color == BLUE) return Color.BLUE;
		if (color == CYAN) return Color.CYAN;
		if (color == GREEN) return Color.GREEN;
		if (color == MAGENTA) return Color.MAGENTA;
		if (color == RED) return Color.RED;
		if (color == YELLOW) return Color.YELLOW;
		
		System.out.println("something's wrong with color chooser");
		return Color.BLACK;
	}

	public void move()
	{
		x = x + dx;
		y = y - dy;
	}

	public void bounce(int xLow, int xHigh)
	{
		// Check for an x bounce.  Note that we bounce if the x is too
		//  low or too high AND IS HEADING IN THE WRONG DIRECTION.
		if ((x - radius <= xLow && dx < 0) || (x + radius >= xHigh && dx > 0))
		{
			dx = -dx;
		}
	}

	public Color chooseRandomColor()
	{
		// generate one of the 6 final colors
		color = GEN.nextInt(6) + 1; 
	
		// assign color
		if (color == BLUE)
		{
			return Color.BLUE;
		}
		else if (color == CYAN)
		{
			return Color.CYAN;
		}
		else if (color == GREEN)
		{
			return Color.GREEN;
		}
		else if (color == MAGENTA)
		{
			return Color.MAGENTA;
		}
		else if (color == RED)
		{
			return Color.RED;
		}
		else if (color == YELLOW)
		{
			return Color.YELLOW;
		}
		else
		{
			System.out.println("Something is wrong with the randomized color.");
			return Color.BLACK;
		}

	}

	public void setPhase(int deletePhase) 
	{
		phase = deletePhase;
	}

}
