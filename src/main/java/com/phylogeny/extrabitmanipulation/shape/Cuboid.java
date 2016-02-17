package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cuboid extends AsymmetricalShape
{

	public void init(float centerX, float centerY, float centerZ,
			float a, float b, float c, float wallThickness, boolean isSolid)
	{
		super.init(centerX, centerY, centerZ, a, b, c, wallThickness, isSolid);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float x = getBitPos(pos, i);
		float y = getBitPos(pos, j);
		float z = getBitPos(pos, k);
		boolean inShape = isPointInsideisCuboid(x, y, z, a, b, c);
		return isSolid ? inShape : inShape && !isPointInsideisCuboid(x, y, z, aInset, bInset, cInset);
	}
	
	private boolean isPointInsideisCuboid(float x, float y, float z, float a, float b, float c)
	{
		return x <= centerX + a && x >= centerX - a && y <= centerY + b
				&& y >= centerY - b && z <= centerZ + c && z >= centerZ - c;
	}
	
}