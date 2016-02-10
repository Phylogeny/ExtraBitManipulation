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
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float x = pos.getX() + i * Utility.pixelF;
		float y = pos.getY() + j * Utility.pixelF;
		float z = pos.getZ() + k * Utility.pixelF;
		return x <= centerX + a && x >= centerX - a && y <= centerY + b
				&& y >= centerY - b && z <= centerZ + c && z >= centerZ - c;
	}
	
}