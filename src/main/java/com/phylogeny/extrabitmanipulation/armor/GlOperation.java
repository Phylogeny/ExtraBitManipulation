package com.phylogeny.extrabitmanipulation.armor;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class GlOperation
{
	private final GlOperationType type;
	private float x, y, z, angle;
	
	public static enum GlOperationType
	{
		TRANSLATION("Translation"),
		ROTATION("Rotation"),
		SCALE("Scale");
		
		private String name;
		
		private GlOperationType(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
	}
	
	public GlOperation(GlOperationType type)
	{
		this.type = type;
	}
	
	public GlOperation(GlOperationType type, float x, float y, float z)
	{
		this(type);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public GlOperation(GlOperationType type, float x, float y, float z, float angle)
	{
		this(type, x, y, z);
		this.angle = angle;
	}
	
	public GlOperation(NBTTagCompound nbt)
	{
		type = GlOperationType.values()[nbt.getInteger(NBTKeys.ARMOR_GL_OPERATION_TYPE)];
		x = nbt.getFloat(NBTKeys.ARMOR_GL_OPERATION_X);
		y = nbt.getFloat(NBTKeys.ARMOR_GL_OPERATION_Y);
		z = nbt.getFloat(NBTKeys.ARMOR_GL_OPERATION_Z);
		angle = nbt.getFloat(NBTKeys.ARMOR_GL_OPERATION_ANGLE);
	}
	
	public static GlOperation createTranslation(float x, float y, float z)
	{
		return new GlOperation(GlOperationType.TRANSLATION, x, y, z);
	}
	
	public static GlOperation createRotation(float angle, float x, float y, float z)
	{
		return new GlOperation(GlOperationType.ROTATION, x, y, z, angle);
	}
	
	public static GlOperation createScale(float x, float y, float z)
	{
		return new GlOperation(GlOperationType.SCALE, x, y, z);
	}
	
	public boolean hasData()
	{
		return x != 0 || y != 0 || z != 0 || (type == GlOperationType.ROTATION && angle % 360 != 0);
	}
	
	public void execute()
	{
		switch (type)
		{
			case TRANSLATION:	GlStateManager.translate(x, y, z);
								break;
			case ROTATION:		GlStateManager.rotate(angle, x, y, z);
								break;
			case SCALE:			GlStateManager.scale(x, y, z);				
		}
	}
	
	public static void executeList(List<GlOperation> glOperations)
	{
		for (GlOperation glOperation : glOperations)
			glOperation.execute();
	}
	
	public void saveToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger(NBTKeys.ARMOR_GL_OPERATION_TYPE, type.ordinal());
		nbt.setFloat(NBTKeys.ARMOR_GL_OPERATION_X, x);
		nbt.setFloat(NBTKeys.ARMOR_GL_OPERATION_Y, y);
		nbt.setFloat(NBTKeys.ARMOR_GL_OPERATION_Z, z);
		nbt.setFloat(NBTKeys.ARMOR_GL_OPERATION_ANGLE, angle);
	}
	
	public static void saveListToNBT(NBTTagCompound nbt, String key, List<GlOperation> glOperations)
	{
		NBTTagList glOperationsNbt = new NBTTagList();
		for (GlOperation glOperation : glOperations)
		{
			NBTTagCompound glOperationNbt = new NBTTagCompound();
			glOperation.saveToNBT(glOperationNbt);
			glOperationsNbt.appendTag(glOperationNbt);
		}
		nbt.setTag(key, glOperationsNbt);
	}
	
	public static void loadListFromNBT(NBTTagCompound nbt, String key, List<GlOperation> glOperations)
	{
		glOperations.clear();
		NBTTagList glOperationsNbt = nbt.getTagList(key, NBT.TAG_COMPOUND);
		for (int i = 0; i < glOperationsNbt.tagCount(); i++)
			glOperations.add(new GlOperation(glOperationsNbt.getCompoundTagAt(i)));
	}
	
	public GlOperationType getType()
	{
		return type;
	}
	
	public float getX()
	{
		return x;
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public void setZ(float z)
	{
		this.z = z;
	}
	
	public float getAngle()
	{
		return angle;
	}
	
	public void setAngle(float angle)
	{
		this.angle = angle;
	}
	
}