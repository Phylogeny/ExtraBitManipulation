package com.phylogeny.extrabitmanipulation.shape;

import java.util.Random;

import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Utility;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Shape
{
	protected int rotation;
	protected float centerX, centerY, centerZ, wallThickness;
	protected boolean sculptHollowShape, openEnds, inverted;
	
	public void init(float centerX, float centerY, float centerZ, int rotation,
			boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		this.rotation = rotation;
		this.sculptHollowShape = sculptHollowShape;
		this.wallThickness = wallThickness;
		this.openEnds = openEnds;
		float v;
		if (rotation > 1)
		{
			if (rotation > 3)
			{
				v = centerX;
				centerX = centerY;
				centerY = v;
			}
			else
			{
				v = centerZ;
				centerZ = centerY;
				centerY = v;
			}
		}
		this.centerX = centerX; 
		this.centerY = centerY;
		this.centerZ = centerZ;
		inverted = rotation % 2 == 0;
	}
	
	public boolean isBlockInsideShape(BlockPos pos)
	{
		if (sculptHollowShape) return false;
		for (int i = 0; i < 16; i += 15)
		{
			for (int j = 0; j < 16; j += 15)
			{
				for (int k = 0; k < 16; k += 15)
				{
					if (!isPointInsideShape(pos, i, j, k))
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		return false;
	}
	
	public Vec3 getRandomInternalPoint(World world, BlockPos pos)
	{
		AxisAlignedBB bounds = getBoundingBox();
		if (bounds != null)
		{
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			Block block = world.getBlockState(pos).getBlock();
			AxisAlignedBB blockBounds = new AxisAlignedBB((double)pos.getX() + block.getBlockBoundsMinX(),
					(double)pos.getY() + block.getBlockBoundsMinY(), (double)pos.getZ() + block.getBlockBoundsMinZ(),
					(double)pos.getX() + block.getBlockBoundsMaxX(), (double)pos.getY() + block.getBlockBoundsMaxY(),
					(double)pos.getZ() + block.getBlockBoundsMaxZ());
			if (blockBounds.getAverageEdgeLength() == 0)
			{
				blockBounds = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
			}
			AxisAlignedBB box = getIntersectingBox(blockBounds, bounds);
			if (box != null)
			{
				float s = Configs.bitSpawnBoxContraction;
				if (s > 0)
				{
					box = box.contract((box.maxX - box.minX) * s, (box.maxY - box.minY) * s, (box.maxZ - box.minZ) * s);
				}
				double d0 = (double)((world.rand.nextFloat() * (box.maxX - box.minX)) + box.minX);
				double d1 = (double)((world.rand.nextFloat() * (box.maxY - box.minY)) + box.minY);
				double d2 = (double)((world.rand.nextFloat() * (box.maxZ - box.minZ)) + box.minZ);
				return new Vec3(d0, d1, d2);
			}
		}
		return null;
	}
	
	protected AxisAlignedBB getBoundingBox()
	{
		return null;
	}

	private AxisAlignedBB getIntersectingBox(AxisAlignedBB box1, AxisAlignedBB box2)
	{
		if (box1.minX > box2.maxX || box2.minX > box1.maxX
				|| box1.minY > box2.maxY || box2.minY > box1.maxY
				|| box1.minZ > box2.maxZ || box2.minZ > box1.maxZ)
		{
			return null;
		}
		double minX = Math.max(box1.minX, box2.minX);
		double minY = Math.max(box1.minY, box2.minY);
		double minZ = Math.max(box1.minZ, box2.minZ);
		double maxX = Math.min(box1.maxX, box2.maxX);
		double maxY = Math.min(box1.maxY, box2.maxY);
		double maxZ = Math.min(box1.maxZ, box2.maxZ);
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	protected float reduceLength(float value)
	{
		return Utility.PIXEL_F < value ? value - wallThickness : 0.0000000001F;
	}
	
	protected float getBitPosDiffX(BlockPos pos, int x, int y, int z, float center)
	{
		return getBitPosX(pos, x, y, z) - center;
	}
	
	protected float getBitPosDiffY(BlockPos pos, int x, int y, int z, float center)
	{
		return getBitPosY(pos, x, y, z) - center;
	}
	
	protected float getBitPosDiffZ(BlockPos pos, int x, int y, int z, float center)
	{
		return getBitPosZ(pos, x, y, z) - center;
	}
	
	protected float getBitPosX(BlockPos pos, int x, int y, int z)
	{
		return (rotation > 3 ? pos.getY() + y * Utility.PIXEL_F : pos.getX() + x * Utility.PIXEL_F);
	}
	
	protected float getBitPosY(BlockPos pos, int x, int y, int z)
	{
		return (rotation < 2 ? pos.getY() + y * Utility.PIXEL_F : (rotation > 3 ? pos.getX() + x * Utility.PIXEL_F : pos.getZ() + z * Utility.PIXEL_F));
	}
	
	protected float getBitPosZ(BlockPos pos, int x, int y, int z)
	{
		return (rotation == 2 || rotation == 3 ? pos.getY() + y * Utility.PIXEL_F : pos.getZ() + z * Utility.PIXEL_F);
	}
	
	protected boolean isPointOffLine(float val, float centerVal, float semiDiameter)
	{
		return val < centerVal - semiDiameter || val > centerVal + semiDiameter;
	}
	
}