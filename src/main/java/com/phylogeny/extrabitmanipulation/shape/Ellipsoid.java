package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

import com.phylogeny.extrabitmanipulation.reference.Utility;

public class Ellipsoid extends AsymmetricalShape
{

	public Ellipsoid(float centerX, float centerY, float centerZ,
			float a, float b, float c)
	{
		super(centerX, centerY, centerZ, a, b, c);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float dx = (pos.getX() + i * Utility.pixelF - centerX) / a;
		float dy = (pos.getY() + j * Utility.pixelF - centerY) / b;
		float dz = (pos.getZ() + k * Utility.pixelF - centerZ) / c;
		return dx * dx + dy * dy + dz * dz <= 1;
	}
	
}