package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.Utility;

import net.minecraft.util.BlockPos;

public class PyramidIsoscelesTriangular extends SlopedAsymmetricalShape
{
	private float offsetCenterZ;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int rotation, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		super.init(centerX, centerY, centerZ, a, b, c, rotation, sculptHollowShape, wallThickness, openEnds);
		if (this.a < this.c)
		{
			offsetCenterZ = this.c / 3.0F;
		}
		else
		{
			float h = this.c * 2;
			float hsq = h * h;
			float w = this.a * 2;
			float wsq = w * w;
			offsetCenterZ = this.c - (w * ((float) Math.sqrt(wsq + 4 * hsq) - w)) / (4 * h);
		}
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, b)) return false;
		float x = getBitPosX(pos, i, j, k);
		float z = getBitPosZ(pos, i, j, k);
		boolean inShape = isPointInPyramid(y, x, z, false);
		return sculptHollowShape ? inShape && !(isPointInPyramid(y, x, z, true)
				&& !isPointOffLine(y)) : inShape;
	}
	
	protected boolean isPointInPyramid(float val, float v1, float v2, boolean contract)
	{
		//TODO
		return false;
	}
	
}