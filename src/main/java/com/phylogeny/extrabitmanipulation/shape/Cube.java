package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Cube extends SymmetricalShape
{
	public void init(float centerX, float centerY, float centerZ, float semiDiameter, float wallThickness, boolean isSolid)
	{
		super.init(centerX, centerY, centerZ, semiDiameter, wallThickness, isSolid);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float x = getBitPos(pos, i);
		float y = getBitPos(pos, j);
		float z = getBitPos(pos, k);
		boolean inShape = isPointInsideisCube(x, y, z, semiDiameter);
		return isSolid ? inShape : inShape && !isPointInsideisCube(x, y, z, semiDiameterInset);
	}
	
	private boolean isPointInsideisCube(float x, float y, float z, float semiDiameter)
	{
		return x <= centerX + semiDiameter && x >= centerX - semiDiameter && y <= centerY + semiDiameter
				&& y >= centerY - semiDiameter && z <= centerZ + semiDiameter && z >= centerZ - semiDiameter;
	}
	
}