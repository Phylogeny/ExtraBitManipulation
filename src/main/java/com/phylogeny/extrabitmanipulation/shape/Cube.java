package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.math.BlockPos;

public class Cube extends SymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float x = getBitPosX(pos, i, j);
		float y = getBitPosY(pos, i, j, k);
		float z = getBitPosZ(pos, j, k);
		boolean inShape = isPointInsideisCube(x, y, z, semiDiameter);
		return sculptHollowShape ? inShape && !isPointInsideisCube(x, y, z, semiDiameterInset) : inShape;
	}
	
	private boolean isPointInsideisCube(float x, float y, float z, float semiDiameter)
	{
		return x <= centerX + semiDiameter && x >= centerX - semiDiameter && y <= centerY + semiDiameter
				&& y >= centerY - semiDiameter && z <= centerZ + semiDiameter && z >= centerZ - semiDiameter;
	}
	
}