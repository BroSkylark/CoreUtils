package core.utils.Math.AnalyticGeometry;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class PointBlock 
{
	public static final PointBlock origin = new PointBlock(0, 0, 0);
	private int x, y, z;
	
	public PointBlock(int i, int j, int k)
	{
		set(i, j, k);
	}
	
	public PointBlock(Point p)
	{
		set(MathHelper.floor_double(p.X()), MathHelper.floor_double(p.Y()), MathHelper.floor_double(p.Z()));
	}
	
	public PointBlock(double x, double y, double z)
	{
		this(new Point(x, y, z));
	}
	
	public PointBlock(PointBlock b)
	{
		x = b.x;
		y = b.y;
		z = b.z;
	}

	@Override
	public int hashCode()
	{
		return x + y + z;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof PointBlock)
		{
			PointBlock p = (PointBlock) o;
			return x == p.x && y == p.y && z == p.z;
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return "P(" + Integer.valueOf(x).toString() + "/" + Integer.valueOf(y).toString() + 
			"/" + Integer.valueOf(z).toString() + ")";
	}
	
	@Override
	public PointBlock clone()
	{
		return new PointBlock(this);
	}
	
	public PointBlock apply(int dx, int dy, int dz)
	{
		x += dx;
		y += dy;
		z += dz;
		
		return this;
	}
	
	public PointBlock apply(VectorBlock v)
	{
		x += v.dX();
		y += v.dY();
		z += v.dZ();
		
		return this;
	}
	
	public BiomeGenBase getBiome(IBlockAccess iblockaccess)
	{
		return iblockaccess.getBiomeGenForCoords(x, z);
	}
	
	public boolean isOpaqueCube(IBlockAccess iba)
	{
		return iba.isBlockOpaqueCube(x, y, z);
	}
	
	public int getBlockId(IBlockAccess iba)
	{
		return iba.getBlockId(x, y, z);
	}
	
	public int getBlockMetadata(IBlockAccess iba)
	{
		return iba.getBlockMetadata(x, y, z);
	}
	
	public Material getBlockMaterial(IBlockAccess iba)
	{
		return iba.getBlockMaterial(x, y, z);
	}
	
	public TileEntity getBlockTileEntity(IBlockAccess iba)
	{
		return iba.getBlockTileEntity(x, y, z);
	}
	
	public void setBlock(World world, int id)
	{
		world.setBlock(x, y, z, id);
	}
	
	public void setBlockMetadata(World world, int m)
	{
		world.setBlockMetadataWithNotify(x, y, z, m, 3);
	}
	
	public void setBlockAndMetadata(World world, int id, int m)
	{
		world.setBlock(x, y, z, id, m, 3);
	}
	
	public void setBlockTileEntity(World world, TileEntity tileentity)
	{
		world.setBlockTileEntity(x, y, z, tileentity);
	}
	
	public void removeBlockTileEntity(World world)
	{
		world.removeBlockTileEntity(x, y, z);
	}
	
	public Point center()
	{
		return new Point(x + 0.5d, y + 0.5d, z + 0.5d);
	}
	
	public Point toPoint()
	{
		return new Point(x, y, z);
	}
	
	public PointBlock getAdjacent(int i)
	{
		return clone().moveInDir(i);
	}
	
	public PointBlock moveInDir(int i)
	{
		return set(x + (i == 4 ? -1 : i == 5 ? 1 : 0), 
				   y + (i == 0 ? -1 : i == 1 ? 1 : 0), 
				   z + (i == 2 ? -1 : i == 3 ? 1 : 0));
	}
	
	public static VectorBlock getVecFromFace(int i)
	{
		return VectorBlock.deriveFromFace(i);
	}
	
	public static int getFaceFromVec(VectorBlock v)
	{
		v.elementarize().normalize();

		switch((int) v.dX())
		{
			case 1:  return 5;
			case -1: return 4;
		}

		switch((int) v.dY())
		{
			case 1:  return 1;
			case -1: return 0;
		}

		switch((int) v.dZ())
		{
			case 1:  return 3;
			case -1: return 2;
		}
		
		return 6;
	}

	public int X() { return x; }
	public int Y() { return y; }
	public int Z() { return z; }
	
	public PointBlock set(int i, int j, int k)
	{		
		x = i;
		y = j;
		z = k;
		
		return this;
	}

	public PointBlock setX(int x)
	{
		this.x = x;
		
		return this;
	}

	public PointBlock setY(int y)
	{
		this.y = y;
		
		return this;
	}

	public PointBlock setZ(int z)
	{
		this.z = z;
		
		return this;
	}

	public static PointBlock readFromNBT(NBTTagCompound nbt) 
	{
		int x = nbt.getInteger("x");
		int y = nbt.getInteger("y");
		int z = nbt.getInteger("z");

		return new PointBlock(x, y, z);
	}
	
	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();

		nbttagcompound.setInteger("x", x);
		nbttagcompound.setInteger("y", y);
		nbttagcompound.setInteger("z", z);
		
		return nbttagcompound;
	}

	public boolean isInCube(Point goal) 
	{
		return x <= goal.X() && x + 1 > goal.X() && y <= goal.Y() && y + 1 > goal.Y() && z <= goal.Z() && z + 1 > goal.Z();
	}
	
	public List<PointBlock> getAllBlocks(World world, int r, int id)
	{
		List<PointBlock> result = new ArrayList<PointBlock>();

		for(int i = x - r ; i <= x + r ; i++)
		{
			for(int j = y - r ; j <= y + r ; j++)
			{
				for(int k = z - r ; k <= z + r ; k++)
				{
					if(world.getBlockId(i, j, k) == id)
						result.add(new PointBlock(i, j, k));
				}
			}
		}
		
		return result;
	}

	public boolean isBlockNormal(World world) 
	{
		return world.isBlockOpaqueCube(x, y, z);
	}
}
