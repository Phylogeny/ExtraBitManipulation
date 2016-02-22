package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.SculptSettings;

import net.minecraft.util.BlockPos;

public class Cone extends SymmetricalShape
{
	private float semiDiameterInset2;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float radius)
	{
		super.init(centerX, centerY, centerZ, radius);
		semiDiameterInset2 = semiDiameterInset;
		float r = semiDiameter;
		float d = r * 2;
		semiDiameterInset = r - (float) ((Math.sqrt(r * r + d * d) * SculptSettings.WALL_THICKNESS) / r);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, j);
		if (isPointOffLine(y, centerY, semiDiameter)) return false;
		float dx = getBitPosDiffX(pos, i, centerX);
		float dz = getBitPosDiffZ(pos, k, centerZ);
		double dist = Math.sqrt(dx * dx + dz * dz);
		boolean inShape = isPointInCircle(y, centerY, semiDiameter, semiDiameter, dist);
		return SculptSettings.SCULPT_HOLLOW_SHAPE ? inShape && !(isPointInCircle(y, centerY, semiDiameter, semiDiameterInset, dist)
				&& (SculptSettings.OPEN_ENDS || !(isPointOffLine(y, centerY, semiDiameterInset2)))) : inShape;
	}
	
	private boolean isPointOffLine(float val, float centerVal)
	{
		return val < centerVal - semiDiameterInset || val > centerVal + semiDiameterInset2;
	}
	
	private boolean isPointInCircle(float val, float centerVal, float semiDiameter, float semiDiameter2, double dist)
	{
		float d = semiDiameter * 2;
		return dist <= ((centerVal + semiDiameter2 - val) * semiDiameter) / d;
	}
	
}