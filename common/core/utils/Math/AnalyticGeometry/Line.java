package core.utils.Math.AnalyticGeometry;

public class Line
{
	private static final int[][] rotate = new int[][] {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
	private Point origin;
	private Vector support;

	public Line(Point p1, Point p2) { this(p1, new Vector(p1, p2)); }
	public Line(Point o, Vector v)
	{
		origin = o.clone();
		support = v.clone();
	}

	public Point get(double x)
	{
		return origin.clone().apply(support.clone().scale(x));
	}

	public Point getOrigin()
	{
		return origin.clone();
	}

	public Vector getSupport()
	{
		return support.clone();
	}

	public Point intersects(Line line)
	{
		Vector a = new Vector(origin), v = support.clone();
		Vector b = new Vector(line.origin), w = line.support.clone();
		double l = Double.NaN, rot[][] = new double[][] {{a.dX(), a.dY(), a.dZ()}, 
				{v.dX(), v.dY(), v.dZ()}, {b.dX(), b.dY(), v.dZ()}, {w.dX(), w.dY(), w.dZ()}};
		int i = 0;

		do
		{
			if(++i >= 6)
			{
				break;
			}

			l = intersectsImpl(
					new Vector(rot[0][rotate[i][0]], rot[0][rotate[i][1]], rot[0][rotate[i][2]]),
					new Vector(rot[1][rotate[i][0]], rot[1][rotate[i][1]], rot[1][rotate[i][2]]),
					new Vector(rot[2][rotate[i][0]], rot[2][rotate[i][1]], rot[2][rotate[i][2]]),
					new Vector(rot[3][rotate[i][0]], rot[3][rotate[i][1]], rot[3][rotate[i][2]]));

		} while(Double.isNaN(l) || Double.isInfinite(l));
		
		if(Double.isNaN(l) || Double.isInfinite(l))
		{
			(new RuntimeException("ERR: Line Intercept: " + toString() + ";\n\t\t\t" + 
			        line.toString())).printStackTrace(System.err);
			return null;
		}
		
		Point r = get(l);
		
		if(!r.isValid())
		{
			
		}

		return r;
	}

	private double intersectsImpl(Vector p, Vector v, Vector q, Vector w)
	{
		double a = (q.dX() - p.dX()) / v.dX();
		double k = (p.dY() - q.dY() + a * v.dY()) / (w.dY() - w.dX() * v.dY() / v.dX());
		double l = k * w.dX() / v.dX() + a;

		return l;
	}

	public Point rotateAlong(Point point, double angle)
	{
		try
		{
			Vector o = new Vector(origin);
			Vector s = support.clone();
			Vector p = new Vector(point);

			angle = angle * Math.PI / 180.0d;

			double l = (s.dX() * (p.dX() - o.dX()) + s.dY() * (p.dY() - o.dY()) + s.dZ() * (p.dZ() - o.dZ())) / 
					(s.dX() * s.dX() + s.dY() * s.dY() + s.dZ() * s.dZ());

			Vector v = p.clone().add(o.clone().negate()).add(s.clone().scale(-l));
			Vector ps = o.clone().add(s.clone().scale(l)).add(v.crossProduct(s).scale(Math.sin(angle)))
					.add(v.clone().scale(Math.cos(angle)));

			return new Point(ps.dX(), ps.dY(), ps.dZ());
		}
		catch(Exception e)
		{
			(new RuntimeException("Failed to rotate point!")).printStackTrace(System.err);
			return null;
		}
	}

	public boolean isParallel(Line line)
	{
		return support.equalsIgnoreScale(line.support);
	}

	public boolean contains(Point p)
	{
		return support.equalsIgnoreScale(new Vector(origin, p));
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Line)
		{
			Line l = (Line) o;
			return contains(l.origin) && isParallel(l);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "g: x = " + origin.toString() + " k * " + support.toString();
	}
}
