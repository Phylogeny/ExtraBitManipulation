package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.BlockPos;

public class Sphere extends SymmetricalShape
{
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float dx = getBitPosDiffX(pos, i, j, k, centerX);
		float dy = getBitPosDiffY(pos, i, j, k, centerY);
		float dz = getBitPosDiffZ(pos, i, j, k, centerZ);
		double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
		return sculptHollowShape ? dist <= semiDiameter && dist > semiDiameterInset : dist <= semiDiameter;
	}
	
}