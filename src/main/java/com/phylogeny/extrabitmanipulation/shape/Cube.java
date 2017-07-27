package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.math.BlockPos;

import com.phylogeny.extrabitmanipulation.reference.Utility;

public class Cube extends SymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float x = pos.getX() + i * Utility.PIXEL_F;
		float y = pos.getY() + j * Utility.PIXEL_F;
		float z = pos.getZ() + k * Utility.PIXEL_F;
		return isPointInsideisCube(x, y, z, semiDiameter) && (!sculptHollowShape || !isPointInsideisCube(x, y, z, semiDiameterInset));
	}
	
	private boolean isPointInsideisCube(float x, float y, float z, float semiDiameter)
	{
		return x <= centerX + semiDiameter && x >= centerX - semiDiameter && y <= centerY + semiDiameter
				&& y >= centerY - semiDiameter && z <= centerZ + semiDiameter && z >= centerZ - semiDiameter;
	}
	
}