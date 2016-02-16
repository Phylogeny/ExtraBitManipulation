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
		float vx = pos.getX() + i * Utility.pixelF - centerX;
		float vy = pos.getY() + j * Utility.pixelF - centerY;
		float vz = pos.getZ() + k * Utility.pixelF - centerZ;
		float dx = vx / a;
		float dy = vy / b;
		float dz = vz / c;
		boolean inShape = dx * dx + dy * dy + dz * dz <= 1;
		if (isSolid) return inShape;
		dx = vx / aInset;
		dy = vy / bInset;
		dz = vz / cInset;
		return inShape && (dx * dx + dy * dy + dz * dz >= 1);
	}
	
}