package com.phylogeny.extrabitmanipulation.helper;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;
import com.phylogeny.extrabitmanipulation.reference.Utility;

public class BitAreaHelper
{
	
	public static Vec3d getBitGridOffset(EnumFacing side, boolean inside, float hitX, float hitY, float hitZ, boolean removeBits)
	{
		float x = 0, y = 0, z = 0;
		x = hitX < (Math.round(hitX/Utility.PIXEL_F) * Utility.PIXEL_F) ? 1 : -1;
		y = hitY < (Math.round(hitY/Utility.PIXEL_F) * Utility.PIXEL_F) ? 1 : -1;
		z = hitZ < (Math.round(hitZ/Utility.PIXEL_F) * Utility.PIXEL_F) ? 1 : -1;
		double offsetX = Math.abs(side.getFrontOffsetX());
		double offsetY = Math.abs(side.getFrontOffsetY());
		double offsetZ = Math.abs(side.getFrontOffsetZ());
		if (side.getAxisDirection() == AxisDirection.NEGATIVE)
		{
			if (offsetX > 0)
				x *= -1;
			
			if (offsetY > 0)
				y *= -1;
			
			if (offsetZ > 0)
				z *= -1;
		}
		boolean su = side == EnumFacing.UP || side == EnumFacing.SOUTH;
		if (removeBits ? (!inside || !su) : (inside && su))
		{
			if (offsetX > 0)
				x *= -1;
			
			if (offsetY > 0)
				y *= -1;
			
			if (offsetZ > 0)
				z *= -1;
		}
		return new Vec3d(x, y, z);
	}
	
	public static boolean readBlockStates(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Vec3d hit, Vec3i drawnStartPoint)
	{
		ItemModelingTool modelingTool = (ItemModelingTool) (stack != null && stack.getItem() instanceof ItemModelingTool ? stack.getItem() : null);
		if (modelingTool == null)
			return false;
		
		modelingTool.initialize(stack);
		ModelingBoxSet boxSet = getModelingToolBoxSet(player, stack, pos.getX(), pos.getY(), pos.getZ(), hit, drawnStartPoint, false);
		if (boxSet.isEmpty())
			return false;
		
		NBTTagCompound nbt = stack.getTagCompound();
		BitIOHelper.saveBlockStates(ChiselsAndBitsAPIAccess.apiInstance, player, world, boxSet.getBoundingBox(), nbt);
		if (BitToolSettingsHelper.getModelGuiOpen(player, nbt))
			player.openGui(ExtraBitManipulation.instance, GuiIDs.MODELING_TOOL_BIT_MAPPING, player.worldObj, 0, 0, 0);
		
		return true;
	}
	
	public static ModelingBoxSet getModelingToolBoxSet(EntityPlayer player, ItemStack stack, int x, int y, int z,
			Vec3d hit, Vec3i drawnStartPointModelingTool, boolean addToBoxForRender)
	{
		int modeArea = BitToolSettingsHelper.getModelAreaMode(player, stack.getTagCompound());
		AxisAlignedBB boxBounding = null;
		AxisAlignedBB boxPoint = null;
		if (modeArea == 2)
		{
			if (drawnStartPointModelingTool != null)
			{
				int x2 = drawnStartPointModelingTool.getX();
				int y2 = drawnStartPointModelingTool.getY();
				int z2 = drawnStartPointModelingTool.getZ();
				if (addToBoxForRender)
				{
					if (Math.max(x, x2) == x)
					{
						x++;
					}
					else
					{
						x2++;
					}
					if (Math.max(y, y2) == y)
					{
						y++;
					}
					else
					{
						y2++;
					}
					if (Math.max(z, z2) == z)
					{
						z++;
					}
					else
					{
						z2++;
					}
				}
				boxBounding = new AxisAlignedBB(x2, y2, z2,
						Math.abs(x2 - x) <= 16 ? x : (x2 - x > 0 ? x2 - 16 : x2 + 16),
						Math.abs(y2 - y) <= 16 ? y : (y2 - y > 0 ? y2 - 16 : y2 + 16),
						Math.abs(z2 - z) <= 16 ? z : (z2 - z > 0 ? z2 - 16 : z2 + 16));
			}
		}
		else
		{
			int hitX = (int) Math.round(hit.xCoord);
			int hitY = (int) Math.round(hit.yCoord);
			int hitZ = (int) Math.round(hit.zCoord);
			boxBounding = new AxisAlignedBB(hitX, hitY, hitZ, hitX, hitY, hitZ);
			boxPoint = boxBounding.expandXyz(0.005);
			boxBounding = boxBounding.expandXyz(8);
			if (modeArea == 1)
			{
				float yaw = Math.abs(player.rotationYaw) % 360;
				int greaterX = 8;
				int lesserX = -8;
				if (player.rotationYaw < 0)
				{
					greaterX *= -1;
					lesserX *= -1;
				}
				int greaterZ = -8;
				int lesserZ = 8;
				int angleX = 180;
				int angleZ = 90;
				EnumFacing side = player.getHorizontalFacing();
				if (player.rotationYaw > 0 ? side.getAxisDirection() == AxisDirection.POSITIVE
						: side == EnumFacing.SOUTH || side == EnumFacing.WEST)
				{
					greaterZ *= -1;
					lesserZ *= -1;
					if (side == (player.rotationYaw > 0 ? EnumFacing.EAST : EnumFacing.WEST))
					{
						lesserX *= -1;
						angleZ = 270;
					}
					else
					{
						angleZ = 0;
					}
				}
				boxBounding = boxBounding.offset(yaw > angleX ? greaterX : lesserX,
						player.rotationPitch > 0 ? -8 : 8, yaw > angleZ ? greaterZ : lesserZ);
			}
			int modeSnapToChunk = BitToolSettingsHelper.getModelSnapMode(player, stack.getTagCompound());
			if (modeSnapToChunk > 0)
			{
				if (x < 0)
					x -= 15;
				
				if (z < 0)
					z -= 15;
				
				x -= x % 16;
				z -= z % 16;
				boolean snapY = modeSnapToChunk == 2;
				if (snapY)
				{
					y += 1;
					y -= y % 16;
				}
				else
				{
					y = (int) boxBounding.minY;
				}
				double offsetX = x - boxBounding.minX;
				double offsetY = y - boxBounding.minY;
				double offsetZ = z - boxBounding.minZ;
				boxBounding = boxBounding.offset(offsetX, offsetY, offsetZ);
				boxPoint = boxPoint.offset(offsetX, offsetY, offsetZ);
			}
		}
		return new ModelingBoxSet(boxBounding, boxPoint);
	}
	
	public static class ModelingBoxSet
	{
		private AxisAlignedBB boxBounding, boxPoint;
		
		public ModelingBoxSet(@Nullable AxisAlignedBB boxBounding, @Nullable AxisAlignedBB boxPoint)
		{
			this.boxBounding = boxBounding;
			this.boxPoint = boxPoint;
		}
		
		public AxisAlignedBB getBoundingBox()
		{
			return boxBounding;
		}
		
		public AxisAlignedBB getPoint()
		{
			return boxPoint;
		}
		
		public boolean hasPoint()
		{
			return boxPoint != null;
		}
		
		public boolean isEmpty()
		{
			return boxBounding == null && boxPoint == null;
		}
		
	}
	
}