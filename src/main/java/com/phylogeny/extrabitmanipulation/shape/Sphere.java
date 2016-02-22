package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.SculptSettings;

import net.minecraft.util.BlockPos;

public class Sphere extends SymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius)
	{
		super.init(centerX, centerY, centerZ, radius);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float dx = getBitPosDiffX(pos, i, centerX);
		float dy = getBitPosDiffY(pos, j, centerY);
		float dz = getBitPosDiffZ(pos, k, centerZ);
		double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
		return SculptSettings.SCULPT_HOLLOW_SHAPE ? dist <= semiDiameter && dist > semiDiameterInset : dist <= semiDiameter;
	}
	
}