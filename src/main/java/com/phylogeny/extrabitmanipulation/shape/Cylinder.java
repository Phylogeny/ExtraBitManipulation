package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.SculptSettings;

import net.minecraft.util.BlockPos;

public class Cylinder extends SymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius)
	{
		super.init(centerX, centerY, centerZ, radius);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, j);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, centerX);
		float dz = getBitPosDiffZ(pos, k, centerZ);
		float dist = dx * dx + dz * dz;
		boolean inShape = isPointInCircle(semiDiameter, dist);
		return SculptSettings.SCULPT_HOLLOW_SHAPE ? inShape && !(isPointInCircle(semiDiameterInset, dist)
				&& (SculptSettings.OPEN_ENDS || !(isPointOffLine(y, centerY, semiDiameterInset)))) : inShape;
	}
	
	private boolean isPointInCircle(float semiDiameter, float dist)
	{
		return dist / (semiDiameter * semiDiameter) <= 1;
	}
	
}