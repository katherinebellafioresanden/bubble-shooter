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
		if (wall.shouldBubbleStop(tempBub))
		{
			tempBub.stopFlying();

			// figure out where in the array we currently are
			Point currentLoc = wall.computeLocation(tempBub);

			// the flying bubble now joins the wall at THAT location in the array
			wall.setBubble(tempBub, currentLoc);

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

		repaint();

	}




	//	public static void addPointToMatchingNeighbors(int bRow, int bCol, int avoidRow, int avoidCol, int eternallyAvoidRow, int eternallyAvoidCol)
	//	{
	//
	//		// this is just a temporary Point that we will use 
	//		// to push coordinates onto the Stack 
	//		Point tempPoint = new Point(bRow, bCol);
	//
	//		Point avoid1 = new Point(avoidRow, avoidCol);
	//		Point avoid2 = new Point(eternallyAvoidRow, eternallyAvoidCol);
	//
	//		// as long as it's not the location we're supposed to avoid, 
	//		// and it's not already ON the stack
	//		// go ahead and push (bRow, bCol) onto the stack
	//		if (!tempPoint.equals(avoid1) && !tempPoint.equals(avoid2))
	//		{
	//			if (!stackContainsPoint(matchingNeighbors, tempPoint))
	//			{
	//				matchingNeighbors.push(tempPoint);
	//				matchingNeighborCount++;
	//			}
	//		}
	//
	//	}

	//	public static boolean stackContainsPoint(Stack s, Point p)
	//	{
	//		boolean stackContainsPoint = false;
	//
	//		// iterate through matchingNeighbors to see if it contains tempPoint
	//		Iterator<Point> iter = s.iterator();
	//		while (iter.hasNext())
	//		{
	//			Point current = iter.next();
	//			if (current != null)
	//			{
	//				if (current.equals(p))
	//				{
	//					stackContainsPoint = true;
	//				}
	//			}
	//		}
	//		return stackContainsPoint;
	//	}

	//	public static boolean checkFirstLayerOfNeighbors(Bubble b, int bRow, int bCol)
	//	{
	//		int avoidRow = bRow;
	//		int avoidCol = bCol;
	//		int alwaysAvoidRow = bRow;
	//		int alwaysAvoidCol = bCol;
	//
	//		findImmediateMatchingNeighbors(b, bRow, bCol, avoidRow, avoidCol, alwaysAvoidRow, alwaysAvoidCol);
	//
	//		// if SOME matching neighbor was found, clear the stack and return true
	//		if (!matchingNeighbors.isEmpty())
	//		{
	//			matchingNeighbors.clear();
	//			return true;
	//		}
	//
	//		return false;
	//	}

	//	public static void findImmediateMatchingNeighbors(Bubble b, int bRow, int bCol, int avoidRow, int avoidCol, int eternallyAvoidRow, int eternallyAvoidCol)
	//	{
	//
	//		// check all 6 neighbors to see if they're the same color
	//		boolean left = false;
	//		boolean right = false;
	//		boolean leftUp = false;
	//		boolean rightUp = false;
	//		boolean leftDown = false;
	//		boolean rightDown = false;
	//
	//		if (bCol != 0) //if we're not on the far left
	//		{
	//			left = bubs[bRow][bCol - 1].isSameColorAs(b);
	//			if (left)
	//			{
	//				addPointToMatchingNeighbors(bRow, bCol - 1, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				// System.out.println("On the left, the colorChooser is " + bubs[bRow][bCol - 1].getColorChooser());
	//			}
	//
	//		}
	//		if (bCol != NUM_BUBBLES_ACROSS - 1) // if we're not on the far right
	//		{
	//			right = bubs[bRow][bCol + 1].isSameColorAs(b);
	//			if (right)
	//			{
	//				addPointToMatchingNeighbors(bRow, bCol + 1, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//			}
	//		}
	//		if (bRow != 0) // if we're not on the very top
	//		{
	//			if (bRow % 2 == 0) // if we're in a NON-indented row
	//			{
	//				rightUp = bubs[bRow - 1][bCol].isSameColorAs(b);
	//				if (bCol != 0) // and we're not at the far left
	//				{
	//					leftUp = bubs[bRow - 1][bCol - 1].isSameColorAs(b);
	//				}
	//				if (rightUp)
	//				{
	//					addPointToMatchingNeighbors(bRow - 1, bCol, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//				if (leftUp)
	//				{
	//					addPointToMatchingNeighbors(bRow - 1, bCol - 1, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//			}
	//			else // we're in an indented row
	//			{
	//				if (bCol != NUM_BUBBLES_ACROSS - 1)
	//				{
	//					rightUp = bubs[bRow - 1][bCol + 1].isSameColorAs(b);
	//				}
	//				leftUp = bubs[bRow - 1][bCol].isSameColorAs(b);
	//
	//				if (rightUp)
	//				{
	//					addPointToMatchingNeighbors(bRow - 1, bCol + 1, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//				if (leftUp)
	//				{
	//					addPointToMatchingNeighbors(bRow - 1, bCol, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//
	//			}
	//		}
	//		if (bRow != TOTAL_BUBBLES_DOWN - 1) // if we're not on the very bottom
	//		{
	//			if (bRow % 2 == 0) // if we're in a NON-indented row
	//			{
	//				rightDown = bubs[bRow + 1][bCol].isSameColorAs(b);
	//				if (bCol != 0)
	//				{
	//					leftDown = bubs[bRow + 1][bCol - 1].isSameColorAs(b);
	//				}
	//
	//				if (rightDown)
	//				{
	//					addPointToMatchingNeighbors(bRow + 1, bCol, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//				if (leftDown)
	//				{
	//					addPointToMatchingNeighbors(bRow + 1, bCol - 1, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//			}
	//			else // we're in an indented row
	//			{
	//				if (bCol != NUM_BUBBLES_ACROSS - 1)
	//				{
	//					rightDown = bubs[bRow + 1][bCol + 1].isSameColorAs(b);
	//				}
	//
	//				leftDown = bubs[bRow + 1][bCol].isSameColorAs(b);
	//
	//				if (rightDown)
	//				{
	//					addPointToMatchingNeighbors(bRow + 1, bCol + 1, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//				if (leftDown)
	//				{
	//					addPointToMatchingNeighbors(bRow + 1, bCol, avoidRow, avoidCol, eternallyAvoidRow, eternallyAvoidCol);
	//				}
	//			}
	//		}
	//
	//		// if no matching neighbors were found
	//		if (!left && !right && !leftUp && !rightUp && !leftDown && !rightDown)
	//		{
	//			System.out.println(" No matching neighbors were found.  matchingNeighbors should be empty.");
	//		}
	//
	//	}

	//	public static void destroyAllMatchingNeighbors(Bubble b, int bRow, int bCol)
	//	{
	//		// stores your previous coordinates 
	//		// so you can tell findImmediateMatchingNeighbors() to avoid them
	//		int prevRow = bRow;
	//		int prevCol = bCol;
	//
	//		// stores the original bubble location, which is eternally off limits
	//		int origRow = bRow;
	//		int origCol = bCol;
	//
	//		// the first time we call this, the coordinates we want to avoid are our own
	//		findImmediateMatchingNeighbors(b, bRow, bCol, prevRow, prevCol, origRow, origCol);
	//
	//		while (!matchingNeighbors.isEmpty())
	//		{
	//			// pop the next matching neighbor
	//			Point currentLocation = matchingNeighbors.pop();
	//			int row = currentLocation.getX();
	//			int col = currentLocation.getY();
	//
	//			// find & push its neighbors
	//			findImmediateMatchingNeighbors(bubs[row][col], row, col, prevRow, prevCol, origRow, origCol);
	//
	//			// destroy it
	//			if (matchingNeighborCount >= 3)
	//			{
	//				bubs[row][col].setExists(false);	
	//				score = score + bubbleValue;
	//			}
	//
	//			// make sure not to check this coordinate next time
	//			prevRow = row;
	//			prevCol = col;
	//		}
	//	}

	//	public static void land(Bubble b)
	//	{
	//
	//		// run through the 2D array and see if b is on top of an available space
	//
	//		boolean keepGoing = true;
	//
	//		for (int row = 0; (row < TOTAL_BUBBLES_DOWN && keepGoing); row++)
	//		{
	//			for (int col = 0; (col < NUM_BUBBLES_ACROSS && keepGoing); col++)
	//			{
	//				// if we're looking at a bubble that exists (ie it's part of the wall)
	//				if (bubs[row][col].exists())
	//				{
	//					// AND we're right next to it
	//					if (b.isAdjacentTo(bubs[row][col]))
	//					{
	//						// then we need to land
	//						b.stopFlying();
	//
	//						// figure out where in the array we currently are
	//						Point currentIndex = findArrayLocation(b);
	//						int r = currentIndex.getX();
	//						int c = currentIndex.getY();
	//
	//
	//						// the flying bubble now joins the wall at THAT location in the array
	//						bubs[r][c].setColor(b.getColor());
	//						bubs[r][c].setExists(true);
	//
	//						// check to see which Bubbles now need to be destroyed
	//						boolean someMatchExists = checkFirstLayerOfNeighbors(bubs[r][c], r, c);
	//						if (someMatchExists)
	//						{
	//							destroyAllMatchingNeighbors(bubs[r][c], r, c);
	//							System.out.println("MADE IT HERE");
	//							System.out.println(matchingNeighborCount);
	//
	//							if (matchingNeighborCount >= 3)
	//							{
	//								bubs[r][c].setExists(false);
	//								score = score + bubbleValue;
	//							}
	//
	//							// reset matchingNeighborCount to 0 for the next flying Bubble
	//							matchingNeighborCount = 0; 
	//
	//						}
	//
	//						// once you've found an adjacent bubble in the wall,
	//						// stop the nested for loop.  
	//						// The whole purpose of the loop was to find this bubble
	//						keepGoing = false;
	//					}
	//				}
	//			}
	//		}
	//
	//		// check to see if there are any "hanging" bubbles that are no longer connected to the wall
	//		// if there are, we should destroy them
	//		destroyHangers();
	//	}

	//	public static void destroyHangers()
	//	{
	//		// loop through the wall, and if a Bubble doesn't have neighbors holding it on, destroy it
	//		boolean keepGoing = true;
	//
	//		for (int row = 0; (row < TOTAL_BUBBLES_DOWN && keepGoing); row++)
	//		{
	//			for (int col = 0; (col < NUM_BUBBLES_ACROSS && keepGoing); col++)
	//			{
	//				// if this bubble exists and is a loner
	//				if (bubs[row][col].exists() && isLoner(row,col))
	//				{
	//					// destroy it
	//					bubs[row][col].setExists(false);
	//					score = score + bubbleValue;
	//				}
	//			}
	//		}
	//
	//
	//	}

	//	public static boolean isLoner(int bRow, int bCol)
	//	{
	//		// check all 6 neighbors to see if they're non-existent
	//		boolean leftGone = false;
	//		boolean rightGone = false;
	//		boolean leftUpGone = false;
	//		boolean rightUpGone = false;
	//		boolean leftDownGone = false;
	//		boolean rightDownGone = false;
	//
	//		if (bCol != 0) //if we're not on the far left
	//		{
	//			leftGone = !bubs[bRow][bCol - 1].exists();
	//		}
	//		else   // we ARE on the far left
	//		{
	//			leftGone = true;
	//		}
	//
	//
	//		if (bCol != NUM_BUBBLES_ACROSS - 1) // if we're not on the far right
	//		{
	//			rightGone = !bubs[bRow][bCol + 1].exists();
	//		}
	//		else // we ARE on the far right
	//		{
	//			rightGone = true;
	//		}
	//
	//
	//		if (bRow != 0) // if we're not on the very top
	//		{
	//			if (bRow % 2 == 0) // if we're in a NON-indented row
	//			{
	//				rightUpGone = !bubs[bRow - 1][bCol].exists();
	//
	//				if (bCol != 0) // and we're not at the far left
	//				{
	//					leftUpGone = !bubs[bRow - 1][bCol - 1].exists();
	//				}
	//				else // we ARE at the far left
	//				{
	//					leftUpGone = true;
	//				}
	//			}
	//			else // we're in an indented row
	//			{
	//				leftUpGone = !bubs[bRow - 1][bCol].exists();
	//
	//				if (bCol != NUM_BUBBLES_ACROSS - 1) // if we're not at the far RIGHT
	//				{
	//					rightUpGone = !bubs[bRow - 1][bCol + 1].exists();
	//				}
	//				else
	//				{
	//					rightUpGone = true;
	//				}
	//			}
	//		}
	//		else  // we are on the very top
	//		{
	//			// if you're at the very top, then it's like you're held up by the screen itself.
	//			// so you can't be a loner.
	//			rightUpGone = false;
	//			leftUpGone = false;
	//		}
	//
	//
	//		if (bRow != TOTAL_BUBBLES_DOWN - 1) // if we're not on the very bottom
	//		{
	//			if (bRow % 2 == 0) // if we're in a NON-indented row
	//			{
	//				rightDownGone = !bubs[bRow + 1][bCol].exists();
	//
	//				if (bCol != 0) // if we're not on the far left
	//				{
	//					leftDownGone = !bubs[bRow + 1][bCol - 1].exists();
	//				}
	//				else  // we ARE on the far left
	//				{
	//					leftDownGone = true;
	//				}
	//			}
	//			else // we're in an indented row
	//			{
	//				leftDownGone = !bubs[bRow + 1][bCol].exists();
	//
	//				if (bCol != NUM_BUBBLES_ACROSS - 1) // if we're not on the far right
	//				{
	//					rightDownGone = !bubs[bRow + 1][bCol + 1].exists();
	//				}
	//				else // we ARE on the far right
	//				{
	//					rightDownGone = true;
	//				}
	//			}
	//		}
	//		else // we ARE on the very bottom
	//		{
	//			rightDownGone = true;
	//			leftDownGone = true;
	//		}
	//
	//		// this means the bubble is indeed a loner
	//		// NOTE: rightDownGone & leftDownGone are currently ommitted.  Not sure if I want them to matter.
	//		if (rightUpGone && leftUpGone && rightGone && leftGone)
	//		{
	//			return true;
	//		}
	//		return false;
	//	}

	//	public static Point findArrayLocation(Bubble b)
	//	{
	//		double x = b.getX();
	//		double y = b.getY();
	//
	//		// just scan through the entire array and see what position is closest to b
	//		Point closestIndex = new Point(0, 0);
	//		double minDist = SCREEN_WIDTH; // a big number to start
	//		for (int row = 0; row < TOTAL_BUBBLES_DOWN; row++)
	//		{
	//			for (int col = 0; col < NUM_BUBBLES_ACROSS; col++)
	//			{
	//				Bubble localBub = bubs[row][col];
	//				double legX = localBub.getX() - x;
	//				double legY = localBub.getY() - y;
	//				double dist = Math.sqrt(legX * legX + legY * legY);
	//				if (dist < minDist)
	//				{
	//					closestIndex.setX(row);
	//					closestIndex.setY(col);
	//					minDist = dist;
	//				}
	//			}
	//		}
	//		return closestIndex;
	//	}

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
			clearScreen(g, Color.WHITE);
			wall.draw(g);
			bubShooter.draw(g);
			tempBub.draw(g);

			// display score
			g.setColor(Color.BLACK);
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
