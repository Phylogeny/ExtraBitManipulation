package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

import com.phylogeny.extrabitmanipulation.reference.Utility;

public class Sphere extends SymmetricalShape
{

	public Sphere(float centerX, float centerY, float centerZ, float radius)
	{
		super(centerX, centerY, centerZ, radius);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k, boolean isSolid)
	{
		float dx = pos.getX() + i * Utility.pixelF - centerX;
		float dy = pos.getY() + j * Utility.pixelF - centerY;
		float dz = pos.getZ() + k * Utility.pixelF - centerZ;
		double val = Math.sqrt(dx * dx + dy * dy + dz * dz);
		boolean inShape = val <= semiDiameter;
		if (isSolid) return inShape;
		return inShape && val > semiDiameterInset;
	}
	
}