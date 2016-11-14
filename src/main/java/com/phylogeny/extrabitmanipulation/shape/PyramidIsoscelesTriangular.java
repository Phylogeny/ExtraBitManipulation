package com.phylogeny.extrabitmanipulation.shape;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PyramidIsoscelesTriangular extends AsymmetricalShape
{
	private float offsetCenter, center1Inset, center2Inset, insetMin, insetMax, insetMin2, insetMax2, height, cInset2;
	private boolean isTwisted, isFlipped;
	
	@Override
	public void init(float centerX, float centerY, float centerZ, float a, float b, float c,
			int direction, boolean sculptHollowShape, float wallThickness, boolean openEnds)
	{
		int rotation = direction / 6;
		direction %= 6;
		super.init(centerX, centerY, centerZ, a, b, c, direction, sculptHollowShape, wallThickness, openEnds);
		isTwisted = rotation % 2 == (direction > 3 ? 0 : 1);
		isFlipped = rotation % 2 == 0 ? (direction % 2 == (rotation == 0 ? 0 : 1))
				: (rotation == 1 ? (direction != 2 && direction != 3) : (direction == 2 || direction == 3));
		if (isEquilateral)
		{
			float contract, s, h;
			if (isTwisted)
			{
				contract = this.a - this.a * (float) Math.cos(0.523599);
				this.centerX -= inverted != isFlipped ? -contract : contract;
				this.a -= contract;
				aInset = reduceLength(this.a);
				s = this.a - this.a / 3.0F;
				h = this.a * 2;
			}
			else
			{
				contract = this.c - this.c * (float) Math.cos(0.523599);
				this.centerZ -= inverted != isFlipped ? -contract : contract;
				this.c -= contract;
				cInset = reduceLength(this.c);
				s = this.c - this.c / 3.0F;
				h = this.c * 2;
			}
			contract = this.b - (float) Math.sqrt(h * h - s * s) * 0.5F;
			this.centerY -= inverted ? -contract : contract;
			this.b -= contract;
			bInset = reduceLength(this.b);
		}
		b = this.b;
		float bInset = this.bInset;
		centerY = this.centerY;
		if (isTwisted)
		{
			a = this.c;
			c = this.a;
			centerZ = this.centerX;
		}
		else
		{
			a = this.a;
			c = this.c;
			centerZ = this.centerZ;
		}
		height = b * 2;
		float hsq = height * height;
		cInset2 = b - (float) ((Math.sqrt(c * c + hsq) * wallThickness) / c);
		insetMax = centerY + bInset;
		insetMin = centerY - bInset;
		if (a <= c)
		{
			offsetCenter = c / 3.0F;
		}
		else
		{
			float h = c * 2;
			hsq = h * h;
			float w = a * 2;
			float wsq = w * w;
			offsetCenter = c - (w * ((float) Math.sqrt(wsq + 4 * hsq) - w)) / (4 * h);
		}
		Vec3d offset = getInnerTriangularPyramidOffset(centerY, centerZ, offsetCenter, a, b, c, wallThickness);
		float offsetZ = (float) (inverted ? -offset.zCoord : offset.zCoord);
		center1Inset = centerZ - (isFlipped ? -offsetZ : offsetZ);
		center2Inset = centerY - (float) (inverted ? -offset.yCoord : offset.yCoord);
		if (isFlipped)
			offsetCenter *= -1;
		
		insetMax2 = center2Inset + b;
		insetMin2 = center2Inset - b;
	}
	
	private Vec3d getInnerTriangularPyramidOffset(float centerY, float centerZ, float offsetCenter, float a, float b, float c, float wallThickness)
	{
		float s1 = c - offsetCenter;
		float h1 = b * 2;
		float a1 = (float) Math.atan(s1 / h1);
		float p1 = ((float) Math.sqrt(s1 * s1 + h1 * h1) * wallThickness) / s1;
		
		float s2 = a;
		float a2 = 1.5708F - (float) Math.atan((c + offsetCenter) / h1);
		float h2 = (float) (Math.sqrt(s2 * s2 + h1 * h1) * Math.cos(a1 - a2));
		float p2 = ((float) Math.sqrt(s2 * s2 + h2 * h2) * wallThickness) / s2;
		
		float dy = (float) (Math.cos(a2) * p2);
		float dz = (float) (Math.sin(a2) * p2);
		
		float apexZ = centerZ - offsetCenter;
		float apexY = centerY + b;
		
		float z1 = centerZ - c;
		float y1 = centerY - b - p1;
		float z2 = apexZ;
		float y2 = apexY - p1;
		
		float z3 = apexZ - dz;
		float y3 = apexY - dy;
		float z4 = centerZ + c - dz;
		float y4 = centerY - b - dy;
		
		float m1 = (y2 - y1) / (z2 - z1);
		float b1 = y1 - m1 * z1;
		float m2 = (y4 - y3) / (z4 - z3);
		float b2 = y3 - m2 * z3;
		
		float interZ = (b2 - b1) / (m1 - m2);
		float interY = m1 * interZ + b1;
		
		return new Vec3d(0, apexY - interY, apexZ - interZ);
	}
	
	@Override
	public boolean isPointInsideShape(BlockPos pos, int i, int j, int k)
	{
		float y = getBitPosY(pos, i, j, k);
		if (isPointOffLine(y, centerY, b)) return false;
		float x = getBitPosX(pos, i, j);
		float z = getBitPosZ(pos, j, k);
		if (isTwisted)
		{
			boolean inShape = isPointInPyramid(y, z, x, centerX, centerY);
			return sculptHollowShape ? inShape && !(isPointInPyramid(y, z, x, center1Inset, center2Inset) && !isPointOffLine(y)) : inShape;
		}
		boolean inShape = isPointInPyramid(y, x, z, centerZ, centerY);
		return sculptHollowShape ? inShape && !(isPointInPyramid(y, x, z, center1Inset, center2Inset) && !isPointOffLine(y)) : inShape;
	}
	
	protected boolean isPointInPyramid(float val, float v1, float v2, float center1, float center2)
	{
		float dy = center2 - val;
		float h = dy + (inverted ? -b : b);
		float s1 = (h * a) / height;
		float s2 = (h * c) / height;
		if (isTwisted)
		{
			float s3 = ((dy + (inverted ? -cInset2 : cInset2)) * a) / height;
			float center = center1 + (inverted ? (offsetCenter * (1 + (s3 / a))) : (-offsetCenter * (1 - (s3 / a))));
			return isPointInTriangle(v1, v2, centerZ, center, s2, isFlipped ? -s1 : s1);
		}
		float s3 = ((dy + (inverted ? -cInset2 : cInset2)) * c) / height;
		float center = center1 + (inverted ? (offsetCenter * (1 + (s3 / c))) : (-offsetCenter * (1 - (s3 / c))));
		return isPointInTriangle(v1, v2, centerX, center, s1, isFlipped ? -s2 : s2);
	}
	
	protected boolean isPointOffLine(float val)
	{
		return inverted ? (!openEnds && val > insetMax) || val < insetMin2 : (!openEnds && val < insetMin) || val > insetMax2;
	}
	
}