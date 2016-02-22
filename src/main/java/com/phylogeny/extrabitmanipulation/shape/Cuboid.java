package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.SculptSettings;

import net.minecraft.util.BlockPos;

public class Cuboid extends AsymmetricalShape
{
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c)
	{
		super.init(centerX, centerY, centerZ, a, b, c);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float x = getBitPosX(pos, i);
		float y = getBitPosY(pos, j);
		float z = getBitPosZ(pos, k);
		boolean inShape = isPointInsideisCuboid(x, y, z, a, b, c);
		return SculptSettings.SCULPT_HOLLOW_SHAPE ? inShape && !isPointInsideisCuboid(x, y, z, aInset, bInset, cInset) : inShape;
	}
	
	private boolean isPointInsideisCuboid(float x, float y, float z, float a, float b, float c)
	{
		return x <= centerX + a && x >= centerX - a && y <= centerY + b
				&& y >= centerY - b && z <= centerZ + c && z >= centerZ - c;
	}
	
}