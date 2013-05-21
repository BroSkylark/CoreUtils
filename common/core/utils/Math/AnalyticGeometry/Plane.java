package core.utils.Math.AnalyticGeometry;

import java.util.ArrayList;
import java.util.List;

public class Plane
{
	private Point origin;
	private Vector support[];
	private List<Area> fields;
	
	public Plane(Point p1, Point p2, Point p3) { this(p1, new Vector(p1, p2), new Vector(p1, p3)); }
	public Plane(Point o, Vector v1, Vector v2)
	{
		origin = o.clone();
		support = new Vector[2];
		support[0] = v1.clone();
		support[1] = v2.clone();
		fields = new ArrayList<Area>();
	}
	
	private Plane(Plane p)
	{
		origin = p.origin.clone();
		support = new Vector[2];
		support[0] = p.support[0].clone();
		support[1] = p.support[1].clone();
		fields = new ArrayList<Area>(p.fields.size());
		for(Area area : p.fields)
		{
			fields.add(area);
		}
	}
	
	public Point get(double k, double l)
	{
		return origin.clone().apply(support[0].clone().scale(k)).apply(support[1].clone().scale(l));
	}
	
	public void addField(Action a, Point[] p)
	{
		if(a != null && p != null && p.length >= 2)
		{
			for(Point point : p)
			{
				if(!contains(point))
				{
					return;
				}
			}
			
			fields.add(new Area(a, p));
		}
	}
	
	public void hit(Point p)
	{
		if(p != null && contains(p))
		{
			for(Area area : fields)
			{
				if(area.isHit(p))
				{
					area.action();
				}
			}
		}
	}
	
	public Plane apply(Vector v)
	{
		origin.apply(v);
		return this;
	}
	
	public boolean contains(Point p)
	{
		Vector v = new Vector(origin, p);
		Vector w = support[0].crossProduct(support[1]);
		
		return v.dotProduct(w) == 0.0d;
	}
	
	public Line intersects(Plane plane)
	{
		if(isParallel(plane)) return null;
		
		Point p1 = plane.intersects(new Line(origin, support[0]));
		Point p2 = plane.intersects(new Line(origin, support[1]));

		if(p1 == null) p1 = plane.intersects(new Line(origin, support[0].clone().add(support[1])));
		if(p2 == null) p2 = plane.intersects(new Line(origin, support[0].clone().add(support[1])));
		
		return (p1 != null && p2 != null) ? new Line(p1, p2) : null;
	}
	
	public Point intersects(Line line)
	{
		try
		{
			Vector q = new Vector(origin), e1 = support[0].clone(), e2 = support[1].clone();
			Vector p = new Vector(line.getOrigin()), v = line.getSupport();
			int i = 0;
			double l = Double.NaN;
			
			do
			{
				if(++i > 3)
				{
					break;
				}
				
				l = intersectImpl(q, e1, e2, p, v);
				q.rotateXYZ();
				e1.rotateXYZ();
				e2.rotateXYZ();
				p.rotateXYZ();
				v.rotateXYZ();
			}
			while(Double.isNaN(l));
			
			if(Double.isNaN(l))
			{
				return null;
			}

			return line.get(l);
		}
		catch(Exception e)
		{
		    e.printStackTrace();
			return null;
		}
	}
	
	private double intersectImpl(Vector q, Vector e1, Vector e2, Vector p, Vector v)
	{
		double a = (e2.dY() - e2.dX() * v.dY() / v.dX()) / (e1.dX() * v.dY() / v.dX() - e1.dY());
		double b = (q.dY() - p.dY() - (q.dX() - p.dX()) * v.dY() / v.dX()) / (e1.dX() * v.dY() / v.dX() - e1.dY());
		double c = a * e1.dX() / v.dX() + e2.dX() / v.dX();
		double d = b * e1.dX() / v.dX() + (q.dX() - p.dX()) / v.dX();
		double f = (q.dZ() - p.dZ() - d * v.dZ() + b * e1.dZ()) / (c * v.dZ() - e2.dZ() - a * e1.dZ());
		double l = f * c + d;
		
		return l;
	}
	
