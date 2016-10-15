package com.phylogeny.extrabitmanipulation.helper;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingBoolean;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingInt;
import com.phylogeny.extrabitmanipulation.config.ConfigBitStack;
import com.phylogeny.extrabitmanipulation.extendedproperties.BitToolSettingsPlayerProperties;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.packet.PacketSetBitStack;
import com.phylogeny.extrabitmanipulation.packet.PacketSetHollowShape;
import com.phylogeny.extrabitmanipulation.packet.PacketSetEndsOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelAreaMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelGuiOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelSnapMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetSculptMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetDirection;
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

public class BitToolSettingsHelper
{
	
	public static NBTTagCompound initNBT(ItemStack stack)
	{
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt;
	}
	
	private static int getInt(NBTTagCompound nbt, int intValue, String key)
	{
		if (nbt != null && nbt.hasKey(key))
			intValue = nbt.getInteger(key);
		
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
			booleanValue = nbt.getBoolean(key);
		
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
			stackToGet = ItemStackHelper.loadStackFromNBT(nbt, key);
		
		return stackToGet;
	}
	
	private static void setStack(EntityPlayer player, ItemStack stack, ItemStack stackToSet, String key)
	{
		NBTTagCompound nbt = initNBT(stack);
		ItemStackHelper.saveStackToNBT(nbt, stackToSet, key);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	public static int getModelAreaMode(EntityPlayer player, NBTTagCompound nbt)
	{
		int mode = Configs.modelAreaMode.getDefaultValue();
		if (Configs.modelAreaMode.isPerTool())
		{
			mode = getInt(nbt, mode, NBTKeys.MODEL_AREA_MODE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				mode = prop.modelAreaMode;
		}
		return mode;
	}
	
	public static void setModelAreaMode(EntityPlayer player, ItemStack stack, int mode)
	{
		World world = player.worldObj;
		if (Configs.modelAreaMode.isPerTool())
		{
			if (!world.isRemote)
				setInt(player, stack, mode, NBTKeys.MODEL_AREA_MODE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.modelAreaMode = mode;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetModelAreaMode(mode));
	}
	
	public static int getModelSnapMode(EntityPlayer player, NBTTagCompound nbt)
	{
		int mode = Configs.modelSnapMode.getDefaultValue();
		if (Configs.modelSnapMode.isPerTool())
		{
			mode = getInt(nbt, mode, NBTKeys.MODEL_SNAP_MODE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				mode = prop.modelSnapMode;
		}
		return mode;
	}
	
	public static void setModelSnapMode(EntityPlayer player, ItemStack stack, int mode)
	{
		World world = player.worldObj;
		if (Configs.modelSnapMode.isPerTool())
		{
			if (!world.isRemote)
				setInt(player, stack, mode, NBTKeys.MODEL_SNAP_MODE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.modelSnapMode = mode;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetModelSnapMode(mode));
	}
	
	public static boolean getModelGuiOpen(EntityPlayer player, NBTTagCompound nbt)
	{
		boolean modelGuiOpen = Configs.modelGuiOpen.getDefaultValue();
		if (Configs.modelGuiOpen.isPerTool())
		{
			modelGuiOpen = getBoolean(nbt, modelGuiOpen, NBTKeys.MODEL_GUI_OPEN);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				modelGuiOpen = prop.modelGuiOpen;
		}
		return modelGuiOpen;
	}
	
	public static void setModelGuiOpen(EntityPlayer player, ItemStack stack, boolean modelGuiOpen)
	{
		World world = player.worldObj;
		if (Configs.modelGuiOpen.isPerTool())
		{
			if (!world.isRemote)
				setBoolean(player, stack, modelGuiOpen, NBTKeys.MODEL_GUI_OPEN);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.modelGuiOpen = modelGuiOpen;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetModelGuiOpen(modelGuiOpen));
	}
	
	public static int getSculptMode(EntityPlayer player, NBTTagCompound nbt)
	{
		int mode = Configs.sculptMode.getDefaultValue();
		if (Configs.sculptMode.isPerTool())
		{
			mode = getInt(nbt, mode, NBTKeys.SCULPT_MODE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				mode = prop.sculptMode;
		}
		return mode;
	}
	
	public static void setSculptMode(EntityPlayer player, ItemStack stack, int mode)
	{
		World world = player.worldObj;
		if (Configs.sculptMode.isPerTool())
		{
			if (!world.isRemote)
				setInt(player, stack, mode, NBTKeys.SCULPT_MODE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.sculptMode = mode;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetSculptMode(mode));
	}
	
	public static int getDirection(EntityPlayer player, NBTTagCompound nbt)
	{
		int direction = Configs.sculptDirection.getDefaultValue();
		if (Configs.sculptDirection.isPerTool())
		{
			direction = getInt(nbt, direction, NBTKeys.DIRECTION);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				direction = prop.direction;
		}
		return direction;
	}
	
	public static void setDirection(EntityPlayer player, ItemStack stack, int direction)
	{
		World world = player.worldObj;
		if (Configs.sculptDirection.isPerTool())
		{
			if (!world.isRemote)
				setInt(player, stack, direction, NBTKeys.DIRECTION);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.direction = direction;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetDirection(direction));
	}
	
	public static int getShapeType(EntityPlayer player, NBTTagCompound nbt, boolean isCurved)
	{
		ConfigBitToolSettingInt shapeTypeConfig = isCurved ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat;
		int shapeType = shapeTypeConfig.getDefaultValue();
		if (shapeTypeConfig.isPerTool())
		{
			shapeType = getInt(nbt, shapeType, NBTKeys.SHAPE_TYPE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				shapeType = isCurved ? prop.shapeTypeCurved : prop.shapeTypeFlat;
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
				setInt(player, stack, shapeType, NBTKeys.SHAPE_TYPE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
			{
				if (isCurved)
				{
					prop.shapeTypeCurved = shapeType;
				}
				else
				{
					prop.shapeTypeFlat = shapeType;
				}
			}
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetShapeType(isCurved, shapeType));
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
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				targetBitGridVertexes = prop.targetBitGridVertexes;
		}
		return targetBitGridVertexes;
	}
	
	public static void setBitGridTargeted(EntityPlayer player, ItemStack stack, boolean targetBitGridVertexes)
	{
		World world = player.worldObj;
		if (Configs.sculptTargetBitGridVertexes.isPerTool())
		{
			if (!world.isRemote)
				setBoolean(player, stack, targetBitGridVertexes, NBTKeys.TARGET_BIT_GRID_VERTEXES);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.targetBitGridVertexes = targetBitGridVertexes;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetTargetBitGridVertexes(targetBitGridVertexes));
	}
	
	public static int getSemiDiameter(EntityPlayer player, NBTTagCompound nbt)
	{
		int semiDiameter = Configs.sculptSemiDiameter.getDefaultValue();
		if (Configs.sculptSemiDiameter.isPerTool())
		{
			if (nbt != null && nbt.hasKey(NBTKeys.SCULPT_SEMI_DIAMETER))
				semiDiameter = nbt.getInteger(NBTKeys.SCULPT_SEMI_DIAMETER);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				semiDiameter = prop.sculptSemiDiameter;
		}
		return semiDiameter;
	}
	
	public static void setSemiDiameter(EntityPlayer player, ItemStack stack, int semiDiameter)
	{
		World world = player.worldObj;
		if (Configs.sculptSemiDiameter.isPerTool())
		{
			if (!world.isRemote)
				setInt(player, stack, semiDiameter, NBTKeys.SCULPT_SEMI_DIAMETER);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.sculptSemiDiameter = semiDiameter;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetSemiDiameter(semiDiameter));
	}
	
	public static boolean isHollowShape(EntityPlayer player, NBTTagCompound nbt, boolean isWire)
	{
		ConfigBitToolSettingBoolean hollowShapeConfig = isWire ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade;
		boolean hollowShape = hollowShapeConfig.getDefaultValue();
		if (hollowShapeConfig.isPerTool())
		{
			hollowShape = getBoolean(nbt, hollowShape, NBTKeys.SCULPT_HOLLOW_SHAPE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				hollowShape = isWire ? prop.sculptHollowShapeWire : prop.sculptHollowShapeSpade;
		}
		return hollowShape;
	}
	
	public static void setHollowShape(EntityPlayer player, ItemStack stack, boolean isWire, boolean hollowShape)
	{
		World world = player.worldObj;
		if ((isWire ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade).isPerTool())
		{
			if (!world.isRemote)
				setBoolean(player, stack, hollowShape, NBTKeys.SCULPT_HOLLOW_SHAPE);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
			{
				if (isWire)
				{
					prop.sculptHollowShapeWire = hollowShape;
				}
				else
				{
					prop.sculptHollowShapeSpade = hollowShape;
				}
			}
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetHollowShape(hollowShape, isWire));
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
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				openEnds = prop.openEnds;
		}
		return openEnds;
	}
	
	public static void setEndsOpen(EntityPlayer player, ItemStack stack, boolean openEnds)
	{
		World world = player.worldObj;
		if (Configs.sculptOpenEnds.isPerTool())
		{
			if (!world.isRemote)
				setBoolean(player, stack, openEnds, NBTKeys.OPEN_ENDS);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.openEnds = openEnds;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetEndsOpen(openEnds));
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
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				wallThickness = prop.wallThickness;
		}
		return wallThickness;
	}
	
	public static void setWallThickness(EntityPlayer player, ItemStack stack, int wallThickness)
	{
		World world = player.worldObj;
		if (Configs.sculptWallThickness.isPerTool())
		{
			if (!world.isRemote)
				setInt(player, stack, wallThickness, NBTKeys.WALL_THICKNESS);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				prop.wallThickness = wallThickness;
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetWallThickness(wallThickness));
	}
	
	public static ItemStack getBitStack(EntityPlayer player, NBTTagCompound nbt, boolean isWire)
	{
		ConfigBitStack bitStackConfig = isWire ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade;
		ItemStack bitStack = bitStackConfig.getDefaultValue();
		if (bitStackConfig.isPerTool())
		{
			bitStack = getStack(nbt, bitStack, NBTKeys.SET_BIT);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
				bitStack = isWire ? prop.setBitWire : prop.setBitSpade;
		}
		return bitStack;
	}
	
	public static void setBitStack(EntityPlayer player, ItemStack stack, boolean isWire, ItemStack bitStack)
	{
		World world = player.worldObj;
		if ((isWire ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade).isPerTool())
		{
			if (!world.isRemote)
				setStack(player, stack, bitStack, NBTKeys.SET_BIT);
		}
		else
		{
			BitToolSettingsPlayerProperties prop = BitToolSettingsPlayerProperties.get(player);
			if (prop != null)
			{
				if (isWire)
				{
					prop.setBitWire = bitStack;
				}
				else
				{
					prop.setBitSpade = bitStack;
				}
			}
		}
		if (world.isRemote)
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetBitStack(isWire, bitStack));
	}
	
	public static String getModeText(String[] titles, String pefaceText, int mode)
	{
		return pefaceText + " Mode: " + titles[mode].toLowerCase();
	}
	
	public static String getModelAreaModeText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getModelAreaModeText(getModelAreaMode(player, nbt));
	}
	
	public static String getModelAreaModeText(int mode)
	{
		return getModeText(ItemModelingTool.AREA_MODE_TITLES, "Area", mode);
	}
	
	public static String getModelSnapModeText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getModelSnapModeText(getModelSnapMode(player, nbt));
	}
	
	public static String getModelSnapModeText(int mode)
	{
		return getModeText(ItemModelingTool.SNAP_MODE_TITLES, "Chunk Snap", mode);
	}
	
	public static String getModelGuiOpenText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getModelGuiOpenText(getModelGuiOpen(player, nbt));
	}
	
	public static String getModelGuiOpenText(boolean openGui)
	{
		return "Open GUI Upon Read: " + (openGui ? "true" : "false");
	}
	
	public static String getSculptModeText(EntityPlayer player, NBTTagCompound nbt)
	{
		return getSculptModeText(getSculptMode(player, nbt));
	}
	
	public static String getSculptModeText(int mode)
	{
		return getModeText(ItemSculptingTool.MODE_TITLES, "Sculpting", mode);
	}
	
	public static String getDirectionText(EntityPlayer player, NBTTagCompound nbt, boolean showRotation)
	{
		return getDirectionText(getDirection(player, nbt), showRotation);
	}
	
	public static String getDirectionText(int direction, boolean showRotation)
	{
		String text = "Direction: " + EnumFacing.getFront(direction % 6).getName().toLowerCase();
		if (showRotation)
		{
			int rotation = direction / 6;
			if (rotation > 0)
				text += " (rotation " +  (rotation * 90) + "\u00B0)";
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
			if (!targetBitGrid)
				size += 1;
			
			diameterText = diameterText.substring(5);
		}
		else
		{
			if (!targetBitGrid)
				size += 0.5;
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
	
	public static int cycleData(int intValue, boolean forward, int max)
	{
		return (intValue + (forward ? 1 : max - 1)) % max;
	}
	
	private static String addBitLengthString(double size, String diameterText)
	{
		if (size >= 16)
		{
			int size2 = ((int) size / 16);
			diameterText += size2 + " meter";
			if (size2 > 1)
				diameterText += "s";
			
			size %= 16;
			if (size > 0)
				diameterText += " & ";
		}
		if (size > 0)
		{
			if (size == (int) size)
			{
				diameterText += (int) size;
			}
			else
			{
				diameterText += size;
			}
			diameterText += " bits";
		}
		return diameterText;
	}
	
	public static String getBitName(ItemStack bitStack)
	{
		return bitStack.getDisplayName().replace("Chiseled Bit - ", "");
	}
	
}