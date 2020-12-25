import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.Timer;

public class BubbleShooterGame 
extends JFrame 
implements ActionListener, MouseListener, MouseMotionListener 
{
	// screen info
	public static final Color BACKGROUND_COLOR = Color.black;

	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 700;
	public static final int TITLE_BAR = 23;
	public static final int DELAY_IN_MILLISEC = 20;
	public static final int VELOCITY = 17;
	public static final int BUBBLE_RADIUS = BubbleWall.BUBBLE_RADIUS;

	// states of the game 
	private static final int YOU_WON = 15;
	private static final int PLAYING = 12;
	private static int state = PLAYING;
	private static int score = 0;
	private static int bubbleValue = 100;

	// wall of bubbles
	private static BubbleWall wall;

	// bubble shooter at bottom of screen
	private static BubbleShooter bubShooter;

	// temporary bubble for shooting 
	private static Bubble tempBub;

	// stack to keep track of neighbors whenever a Bubble hits the wall
	private static Stack<Point> matchingNeighbors = new Stack<Point>();
	private static int matchingNeighborCount = 0; // used to make sure there are at least 3 matching neighbors to destroy a bubble

	private static int actionPerformedCount = 0;
	
	public static void main(String[] args)
	{
		// create the objects
		wall = new BubbleWall();
		bubShooter = new BubbleShooter(SCREEN_WIDTH / 2, SCREEN_HEIGHT - BUBBLE_RADIUS);
		tempBub = new Bubble(SCREEN_WIDTH / 2, SCREEN_HEIGHT - BUBBLE_RADIUS, BUBBLE_RADIUS);

		// create the game
		System.out.println("This is Bubble Shooter.");
		BubbleShooterGame game = new BubbleShooterGame();

		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		game.setVisible(true);
		game.createBufferStrategy(2);					
		game.addMouseListener(game);
		game.addMouseMotionListener(game);
		game.repaint(); // try removing

		// create and start the timer
		Timer clock = new Timer(DELAY_IN_MILLISEC, game);			
		clock.start();
	}


	public void actionPerformed(ActionEvent e)
	{	
		// check if won
		if (wall.isEmpty())
		{
			state = YOU_WON;
			repaint();
			return;
		}
		

		

		tempBub.move();
		tempBub.bounce(0, SCREEN_WIDTH);


		// check if bubble hit wall
		if (wall.receiveBubble(tempBub))
		{
			tempBub.stopFlying();
			score = wall.getNumBubblesDestroyed();
		}


		// if the bubble missed completely and goes off the top of the screen, 
		// it should also stop flying
		if (tempBub.getY() < 0)
		{
			tempBub.stopFlying();
		}


		// create a new Bubble for the next shot
		if (tempBub.isDoneFlying())
		{
			tempBub = createBubble();
		}

		// fade out and delete certain bubbles
		wall.incrementPhaseOfBubbles();
		repaint();

	}



	@Override
	public void mouseMoved(MouseEvent e)
	{
		// current coordinates of mouse
		int mouseX = e.getX();
		int mouseY = e.getY();

		bubShooter.updateAngle(mouseX, mouseY);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		shootBubble();

	}

	public static void shootBubble()
	{
		// shoot a bubble 
		double angle = bubShooter.getAngle();
		double newDx = VELOCITY * Math.cos(angle);
		double newDy = VELOCITY * Math.sin(angle);

		tempBub.setVelocity(newDx, newDy);
	}

	public static Bubble createBubble()
	{
		Bubble b = new Bubble(SCREEN_WIDTH / 2, SCREEN_HEIGHT - BUBBLE_RADIUS, BUBBLE_RADIUS);
		return b;

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}


	/**
	 * paint 		draw the window					
	 * 
	 * @param g		Graphics object to draw on - which we won't use
	 */
	public void paint(Graphics g)
	{
		BufferStrategy bf = this.getBufferStrategy();
		if (bf == null)
			return;

		Graphics g2 = null;

		try 
		{
			g2 = bf.getDrawGraphics();

			// myPaint does the actual drawing
			myPaint(g2);
		} 
		finally 
		{
			// It is best to dispose() a Graphics object when done with it.
			g2.dispose();
		}

		// Shows the contents of the backbuffer on the screen.
		bf.show();

		//Tell the System to do the Drawing now, otherwise it can take a few extra ms until 
		//Drawing is done which looks very jerky
		Toolkit.getDefaultToolkit().sync();	
	}

	public void myPaint(Graphics g)
	{

		if (state == PLAYING)
		{
			clearScreen(g, BACKGROUND_COLOR);
			wall.draw(g);
			bubShooter.draw(g);
			tempBub.draw(g);

			// display score
			g.setColor(Color.BLACK);
			if (BACKGROUND_COLOR.equals(Color.black))
			{
				g.setColor(Color.white);
			}
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("Current score: " + score, SCREEN_WIDTH - 300, SCREEN_HEIGHT - 40);
		}
		else if (state == YOU_WON)
		{
			// Winning screen designed by Alix Sanden :)
			g.setColor(Color.CYAN);
			g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString("Hello", 50, 50);
			g.drawString("Congratulations", 100, 100);
			g.drawString("Great Job", 150, 150);
			g.drawString("Your Mother is Proud of You", 200, 200);
			g.drawString("Your Father Will Not Disown You", 250, 250);
			g.drawString("You Have Brought Honor Upon Your Family", 300, 300);
			g.drawString("You Will Not Die Alone", 350, 350);
			g.drawString("You Will Live Plentifully For All Your Days", 400, 400);
		}


	}

	public static void clearScreen(Graphics g, Color clearer)
	{
		g.setColor(clearer);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

	}



}