	public Plane rotateAlong(Line line, double angle)
	{
		Point p1 = line.rotateAlong(origin, angle),
			  p2 = line.rotateAlong(origin.clone().apply(support[0]), angle),
			  p3 = line.rotateAlong(origin.clone().apply(support[1]), angle);
		
		if(p1 == null || p2 == null || p3 == null)
		{
			return null;
		}
		
		origin = p1;
		support[0] = new Vector(p1, p2);
		support[1] = new Vector(p1, p3);
		
		return this;
	}
	
	public boolean isParallel(Plane plane)
	{
		return getNormal().equalsIgnoreScale(plane.getNormal());
	}
	
	public Vector getNormal()
	{
		return support[0].crossProduct(support[1]);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Plane)
		{
			Plane p = (Plane) o;
			return isParallel(p) && contains(p.origin);
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return 1;
	}
	
	@Override
	public Plane clone()
	{
		return new Plane(this);
	}
	
	@Override
	public String toString()
	{
		return "E: x = " + origin.toString() + " + k * " + support[0].toString() + " + l * " + support[1].toString();
	}
	
	private class Area
	{
		private Action action;
		private Point border[];
		
		public Area(Action a, Point[] p)
		{
			action = a;
			border = p.clone();
		}
		
		public void action()
		{
			action.onHit();
		}
		
		public boolean isHit(Point p)
		{
			int i = 1, j = 2;
			
			if(border.length < 2) return false;
			
			if(border.length == 2)
			{
				return border[0].distanceTo(border[1]) >= border[0].distanceTo(p);
			}
			
			do
			{
				if(hasHitTriangle(border[0], border[i], border[j], p))
				{
					return true;
				}
				
				if(i < j) i += 2;
				else j += 2;
			}
			while(i < border.length && j < border.length);
			
			return false;
		}
		
		private boolean hasHitTriangle(Point p1, Point p2, Point p3, Point q)
		{
//			Vector v1 = new Vector(p1, p2), v2 = new Vector(p1, p3);
//			Vector w = new Vector(p1, q);
//			
//			if(w.equalsIgnoreScale(v1)) {ModManager.out("identical"); return w.magnitude() <= v1.magnitude(); }
//			if(w.equalsIgnoreScale(v2)) {ModManager.out("identical"); return w.magnitude() <= v2.magnitude(); }
//			
//			double theta = inB(v1.getAngle(v2) * 180.0d / Math.PI, 0, 360);
//			double alpha = inB(w.getAngle(v1) * 180.0d / Math.PI, 0, 360);
//			double beta = inB(w.getAngle(v2) * 180.0d / Math.PI, 0, 360);
//			
//			if(alpha > theta || beta > theta)
//			{
//				ModManager.out("Angle FAIL: " + alpha + ", " + beta + ", " + theta);
//				return false;
//			}
//			
//			Line g = new Line(p2, p3), h = new Line(p1, q);
//			Point s = g.intersects(h);
//
//			if(!contains(g.get(0)) || !contains(g.get(1)) || !contains(h.get(0)) || !contains(h.get(1)) ||
//					g.isParallel(h))
//			{
//				ModManager.out("Error:\t" + g.toString() + " and\n\t\t" + h.toString() + " do not intersect!");
//			}
//			
//			if(s != null)
//			{
//				ModManager.out("Outside triangle: " + w.magnitude() + " > " + p1.distanceTo(s));
//				return w.magnitude() <= p1.distanceTo(s);
//			}
//			else
//			{
//			}
			
			return false;
		}
	}
	
	public static interface Action
	{
		public abstract void onHit();
	}
	
	public static final double inB(double d, double min, double max)
	{
		double inc = max - min;
		while(d > max) d -= inc;
		while(d < min) d += inc;
		return d;
	}
}
