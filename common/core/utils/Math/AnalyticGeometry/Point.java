package core.utils.Math.AnalyticGeometry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class Point 
{
	public static final Point origin = new Point(0, 0, 0);
	private double x, y, z;
	
	public Point(double x, double y, double z)
	{
		set(x, y, z);
	}
	
	private Point(Point p)
	{
		x = p.x;
		y = p.y;
		z = p.z;
	}
	
	@Override
	public int hashCode()
	{
		return (int) (x + y + z);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Point)
		{
			Point p = (Point) o;
			return x == p.x && y == p.y && z == p.z;
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
	    return String.format("P(%.3f/%.3f/%.3f)", x, y, z);
	}
	
	@Override
	public Point clone()
	{
		return new Point(this);
	}
	
	public AxisAlignedBB getBoundingBox(Vector v) { return getBoundingBox(clone().apply(v)); }
	public AxisAlignedBB getBoundingBox(Point p)
	{
		Point min = new Point(Math.min(x, p.x), Math.min(y, p.y), Math.min(z, p.z));
		Point max = new Point(Math.max(x, p.x), Math.max(y, p.y), Math.max(z, p.z));
		
		return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
	}
	
	public double distanceTo(Point p)
	{
		return (new Vector(this, p)).magnitude();
	}
	
	public boolean isValid()
	{
		return !Double.isInfinite(x) && !Double.isNaN(x) && !Double.isInfinite(y) && !Double.isNaN(y)
				&& !Double.isInfinite(z) && !Double.isNaN(z);
	}
	
	public double distanceSqaredTo(Point p)
	{
		double dx = x - p.x, dy = y - p.y;
		return dx * dx + dy * dy;
	}
	
	public Point apply(double dx, double dy, double dz)
	{
		x += dx;
		y += dy;
		z += dz;
		
		return this;
	}
	
	public Point apply(Vector v)
	{
		x += v.dX();
		y += v.dY();
		z += v.dZ();
		
		return this;
	}
	
	public Point forceRound(int i)
	{
		double s = 1;
		while(i-- > 0) s *= 10.0d;
		
		x = Math.round(x * s) / s;
		y = Math.round(y * s) / s;
		z = Math.round(z * s) / s;
		
		return this;
	}
	
	public PointBlock toBlock()
	{
		return new PointBlock(this);
	}
	
	public boolean isCloseTo(Point p) { return isCloseTo(p, 0.1d); }
	public boolean isCloseTo(Point p, double d)
	{
		return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y) + (p.z - z) * (p.z - z) <= d * d;
	}

	public double X() { return x; }
	public double Y() { return y; }
	public double Z() { return z; }
	
	public Point set(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;

		return this;
	}

	public Point setX(double x)
	{
		this.x = x;
		
		return this;
	}

	public Point setY(double y)
	{
		this.y = y;
		
		return this;
	}

	public Point setZ(double z)
	{
		this.z = z;
		
		return this;
	}

	public static Point readFromNBT(NBTTagCompound nbt) 
	{
		double x = nbt.getDouble("x");
		double y = nbt.getDouble("y");
		double z = nbt.getDouble("z");

		return new Point(x, y, z);
	}
	
	public NBTTagCompound toNBT()
	{
	    NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", x);
		nbt.setDouble("y", y);
		nbt.setDouble("z", z);
		return nbt;
	}

	public Vec3 toVec3() 
	{
		return Vec3.createVectorHelper(x, y, z);
	}
	
	public static Point getPlayerEyeLocation(EntityPlayer player)
	{
	    return new Point(player.posX, player.posY + (!player.worldObj.isRemote ? 1.62f : 0), player.posZ);
	}
}
