package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cuboid extends AsymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float x = getBitPosX(pos, i, j);
		float y = getBitPosY(pos, i, j, k);
		float z = getBitPosZ(pos, j, k);
		boolean inShape = isPointInsideisCuboid(x, y, z, a, b, c);
		return sculptHollowShape ? inShape && !isPointInsideisCuboid(x, y, z, aInset, bInset, cInset) : inShape;
	}
	
	private boolean isPointInsideisCuboid(float x, float y, float z, float a, float b, float c)
	{
		return x <= centerX + a && x >= centerX - a && y <= centerY + b
				&& y >= centerY - b && z <= centerZ + c && z >= centerZ - c;
	}
	
}