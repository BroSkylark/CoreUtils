package core.utils.Math.AnalyticGeometry;

import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Vector 
{
	public static final Vector nullVector = new Vector(0, 0, 0), 
									   eX = new Vector(1, 0, 0), 
									   eY = new Vector(0, 1, 0), 
									   eZ = new Vector(0, 0, 1);
	private double dX, dY, dZ;
	private double magnitude;
	
	public Vector(double dx, double dy, double dz)
	{
		set(dx, dy, dz);
	}
	
	public Vector(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		set(x2 - x1, y2 - y1, z2 - z1);
	}
	
	public Vector(Point p1, Point p2)
	{
		set(p2.X() - p1.X(), p2.Y() - p1.Y(), p2.Z() - p1.Z());
	}
	
	private Vector(Vector v)
	{
		dX = v.dX;
		dY = v.dY;
		dZ = v.dZ;
		magnitude = v.magnitude;
	}
	
	public Vector(Point p) 
	{
		set(p.X(), p.Y(), p.Z());
	}
	
	public static Vector getVecFromPitchAndYaw(float pitch, float yaw)
	{
		double motionX = -MathHelper.sin((yaw / 180F) * 3.141593F) * MathHelper.cos((pitch / 180F) * 3.141593F);
        double motionY = -MathHelper.sin(((pitch) / 180F) * 3.141593F);
        double motionZ = MathHelper.cos((yaw / 180F) * 3.141593F) * MathHelper.cos((pitch / 180F) * 3.141593F);

        return (new Vector(motionX, motionY, motionZ)).normalize();
	}
	
	public float[] getPitchAndYaw()
	{
		float pitch, yaw;
		
		pitch = (float) -Math.asin(dY % 1.0f);
		yaw = (float) Math.acos((dX % 1.0f) / MathHelper.cos(pitch));
		
		pitch *= 180f / Math.PI;
		yaw   *= 180f / Math.PI;
		
		return new float[] {pitch, yaw};
	}
	
	public Vector rotateXYZ()
	{
		return set(dY, dZ, dX);
	}
	
	public boolean equalsIgnoreScale(Vector v)
	{
		return clone().normalize().equals(v.clone().normalize()) || 
				clone().normalize().equals(v.clone().normalize().negate());
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Vector)
		{
			Vector v = (Vector) o;
			return v.dX == dX && v.dY == dY && v.dZ == dZ;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return (int) (dX + dY + dZ);
	}
	
	@Override
	public String toString()
	{
		return "v(" + dX + "|" + dY + "|" + dZ + "), |v| == " + round(magnitude, 3).toString();
	}
	
	private Double round(double d, int s)
	{
		double i = 1;
		while(s-- > 0)
		{
			i *= 10;
		}
		
		return Double.valueOf(((int) (d * i)) / i);
	}
	
	@Override
	public Vector clone()
	{
		return new Vector(this);
	}
	
	public double getAngle(Vector vector)
	{
		if(vector == null || magnitude == 0 || vector.magnitude == 0)
			return 0;
		
		return Math.acos(dotProduct(vector) / (magnitude * vector.magnitude));
	}
	
	public double dotProduct(Vector vector)
	{
		return vector == null ? 0 : (dX * vector.dX + dY * vector.dY + dZ * vector.dZ);
	}
	
	public Vector crossProduct(Vector v)
	{
		return new Vector(dY * v.dZ - dZ * v.dY, dZ * v.dX - dX * v.dZ, dX * v.dY - dY * v.dX);
	}
	
	public Vector add(Vector vector)
	{
		return set(dX + vector.dX, dY + vector.dY, dZ + vector.dZ);
	}
	
	public Vector negate()
	{
		dX = -dX;
		dY = -dY;
		dZ = -dZ;
		
		return this;
	}
	
	public Vector normalize()
	{
		if(magnitude == 0)
			return this;
		else
			return scale(1 / magnitude);
	}
	
	public Vector scale(double s)
	{
		return set(dX * s, dY * s, dZ * s);
	}
	
	public Vector elementarize()
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
	
	public Vector round(int d)
	{
		return set(round(dX, d), round(dY, d), round(dZ, d));
	}

	public Vector setX(double dx) { return set(dx, dY, dZ); }
	public Vector setY(double dy) { return set(dX, dy, dZ); }
	public Vector setZ(double dz) { return set(dX, dY, dz); }	
	
	public Vector set(double dx, double dy, double dz)
	{
		dX = dx;
		dY = dy;
		dZ = dz;
		
		magnitude = Math.sqrt(dx * dx + dy * dy + dz * dz);
		
		if(Double.isNaN(magnitude))
		{
			(new RuntimeException("ERROR: NaN whilst calculating magnitude of " + 
			        toString())).printStackTrace(System.err);
		}
		
		return this;
	}
	
	public double dX() { return dX; }
	public double dY() { return dY; }
	public double dZ() { return dZ; }
	public double magnitude() { return magnitude; }

	public static Vector deriveFromFace(int i) 
	{
		return new Vector(Facing.offsetsXForSide[i], Facing.offsetsYForSide[i], Facing.offsetsZForSide[i]);
	}

	public VectorBlock toBlockVector() 
	{
		return new VectorBlock(MathHelper.floor_double(dX), MathHelper.floor_double(dY), MathHelper.floor_double(dZ));
	}
	
	public int getFace()
	{
		elementarize();

		for(int i = 0 ; i < 6 ; i++)
		{
			if(Facing.offsetsXForSide[i] != 0 && Facing.offsetsXForSide[i] == dX)
				return Facing.offsetsXForSide[i];
			if(Facing.offsetsYForSide[i] != 0 && Facing.offsetsYForSide[i] == dY)
				return Facing.offsetsYForSide[i];
			if(Facing.offsetsZForSide[i] != 0 && Facing.offsetsZForSide[i] == dZ)
				return Facing.offsetsZForSide[i];
		}
		
		return 6;
	}

	public Vector add(double dx, double dy, double dz)
	{
		return set(dX + dx, dY + dy, dZ + dz);
	}

	public Vec3 toVec3()
	{
		return Vec3.createVectorHelper(dX, dY, dZ);
	}
}
