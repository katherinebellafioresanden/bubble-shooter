
public class Point
{
	private int r;
	private int c;

	public Point(int rIn, int cIn)
	{
		r = rIn;
		c = cIn;
	}

	public int getR()
	{
		return r;
	}

	public int getC()
	{
		return c;
	}

	public void setR(int newR)
	{
		r = newR;
	}
	public void setC(int newC)
	{
		c = newC;
	}
	
	public boolean equals(Point otherPoint)
	{
		int otherR = otherPoint.getR();
		int otherC = otherPoint.getC();
		
		if (r == otherR && c == otherC)
		{
			return true;
		}
		
		return false;
	}
	
	public String toString()
	{
		String s = "(row = " + r + ", col = " + c + ")";
		return s;
	}
}