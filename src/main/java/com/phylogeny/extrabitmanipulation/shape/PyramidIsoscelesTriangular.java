package com.phylogeny.extrabitmanipulation.shape;

import com.phylogeny.extrabitmanipulation.reference.Utility;

import net.minecraft.util.BlockPos;

public class PyramidIsoscelesTriangular extends SlopedAsymmetricalShape
{
	private float offsetCenterZ, centerZInset, centerYInset;
	
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
		
		float s1 = this.c - offsetCenterZ;
		float h1 = this.b * 2;
		float a1 = (float) Math.atan(s1 / h1);
		float p1 = ((float) Math.sqrt(s1 * s1 + h1 * h1) * wallThickness) / s1;
		
		float s2 = this.a;
		float a2 = 1.5708F - (float) Math.atan((this.c + offsetCenterZ) / h1);
		float h2 = (float) (Math.sqrt(s2 * s2 + h1 * h1) * Math.cos(a1 - a2));
		float p2 = ((float) Math.sqrt(s2 * s2 + h2 * h2) * wallThickness) / s2;
		
		float dy = (float) (Math.cos(a2) * p2);
		float dz = (float) (Math.sin(a2) * p2);
		
		float apexZ = this.centerZ - offsetCenterZ;
		float apexY = this.centerY + this.b;
		
		float z1 = this.centerZ - this.c;
		float y1 = this.centerY - this.b - p1;
		float z2 = apexZ;
		float y2 = apexY - p1;
		
		float z3 = apexZ - dz;
		float y3 = apexY - dy;
		float z4 = this.centerZ + this.c - dz;
		float y4 = this.centerY - this.b - dy;
		
		float m1 = (y2 - y1) / (z2 - z1);
		float b1 = y1 - m1 * z1;
		float m2 = (y4 - y3) / (z4 - z3);
		float b2 = y3 - m2 * z3;
		
		float interZ = (b2 - b1) / (m1 - m2);
		float interY = m1 * interZ + b1;
		
		float offsetZ = apexZ - interZ;
		float offsetY = apexY - interY;
		
		centerZInset = this.centerZ - (inverted ? -offsetZ : offsetZ);
		centerYInset = this.centerY - (inverted ? -offsetY : offsetY);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, b)) return false;
		float x = getBitPosX(pos, i, j, k);
		float z = getBitPosZ(pos, i, j, k);
		boolean inShape = isPointInPyramid(y, x, z, centerZ, centerY);
		return sculptHollowShape ? inShape && !(isPointInPyramid(y, x, z, centerZInset, centerYInset)
				&& !isPointOffLine(y)) : inShape;
	}
	
	protected boolean isPointInPyramid(float val, float v1, float v2, float center1, float center2)
	{
		float dy = center2 - val;
		float h = dy + (inverted ? -b : b);
		float s1 = (h * a) / height;
		float s2 = (h * c) / height;
		float s3 = ((dy + (inverted ? -cInset2 : cInset2)) * c) / height;
		float center = center1 + (inverted ? (offsetCenterZ * (1 + (s3 / c))) : (-offsetCenterZ * (1 - (s3 / c))));
		return isPointInTriangle(v1, v2, centerX, center, s1, s2, s2);
	}
	
}