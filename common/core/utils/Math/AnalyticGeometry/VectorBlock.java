package core.utils.Math.AnalyticGeometry;

import net.minecraft.util.Facing;

public class VectorBlock
{
	public static final VectorBlock nullVector = new VectorBlock(0, 0, 0);
	private int dX, dY, dZ;
	private double magnitude;
	
	public VectorBlock(int dx, int dy, int dz)
	{
		set(dx, dy, dz);
	}
	
	public VectorBlock(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		set(x2 - x1, y2 - y1, z2 - z1);
	}
	
	public VectorBlock(PointBlock p1, PointBlock p2)
	{
		set(p2.X() - p1.X(), p2.Y() - p1.Y(), p2.Z() - p1.Z());
	}
	
	private VectorBlock(VectorBlock v)
	{
		dX = v.dX;
		dY = v.dY;
		dZ = v.dZ;
		magnitude = v.magnitude;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof VectorBlock)
		{
			VectorBlock v = (VectorBlock) o;
			return v.dX == dX && v.dY == dY && v.dZ == dZ;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return dX + dY + dZ;
	}
	
	@Override
	public String toString()
	{
		return "(" + Integer.valueOf(dX).toString() + "|" + Integer.valueOf(dY).toString() + 
		"|" + Integer.valueOf(dZ).toString() + "), |v| == " + Double.valueOf(magnitude).toString();
	}
	
	@Override
	public VectorBlock clone()
	{
		return new VectorBlock(this);
	}
	
	public VectorBlock negate()
	{
		dX = -dX;
		dY = -dY;
		dZ = -dZ;
		
		return this;
	}
	
	public VectorBlock scale(int s)
	{
		return set(dX * s, dY * s, dZ * s);
	}
	
	public VectorBlock normalize()
	{
		return set(dX == 0 ? 0 : 1, dY == 0 ? 0 : 1, dZ == 0 ? 0 : 1);
	}
	
	public VectorBlock elementarize()
	{
		double dx = dX * dX, dy = dY * dY, dz = dZ * dZ;
		
		if(dx >= dy)
		{
			if(dx >= dz)
				return set(dX, 0, 0);
			else
				return set(0, 0, dZ);
		}
		else
		{
			if(dy >= dz)
				return set(0, dY, 0);
			else
				return set(0, 0, dZ);
		}
	}

	public Vector toVector() 
	{
		return new Vector(dX, dY, dZ);
	}

	public VectorBlock setX(int dx) { return set(dx, dY, dZ); }
	public VectorBlock setY(int dy) { return set(dX, dy, dZ); }
	public VectorBlock setZ(int dz) { return set(dX, dY, dz); }
	
	public VectorBlock set(int dx, int dy, int dz)
	{
		dX = dx;
		dY = dy;
		dZ = dz;
		
		magnitude = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
		
		return this;
	}

	public int dX() { return dX; }
	public int dY() { return dY; }
	public int dZ() { return dZ; }
	public double magnitude() { return magnitude; }

	public static VectorBlock deriveFromFace(int i) 
	{
		return new VectorBlock(Facing.offsetsXForSide[i], Facing.offsetsYForSide[i], Facing.offsetsZForSide[i]);
	}
}
