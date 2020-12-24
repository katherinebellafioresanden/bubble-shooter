import java.awt.*;
import java.util.*;
import java.util.List;



public class BubbleWall
{
	// general screen & wall info
	private static final int SCREEN_WIDTH = BubbleShooterGame.SCREEN_WIDTH;
	private static final int SCREEN_HEIGHT = BubbleShooterGame.SCREEN_HEIGHT;
	private static final int TITLE_BAR = BubbleShooterGame.TITLE_BAR;

	private static final int NUM_BUBBLES_ACROSS = 15;
	private static final int NUM_BUBBLES_DOWN = 5;
	private static final double BUBBLE_PERVASIVENESS = 0.9; 
	// a value of 1 would mean there are no gaps in the bubble array

	private static final double BUBBLE_DIAMETER_AND_GAP = SCREEN_WIDTH / (NUM_BUBBLES_ACROSS + 0.5); 
	private static final double BUBBLE_DIAMETER = BUBBLE_DIAMETER_AND_GAP * BUBBLE_PERVASIVENESS;
	private static final int GAP = (int) (BUBBLE_DIAMETER_AND_GAP - BUBBLE_DIAMETER); // blank space between bubbles
	public static final int BUBBLE_RADIUS = (int) (BUBBLE_DIAMETER / 2);

	// TOTAL_BUBBLES_DOWN includes invisible bubbles all the way to the bottom of screen
	private static final double HEIGHT_OF_2_LAYERS = 2 * BUBBLE_RADIUS + (BUBBLE_RADIUS + GAP / 2) * Math.sqrt(3);
	private static final int TOTAL_BUBBLES_DOWN = (int) (SCREEN_HEIGHT / HEIGHT_OF_2_LAYERS * 2);
	
	private static final int MATCHING_NEIGHBOR_THRESHOLD = 3;


	// ------------- instance variables -------------


	private Bubble[][] bubs;
	private int[][] info; // info[r][c] == 0 if a bubble does not exist.
						  // info[r][c] == bubble's colorChooser otherwise
	
	private int numBubblesDestroyed;
	private List<int[]> toDelete;
	
	

	public BubbleWall()
	{
		Bubble.initializeNonexistentColor();
		
		bubs = new Bubble[TOTAL_BUBBLES_DOWN][NUM_BUBBLES_ACROSS];
		info = new int[TOTAL_BUBBLES_DOWN][NUM_BUBBLES_ACROSS];
		numBubblesDestroyed = 0;
		toDelete = new ArrayList<>();

		buildBubbleArray();
	}

	public void buildBubbleArray()
	{
		int arrayCornerX = GAP / 2 + BUBBLE_RADIUS;
		int arrayCornerY = TITLE_BAR + GAP / 2 + BUBBLE_RADIUS;

		// build bubble array
		for (int row = 0; row < TOTAL_BUBBLES_DOWN; row++)
		{
			for (int col = 0; col < NUM_BUBBLES_ACROSS; col++)
			{
				if (row % 2 == 1) // if we're in the second row, or fourth, etc
				{
					// shift the X corner right by a bit, calculated by 30-60-90 triangles
					arrayCornerX = 2 * BUBBLE_RADIUS + GAP;
				}
				else
				{
					arrayCornerX = GAP / 2 + BUBBLE_RADIUS;
				}

				int spacingX = 2 * BUBBLE_RADIUS + GAP;
				int spacingY = (int) ((BUBBLE_RADIUS + GAP / 2) * Math.sqrt(3)); // 30-60-90 calculation
				int centerX = arrayCornerX + spacingX * col;
				int centerY = arrayCornerY + spacingY * row;

				// a Bubble is born!!!
				bubs[row][col] = new Bubble(centerX, centerY, BUBBLE_RADIUS);
				info[row][col] = bubs[row][col].getColorChooser();
				// System.out.println("building wall. r = " + row + ", c = " + col + ", info = " + info[row][col]);

				// all bubbles from here down should be non-existent
				if (row >= NUM_BUBBLES_DOWN)
				{
					bubs[row][col].setExists(Bubble.NONEXISTENT);
					info[row][col] = -1;
				}
			}
		}
	}

	public boolean isEmpty()
	{
		// check bubble array
		for (int row = 0; row < TOTAL_BUBBLES_DOWN; row++)
		{
			for (int col = 0; col < NUM_BUBBLES_ACROSS; col++)
			{  
				if (info[row][col] > 0)
				{
					return false;
				}
			}
		}

		return true;
	}
	
	public int getNumBubblesDestroyed()
	{
		return numBubblesDestroyed;
	}

	public Bubble getBubbleAt(int r, int c)
	{
		return bubs[r][c];
	}

