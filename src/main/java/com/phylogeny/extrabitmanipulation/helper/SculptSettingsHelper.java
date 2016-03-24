package com.phylogeny.extrabitmanipulation.helper;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.config.ConfigSculptSettingBoolean;
import com.phylogeny.extrabitmanipulation.config.ConfigSculptSettingInt;
import com.phylogeny.extrabitmanipulation.config.ConfigSculptSettingBitStack;
import com.phylogeny.extrabitmanipulation.extendedproperties.SculptSettingsPlayerProperties;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.packet.PacketSetBitStack;
import com.phylogeny.extrabitmanipulation.packet.PacketSetHollowShape;
import com.phylogeny.extrabitmanipulation.packet.PacketSetEndsOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetRotation;
import com.phylogeny.extrabitmanipulation.packet.PacketSetSemiDiameter;
import com.phylogeny.extrabitmanipulation.packet.PacketSetShapeType;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTargetBitGridVertexes;
import com.phylogeny.extrabitmanipulation.packet.PacketSetWallThickness;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;
import com.phylogeny.extrabitmanipulation.shape.Shape;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SculptSettingsHelper
{
	
	public static NBTTagCompound initNBT(ItemStack stack)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt;
	}
	
	private static int getInt(NBTTagCompound nbt, int intValue, String key)
	{
		if (nbt != null && nbt.hasKey(key))
		{
			intValue = nbt.getInteger(key);
		}
		return intValue;
	}
	
	private static void setInt(EntityPlayer player, ItemStack stack, int intValue, String key)
	{
		NBTTagCompound nbt = initNBT(stack);
		nbt.setInteger(key, intValue);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	private static boolean getBoolean(NBTTagCompound nbt, boolean booleanValue, String key)
	{
		if (nbt != null && nbt.hasKey(key))
		{
			booleanValue = nbt.getBoolean(key);
		}
		return booleanValue;
	}
	
	private static void setBoolean(EntityPlayer player, ItemStack stack, boolean booleanValue, String key)
	{
		NBTTagCompound nbt = initNBT(stack);
		nbt.setBoolean(key, booleanValue);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	private static ItemStack getStack(NBTTagCompound nbt, ItemStack stackToGet, String key)
	{
		if (nbt != null && nbt.hasKey(key))
		{
			stackToGet = ItemStackHelper.loadStackFromNBT(nbt, key);
		}
		return stackToGet;
	}
	
	private static void setStack(EntityPlayer player, ItemStack stack, ItemStack stackToSet, String key)
	{
		NBTTagCompound nbt = initNBT(stack);
		ItemStackHelper.saveStackToNBT(nbt, stackToSet, key);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	public static int getMode(EntityPlayer player, NBTTagCompound nbt)
	{
		int mode = Configs.sculptMode.getDefaultValue();
		if (Configs.sculptMode.isPerTool())
		{
			mode = getInt(nbt, mode, NBTKeys.MODE);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				mode = sculptProp.mode;
			}
		}
		return mode;
	}
	
	public static void setMode(EntityPlayer player, ItemStack stack, int mode)
	{
		World world = player.worldObj;
		if (Configs.sculptMode.isPerTool())
		{
			if (!world.isRemote)
			{
				setInt(player, stack, mode, NBTKeys.MODE);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				sculptProp.mode = mode;
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetMode(mode));
		}
	}
	
	public static int getRotation(EntityPlayer player, NBTTagCompound nbt)
	{
		int rotation = Configs.sculptRotation.getDefaultValue();
		if (Configs.sculptRotation.isPerTool())
		{
			rotation = getInt(nbt, rotation, NBTKeys.ROTATION);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				rotation = sculptProp.rotation;
			}
		}
		return rotation;
	}
	
	public static void setRotation(EntityPlayer player, ItemStack stack, int rotation)
	{
		World world = player.worldObj;
		if (Configs.sculptRotation.isPerTool())
		{
			if (!world.isRemote)
			{
				setInt(player, stack, rotation, NBTKeys.ROTATION);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				sculptProp.rotation = rotation;
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetRotation(rotation));
		}
	}
	
	public static int getShapeType(EntityPlayer player, NBTTagCompound nbt, boolean isCurved)
	{
		ConfigSculptSettingInt shapeTypeConfig = isCurved ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat;
		int shapeType = shapeTypeConfig.getDefaultValue();
		if (shapeTypeConfig.isPerTool())
		{
			shapeType = getInt(nbt, shapeType, NBTKeys.SHAPE_TYPE);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				shapeType = isCurved ? sculptProp.shapeTypeCurved : sculptProp.shapeTypeFlat;
			}
		}
		return isCurved && shapeType > 2 ? Configs.sculptShapeTypeCurved.getDefaultValue()
				: (!isCurved && shapeType < 3 ? Configs.sculptShapeTypeFlat.getDefaultValue() : shapeType);
	}
	
	public static void setShapeType(EntityPlayer player, ItemStack stack, boolean isCurved, int shapeType)
	{
		World world = player.worldObj;
		if ((isCurved ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat).isPerTool())
		{
			if (!world.isRemote)
			{
				setInt(player, stack, shapeType, NBTKeys.SHAPE_TYPE);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				if (isCurved)
				{
					sculptProp.shapeTypeCurved = shapeType;
				}
				else
				{
					sculptProp.shapeTypeFlat = shapeType;
				}
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetShapeType(isCurved, shapeType));
		}
	}
	
	public static boolean isBitGridTargeted(EntityPlayer player, NBTTagCompound nbt)
	{
		boolean targetBitGridVertexes = Configs.sculptTargetBitGridVertexes.getDefaultValue();
		if (Configs.sculptTargetBitGridVertexes.isPerTool())
		{
			targetBitGridVertexes = getBoolean(nbt, targetBitGridVertexes, NBTKeys.TARGET_BIT_GRID_VERTEXES);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				targetBitGridVertexes = sculptProp.targetBitGridVertexes;
			}
		}
		return targetBitGridVertexes;
	}
	
	public static void setBitGridTargeted(EntityPlayer player, ItemStack stack, boolean targetBitGridVertexes)
	{
		World world = player.worldObj;
		if (Configs.sculptTargetBitGridVertexes.isPerTool())
		{
			if (!world.isRemote)
			{
				setBoolean(player, stack, targetBitGridVertexes, NBTKeys.TARGET_BIT_GRID_VERTEXES);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				sculptProp.targetBitGridVertexes = targetBitGridVertexes;
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetTargetBitGridVertexes(targetBitGridVertexes));
		}
	}
	
	public static int getSemiDiameter(EntityPlayer player, NBTTagCompound nbt)
	{
		int semiDiameter = Configs.sculptSemiDiameter.getDefaultValue();
		if (Configs.sculptSemiDiameter.isPerTool())
		{
			if (nbt != null && nbt.hasKey(NBTKeys.SCULPT_SEMI_DIAMETER))
			{
				semiDiameter = nbt.getInteger(NBTKeys.SCULPT_SEMI_DIAMETER);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				semiDiameter = sculptProp.sculptSemiDiameter;
			}
		}
		return semiDiameter;
	}
	
	public static void setSemiDiameter(EntityPlayer player, ItemStack stack, int semiDiameter)
	{
		World world = player.worldObj;
		if (Configs.sculptSemiDiameter.isPerTool())
		{
			if (!world.isRemote)
			{
				setInt(player, stack, semiDiameter, NBTKeys.SCULPT_SEMI_DIAMETER);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				sculptProp.sculptSemiDiameter = semiDiameter;
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetSemiDiameter(semiDiameter));
		}
	}
	
	public static boolean isHollowShape(EntityPlayer player, NBTTagCompound nbt, boolean isWire)
	{
		ConfigSculptSettingBoolean hollowShapeConfig = isWire ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade;
		boolean hollowShape = hollowShapeConfig.getDefaultValue();
		if (hollowShapeConfig.isPerTool())
		{
			hollowShape = getBoolean(nbt, hollowShape, NBTKeys.SCULPT_HOLLOW_SHAPE);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				hollowShape = isWire ? sculptProp.sculptHollowShapeWire : sculptProp.sculptHollowShapeSpade;
			}
		}
		return hollowShape;
	}
	
	public static void setHollowShape(EntityPlayer player, ItemStack stack, boolean isWire, boolean hollowShape)
	{
		World world = player.worldObj;
		if ((isWire ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade).isPerTool())
		{
			if (!world.isRemote)
			{
				setBoolean(player, stack, hollowShape, NBTKeys.SCULPT_HOLLOW_SHAPE);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				if (isWire)
				{
					sculptProp.sculptHollowShapeWire = hollowShape;
				}
				else
				{
					sculptProp.sculptHollowShapeSpade = hollowShape;
				}
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetHollowShape(hollowShape, isWire));
		}
	}
	
	public static boolean areEndsOpen(EntityPlayer player, NBTTagCompound nbt)
	{
		boolean openEnds = Configs.sculptOpenEnds.getDefaultValue();
		if (Configs.sculptOpenEnds.isPerTool())
		{
			openEnds = getBoolean(nbt, openEnds, NBTKeys.OPEN_ENDS);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				openEnds = sculptProp.openEnds;
			}
		}
		return openEnds;
	}
	
	public static void setEndsOpen(EntityPlayer player, ItemStack stack, boolean openEnds)
	{
		World world = player.worldObj;
		if (Configs.sculptOpenEnds.isPerTool())
		{
			if (!world.isRemote)
			{
				setBoolean(player, stack, openEnds, NBTKeys.OPEN_ENDS);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				sculptProp.openEnds = openEnds;
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetEndsOpen(openEnds));
		}
	}
	
	public static int getWallThickness(EntityPlayer player, NBTTagCompound nbt)
	{
		int wallThickness = Configs.sculptWallThickness.getDefaultValue();
		if (Configs.sculptWallThickness.isPerTool())
		{
			wallThickness = getInt(nbt, wallThickness, NBTKeys.WALL_THICKNESS);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				wallThickness = sculptProp.wallThickness;
			}
		}
		return wallThickness;
	}
	
	public static void setWallThickness(EntityPlayer player, ItemStack stack, int wallThickness)
	{
		World world = player.worldObj;
		if (Configs.sculptWallThickness.isPerTool())
		{
			if (!world.isRemote)
			{
				setInt(player, stack, wallThickness, NBTKeys.WALL_THICKNESS);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				sculptProp.wallThickness = wallThickness;
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetWallThickness(wallThickness));
		}
	}
	
	public static ItemStack getBitStack(EntityPlayer player, NBTTagCompound nbt, boolean isWire)
	{
		ConfigSculptSettingBitStack bitStackConfig = isWire ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade;
		ItemStack bitStack = bitStackConfig.getDefaultValue();
		if (bitStackConfig.isPerTool())
		{
			bitStack = getStack(nbt, bitStack, NBTKeys.SET_BIT);
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				bitStack = isWire ? sculptProp.setBitWire : sculptProp.setBitSpade;
			}
		}
		return bitStack;
	}
	
	public static void setBitStack(EntityPlayer player, ItemStack stack, boolean isWire, ItemStack bitStack)
	{
		World world = player.worldObj;
		if ((isWire ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade).isPerTool())
		{
			if (!world.isRemote)
			{
				setStack(player, stack, bitStack, NBTKeys.SET_BIT);
			}
		}
		else
		{
			SculptSettingsPlayerProperties sculptProp = SculptSettingsPlayerProperties.get(player);
			if (sculptProp != null)
			{
				if (isWire)
				{
					sculptProp.setBitWire = bitStack;
				}
				else
				{
					sculptProp.setBitSpade = bitStack;
				}
			}
		}
		if (world.isRemote)
		{
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetBitStack(isWire, bitStack));
		}
	}
	
	public static String getModeText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getModeText(getMode(player, nbt));
	}
	
	public static String getModeText(int mode)
	{
		return "Mode: " + ItemSculptingTool.MODE_TITLES[mode].toLowerCase();
	}
	
	public static String getRotationText(EntityPlayer player, NBTTagCompound nbt, boolean showRoll)
	{
		return getRotationText(getRotation(player, nbt), showRoll);
	}
	
	public static String getRotationText(int rotation, boolean showRoll)
	{
		String text = "Rotation: " + EnumFacing.getFront(rotation % 6).getName().toLowerCase();
		if (showRoll)
		{
			int roll = rotation / 6;
			if (roll > 0)
			{
				text += " (roll " +  (roll * 90) + "\u00B0)";
			}
		}
		return text;
	}
	
	public static String getShapeTypeText(EntityPlayer player, NBTTagCompound nbt, ItemSculptingTool item)
	{
		return getShapeTypeText(getShapeType(player, nbt, item.isCurved()));
	}
	
	public static String getShapeTypeText(int shapeType)
	{
		return "Shape: " + Shape.SHAPE_NAMES[shapeType].toLowerCase();
	}
	
	public static String getBitGridTargetedText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getBitGridTargetedText(isBitGridTargeted(player, nbt));
	}
	
	public static String getBitGridTargetedText(boolean targetBitGrid)
	{
		return "Targeting: " + (targetBitGrid ? "bit grid vertiecies" : "bits");
	}

	public static String getSemiDiameterText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getSemiDiameterText(player, nbt, getSemiDiameter(player, nbt));
	}
	
	public static String getSemiDiameterText(EntityPlayer player, NBTTagCompound nbt, int semiDiameter)
	{
		double size = semiDiameter;
		boolean targetBitGrid = isBitGridTargeted(player, nbt);
		String diameterText = "Semi-Diameter: ";
		if (Configs.displayNameDiameter)
		{
			size = size * 2;
			if (!targetBitGrid) size += 1;
			diameterText = diameterText.substring(5);
		}
		else
		{
			if (!targetBitGrid) size += 0.5;
		}
		if (Configs.displayNameUseMeterUnits)
		{
			diameterText += Math.round(size * Utility.PIXEL_D * 100) / 100.0 + " meters";
		}
		else
		{
			diameterText = addBitLengthString(size, diameterText);
		}
		return diameterText;
	}
	
	public static String getHollowShapeText(EntityPlayer player, NBTTagCompound nbt, ItemSculptingTool item)
	{
		return getHollowShapeText(isHollowShape(player, nbt, item.removeBits()));
	}
	
	public static String getHollowShapeText(boolean isHollowShape)
	{
		return "Interior: " + (isHollowShape ? "hollow" : "solid");
	}
	
	public static String getOpenEndsText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getOpenEndsText(areEndsOpen(player, nbt));
	}
	
	public static String getOpenEndsText(boolean areEndsOpen)
	{
		return "Ends: " + (areEndsOpen ? "open" : "closed");
	}
	
	public static String getWallThicknessText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getWallThicknessText(getWallThickness(player, nbt));
	}

	public static String getWallThicknessText(int wallThickness)
	{
		return addBitLengthString(wallThickness, "Wall Thickness: ");
	}
	
	private static String addBitLengthString(double size, String diameterText)
	{
		if (size >= 16)
		{
			int size2 = ((int) size / 16);
			diameterText += size2 + " meter";
			if (size2 > 1) diameterText += "s";
			size %= 16;
			if (size > 0) diameterText += " & ";
		}
		if (size > 0) diameterText += size + " bits";
		return diameterText;
	}
	
	public static int cycleData(int intValue, boolean forward, int max)
	{
		return (intValue + (forward ? 1 : max - 1)) % max;
	}
}