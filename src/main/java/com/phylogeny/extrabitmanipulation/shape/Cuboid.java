package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

import com.phylogeny.extrabitmanipulation.reference.Utility;

public class Cuboid extends AsymmetricalShape
{

	public Cuboid(float centerX, float centerY, float centerZ,
			float a, float b, float c)
	{
		super(centerX, centerY, centerZ, a, b, c);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k, boolean isSolid)
	{
		float x = pos.getX() + i * Utility.pixelF;
		float y = pos.getY() + j * Utility.pixelF;
		float z = pos.getZ() + k * Utility.pixelF;
		boolean inShape = x <= centerX + a && x >= centerX - a && y <= centerY + b
				&& y >= centerY - b && z <= centerZ + c && z >= centerZ - c;
		if (isSolid) return inShape;
		return inShape && !(x <= centerX + aInset && x >= centerX - aInset && y <= centerY + bInset
				&& y >= centerY - bInset && z <= centerZ + cInset && z >= centerZ - cInset);
	}
	
}