	// returns true if the bubble stopped, false otherwise
	public boolean receiveBubble(Bubble b)
	{
		Point loc = computeLocation(b);
		int row = loc.getR();
		int col = loc.getC();

		int span = 2;
		int padding = 5;
		int marginOfError = 2 * BUBBLE_RADIUS + GAP + padding;

		for (int r = row - span; r <= row + span; r++)
		{
			for (int c = col - span; c <= col + span; c++)
			{
				if (isValid(r, c))
				{	
					Bubble cur = bubs[r][c];

					if (cur.exists() && cur.distFrom(b) <= marginOfError)
					{
						setBubble(b, row, col);
						numBubblesDestroyed +=destroyMatchingNeighbors(row, col);
						return true;
					}
				}

			}
		}
		
		// now check to see if bubble is at top of screen, alone
		if (row == 0)
		{
			setBubble(b, row, col);
			// no need to destroyMatchingNeighbors bc there are none
			return true;
			
		}
		
		return false;
	}
	
	public boolean isValid(int row, int col)
	{
		return row >= 0 && col >= 0 && row < bubs.length && col < bubs[0].length;
	}

	public Point computeLocation(Bubble b)
	{

		Point closest = new Point(-1,-1); // return row & col values of -1 if no point is close enough
		double closestDistance = Double.MAX_VALUE;

		for (int row = 0; row < TOTAL_BUBBLES_DOWN; row++)
		{
			for (int col = 0; col < NUM_BUBBLES_ACROSS; col++)
			{
				Bubble cur = bubs[row][col];

				double dist = cur.distFrom(b);
				boolean desiredDistanceRange = dist < BUBBLE_RADIUS + b.getRadius();
				if (desiredDistanceRange && dist < closestDistance)
				{
					closest = new Point(row, col);
					closestDistance = dist;
				}
			}
		}
		return closest;
	}
	
	public int destroyMatchingNeighbors(int row, int col)
	{
		int color = bubs[row][col].getColorChooser();
		
		boolean[][] seen = new boolean[bubs.length][bubs[0].length];
		
		toDelete = bfs(seen, color, row, col);
		
		partiallyDeleteBubbles();

		return toDelete.size(); // return the number of bubbles destroyed

	}
	
	public void partiallyDeleteBubbles()
	{
		for (int[] pos : toDelete)
		{
			int r = pos[0];
			int c = pos[1];
			
			partiallyDeleteBubble(r, c);
		}
	}
	
	public void fullyDeleteBubbles()
	{
		for (int[] pos : toDelete)
		{
			int r = pos[0];
			int c = pos[1];
			
			deleteBubble(r, c);
		}
		
		// wipe toDelete
		toDelete = new ArrayList<>();
	}
	
	public List<int[]> bfs(boolean[][] seen, int color, int row, int col)
	{
		List<int[]> spotsToDelete = new ArrayList<>();
		
		int matchingNeighborCount = 0; 
		
		Queue<int[]> queue = new LinkedList<>();
		queue.offer(new int[] {row, col});
		
		while (!queue.isEmpty())
		{
			// pop off the first element in the queue
			int[] curr = queue.remove();
			int r = curr[0];
			int c = curr[1];
				
			// process this element
			seen[r][c] = true;
			matchingNeighborCount++;
			spotsToDelete.add(new int[] {r, c});
			
			
			// define directions of neighbors
			List<int[]> directions = new ArrayList<>();
			directions.addAll(Arrays.asList(
					new int[] {-1, 0}, 
					new int[] {1, 0}, 
					new int[] {0, -1},
					new int[] {0, 1}));
			
			if (r % 2 == 1) // if row number is odd, this is a right-shifted row
			{
				directions.add(new int[] {-1, 1}); // upper right
				directions.add(new int[] {1, 1}); // lower right
			}
			else // even row number.  this is NOT a right-shifted row
			{
				directions.add(new int[] {-1, -1}); // upper left
				directions.add(new int[] {-1, -1}); // lower left
			}
			
			// explore neighbors
			for (int[] dir : directions)
			{
				
				int newR = r + dir[0];
				int newC = c + dir[1];
				
				// if this neighbor is worth exploring:
				// position is valid, exists, not seen before, 
				// and has matching color
				if (isValid(newR, newC) && 
						!seen[newR][newC] && 
						info[newR][newC] == color)
				{
					queue.offer(new int[] {newR, newC});
				}
			}
		}
		
		if (matchingNeighborCount >= MATCHING_NEIGHBOR_THRESHOLD)
		{
			return spotsToDelete;
		}
		
		// return an empty list if we didn't reach the threshold
		return new ArrayList<>(); 
		
	}
	
	public void deleteBubble(int row, int col)
	{
		bubs[row][col].setExists(Bubble.NONEXISTENT);
		info[row][col] = -1;
	}
	
	public void partiallyDeleteBubble(int row, int col)
	{
		bubs[row][col].setExists(Bubble.PARTIAL);
		info[row][col] = -1;
	}

	public void setBubble(Bubble b, int row, int col)
	{
		bubs[row][col].setExists(Bubble.EXISTS);
		bubs[row][col].setColor(b.getColor());
		info[row][col] = b.getColorChooser();
	}

	public void draw(Graphics g)
	{
		// draw bubble array
		for (int row = 0; row < TOTAL_BUBBLES_DOWN; row++)
		{
			for (int col = 0; col < NUM_BUBBLES_ACROSS; col++)
			{
				bubs[row][col].draw(g);
			}
		}
		
	}

}
