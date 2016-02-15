package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

import com.phylogeny.extrabitmanipulation.reference.Utility;

public class Cube extends SymmetricalShape
{
	public Cube(float centerX, float centerY, float centerZ, float semiDiameter)
	{
		super(centerX, centerY, centerZ, semiDiameter);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k, boolean isSolid)
	{
		float x = pos.getX() + i * Utility.pixelF;
		float y = pos.getY() + j * Utility.pixelF;
		float z = pos.getZ() + k * Utility.pixelF;
		boolean inShape = x <= centerX + semiDiameter && x >= centerX - semiDiameter && y <= centerY + semiDiameter
				&& y >= centerY - semiDiameter && z <= centerZ + semiDiameter && z >= centerZ - semiDiameter;
		if (isSolid) return inShape;
		return inShape && !(x <= centerX + semiDiameterInset && x >= centerX - semiDiameterInset && y <= centerY + semiDiameterInset
				&& y >= centerY - semiDiameterInset && z <= centerZ + semiDiameterInset && z >= centerZ - semiDiameterInset);
	}
	
}