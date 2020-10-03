import java.awt.*;
import java.util.Random;

public class Bubble
{
	// random number generator
	public static final Random GEN = new Random();

	// list of possible colors for a Bubble
	public static final int BLUE = 0;
	public static final int CYAN = 1;
	public static final int GREEN = 2;
	public static final int MAGENTA = 3;
	public static final int RED = 4;
	public static final int YELLOW = 5;
	
	// ------DATA-------

	// private details about each Bubble
	private double x, y; // center of Bubble
	private double dx, dy; // speed of Bubble, usually 0
	private boolean doneFlying = false; // only used for shooting bubbles
	private static int radius;
	private int colorChooser; // for comparing colors
	private Color color;
	
	// details about wall Bubbles
	private boolean exists = true; 
	private boolean available = false;
	
	// ------METHODS-------

	public Bubble(double xIn, double yIn, int radiusIn)
	{
		x = xIn;
		y = yIn;
		radius = radiusIn;
		color = chooseRandomColor();
		dx = 0;
		dy = 0;

	}

	// SETTERS *****************************************************
	public void setColor(Color newColor)
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

	public void setExists(boolean newStatus)
	{
		exists = newStatus;
		
		if (newStatus == false)
		{
			colorChooser = -1; // if it doesn't exist, it shouldn't have a color
		}
		else // newStatus is TRUE
		{
			if (Color.BLUE.equals(color))
			{
				colorChooser = BLUE;
			}
			else if (Color.CYAN.equals(color))
			{
				colorChooser = CYAN;
			}
			else if (Color.GREEN.equals(color))
			{
				colorChooser = GREEN;
			}
			else if (Color.MAGENTA.equals(color))
			{
				colorChooser = MAGENTA;
			}
			else if (Color.RED.equals(color))
			{
				colorChooser = RED;
			}
			else if (Color.YELLOW.equals(color))
			{
				colorChooser = YELLOW;
			}
			else
			{
				System.out.println("in setExists() and something's wrong with color.");
			}
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
		return exists;
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

	public Color getColor()
	{
		return color;
	}

	public int getColorChooser()
	{
		return colorChooser;
	}
	
	public int getRadius()
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
		if (colorChooser == otherBub.getColorChooser())
		{
			return true;
		}

		return false;
	}


	public void draw(Graphics g)
	{	
		// non existent bubbles should not be drawn
		if (!exists)
		{
			// bubble
			g.setColor(new Color(253, 253, 253));
			g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);
			return;
		}

		// bubble
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), 2 * radius, 2 * radius);

		// sun reflection
		int miniR = radius / 5;
		g.setColor(Color.white);
		g.fillOval((int) (x - radius / 2 - miniR), (int) (y - radius / 2 - miniR), miniR * 2, miniR * 2);

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
		colorChooser = GEN.nextInt(6); 
	
		// assign color
		if (colorChooser == BLUE)
		{
			return Color.BLUE;
		}
		else if (colorChooser == CYAN)
		{
			return Color.CYAN;
		}
		else if (colorChooser == GREEN)
		{
			return Color.GREEN;
		}
		else if (colorChooser == MAGENTA)
		{
			return Color.MAGENTA;
		}
		else if (colorChooser == RED)
		{
			return Color.RED;
		}
		else if (colorChooser == YELLOW)
		{
			return Color.YELLOW;
		}
		else
		{
			System.out.println("Something is wrong with the randomized color.");
			return Color.BLACK;
		}

	}

}
