package com.phylogeny.extrabitmanipulation.helper;

import java.util.Map;

import io.netty.buffer.ByteBuf;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingBoolean;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingInt;
import com.phylogeny.extrabitmanipulation.config.ConfigBitStack;
import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.config.ConfigReplacementBits;
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

import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class BitToolSettingsHelper
{
	
	public static String[] getDirectionNames()
	{
		String[] directionTexts = new String[6];
		for (EnumFacing facing : EnumFacing.VALUES)
		{
			directionTexts[facing.getIndex()] = facing.getName().substring(0, 1).toUpperCase() + facing.getName().substring(1);
		}
		return directionTexts;
	}
	
	public static NBTTagCompound initNBT(ItemStack stack)
	{
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		
		return stack.getTagCompound();
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
	
	private static int getInt(ConfigBitToolSettingInt config, NBTTagCompound nbt, String nbtKey)
	{
		return config.isPerTool() ? getInt(nbt, config.getDefaultValue(), nbtKey) : config.getValue();
	}
	
	private static boolean getBoolean(ConfigBitToolSettingBoolean config, NBTTagCompound nbt, String nbtKey)
	{
		return config.isPerTool() ? getBoolean(nbt, config.getDefaultValue(), nbtKey) : config.getValue();
	}
	
	private static ItemStack getStack(ConfigBitStack config, NBTTagCompound nbt, String nbtKey)
	{
		return config.isPerTool() ? getStack(nbt, config.getDefaultValue(), nbtKey) : config.getValue();
	}
	
	private static void setIntProperty(World world, Configuration configFile, ConfigBitToolSettingInt config, String catagory, int value)
	{
		if (!world.isRemote)
			return;
		
		Property prop = configFile.get(catagory, config.getTitle(), config.getDefaultValue());
		if (prop != null)
		{
			config.setValue(value);
			prop.setValue(value);
			configFile.save();
		}
	}
	
	private static void setBooleanProperty(World world, Configuration configFile, ConfigBitToolSettingBoolean config, String catagory, boolean value)
	{
		if (!world.isRemote)
			return;
		
		Property prop = configFile.get(catagory, config.getTitle(), config.getDefaultValue());
		if (prop != null)
		{
			config.setValue(value);
			prop.setValue(value);
			configFile.save();
		}
	}
	
	private static void setStackProperty(World world, Configuration configFile, ConfigBitStack config, String catagory, IBitBrush value)
	{
		if (!world.isRemote)
			return;
		
		Property prop = configFile.get(catagory, config.getTitle(), config.getStringDeafult());
		if (prop != null)
		{
			config.setValue(value == null ? null : value.getItemStack(1));
			prop.setValue(BitIOHelper.getStringFromState(value == null ? null : value.getState()));
			configFile.save();
		}
	}
	
	public static void setBitMapProperty(boolean isStateMap, String[] stringEntries)
	{
		Configuration configFile = ConfigHandlerExtraBitManipulation.modelingMapConfigFile;
		Property prop = configFile.get(ConfigHandlerExtraBitManipulation.MODELING_TOOL_MANUAL_MAPPINGS,
				isStateMap ? ConfigHandlerExtraBitManipulation.STATE_TO_BIT_MAP : ConfigHandlerExtraBitManipulation.BLOCK_TO_BIT_MAP, new String[]{});
		if (prop != null)
		{
			prop.setValues(stringEntries);
			configFile.save();
		}
	}
	
	public static int getModelAreaMode(NBTTagCompound nbt)
	{
		return getInt(Configs.modelAreaMode, nbt, NBTKeys.MODEL_AREA_MODE);
	}
	
	public static void setModelAreaMode(EntityPlayer player, ItemStack stack, int mode, ConfigBitToolSettingInt modelAreaMode)
	{
		World world = player.worldObj;
		if (modelAreaMode == null || modelAreaMode.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetModelAreaMode(mode));
			}
			else
			{
				setInt(player, stack, mode, NBTKeys.MODEL_AREA_MODE);
			}
		}
		else if (world.isRemote)
		{
			setIntProperty(world, ConfigHandlerExtraBitManipulation.modelingMapConfigFile,
					modelAreaMode, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_MODEL, mode);
		}
	}
	
	public static int getModelSnapMode(NBTTagCompound nbt)
	{
		return getInt(Configs.modelSnapMode, nbt, NBTKeys.MODEL_SNAP_MODE);
	}
	
	public static void setModelSnapMode(EntityPlayer player, ItemStack stack, int mode, ConfigBitToolSettingInt modelSnapMode)
	{
		World world = player.worldObj;
		if (modelSnapMode == null || modelSnapMode.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetModelSnapMode(mode));
			}
			else
			{
				setInt(player, stack, mode, NBTKeys.MODEL_SNAP_MODE);
			}
		}
		else if (world.isRemote)
		{
			setIntProperty(world, ConfigHandlerExtraBitManipulation.modelingMapConfigFile,
					modelSnapMode, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_MODEL, mode);
		}
	}
	
	public static boolean getModelGuiOpen(NBTTagCompound nbt)
	{
		return getBoolean(Configs.modelGuiOpen, nbt, NBTKeys.MODEL_GUI_OPEN);
	}
	
	public static void setModelGuiOpen(EntityPlayer player, ItemStack stack, boolean isOpen, ConfigBitToolSettingBoolean modelGuiOpen)
	{
		World world = player.worldObj;
		if (modelGuiOpen == null || modelGuiOpen.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetModelGuiOpen(isOpen));
			}
			else
			{
				setBoolean(player, stack, isOpen, NBTKeys.MODEL_GUI_OPEN);
			}
		}
		else if (world.isRemote)
		{
			setBooleanProperty(world, ConfigHandlerExtraBitManipulation.modelingMapConfigFile,
					modelGuiOpen, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_MODEL, isOpen);
		}
	}
	
	public static int getSculptMode(NBTTagCompound nbt)
	{
		return getInt(Configs.sculptMode, nbt, NBTKeys.SCULPT_MODE);
	}
	
	public static void setSculptMode(EntityPlayer player, ItemStack stack, int mode, ConfigBitToolSettingInt sculptMode)
	{
		World world = player.worldObj;
		if (sculptMode == null || sculptMode.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetSculptMode(mode));
			}
			else
			{
				setInt(player, stack, mode, NBTKeys.SCULPT_MODE);
			}
		}
		else if (world.isRemote)
		{
			setIntProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptMode, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, mode);
		}
	}
	
	public static int getDirection(NBTTagCompound nbt)
	{
		return getInt(Configs.sculptDirection, nbt, NBTKeys.DIRECTION);
	}
	
	public static void setDirection(EntityPlayer player, ItemStack stack, int direction, ConfigBitToolSettingInt sculptDirection)
	{
		World world = player.worldObj;
		if (sculptDirection == null || sculptDirection.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetDirection(direction));
			}
			else
			{
				setInt(player, stack, direction, NBTKeys.DIRECTION);
			}
		}
		else if (world.isRemote)
		{
			setIntProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptDirection, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, direction);
		}
	}
	
	public static int getShapeType(NBTTagCompound nbt, boolean isCurved)
	{
		int shapeType = getInt(isCurved ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat, nbt, NBTKeys.SHAPE_TYPE);
		return isCurved && shapeType > 2 ? Configs.sculptShapeTypeCurved.getDefaultValue()
				: (!isCurved && shapeType < 3 ? Configs.sculptShapeTypeFlat.getDefaultValue() : shapeType);
	}
	
	public static void setShapeType(EntityPlayer player, ItemStack stack, boolean isCurved, int shapeType, ConfigBitToolSettingInt sculptShapeType)
	{
		World world = player.worldObj;
		if (sculptShapeType == null || sculptShapeType.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetShapeType(isCurved, shapeType));
			}
			else
			{
				setInt(player, stack, shapeType, NBTKeys.SHAPE_TYPE);
			}
		}
		else if (world.isRemote)
		{
			setIntProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptShapeType, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, shapeType);
		}
	}
	
	public static boolean isBitGridTargeted(NBTTagCompound nbt)
	{
		return getBoolean(Configs.sculptTargetBitGridVertexes, nbt, NBTKeys.TARGET_BIT_GRID_VERTEXES);
	}
	
	public static void setBitGridTargeted(EntityPlayer player, ItemStack stack, boolean isTargeted, ConfigBitToolSettingBoolean sculptTargetBitGridVertexes)
	{
		World world = player.worldObj;
		if (sculptTargetBitGridVertexes == null || sculptTargetBitGridVertexes.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetTargetBitGridVertexes(isTargeted));
			}
			else
			{
				setBoolean(player, stack, isTargeted, NBTKeys.TARGET_BIT_GRID_VERTEXES);
			}
		}
		else if (world.isRemote)
		{
			setBooleanProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptTargetBitGridVertexes, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, isTargeted);
		}
	}
	
	public static int getSemiDiameter(NBTTagCompound nbt)
	{
		return getInt(Configs.sculptSemiDiameter, nbt, NBTKeys.SCULPT_SEMI_DIAMETER);
	}
	
	public static void setSemiDiameter(EntityPlayer player, ItemStack stack, int semiDiameter, ConfigBitToolSettingInt sculptSemiDiameter)
	{
		World world = player.worldObj;
		if (sculptSemiDiameter == null || sculptSemiDiameter.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetSemiDiameter(semiDiameter));
			}
			else
			{
				setInt(player, stack, semiDiameter, NBTKeys.SCULPT_SEMI_DIAMETER);
			}
		}
		else if (world.isRemote)
		{
			setIntProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptSemiDiameter, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, semiDiameter);
		}
	}
	
	public static boolean isHollowShape(NBTTagCompound nbt, boolean isWire)
	{
		return getBoolean(isWire ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade, nbt, NBTKeys.SCULPT_HOLLOW_SHAPE);
	}
	
	public static void setHollowShape(EntityPlayer player, ItemStack stack, boolean isWire, boolean hollowShape, ConfigBitToolSettingBoolean sculptHollowShape)
	{
		World world = player.worldObj;
		if (sculptHollowShape == null || sculptHollowShape.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetHollowShape(hollowShape, isWire));
			}
			else
			{
				setBoolean(player, stack, hollowShape, NBTKeys.SCULPT_HOLLOW_SHAPE);
			}
		}
		else if (world.isRemote)
		{
			setBooleanProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptHollowShape, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, hollowShape);
		}
	}
	
	public static boolean areEndsOpen(NBTTagCompound nbt)
	{
		return getBoolean(Configs.sculptOpenEnds, nbt, NBTKeys.OPEN_ENDS);
	}
	
	public static void setEndsOpen(EntityPlayer player, ItemStack stack, boolean openEnds, ConfigBitToolSettingBoolean sculptOpenEnds)
	{
		World world = player.worldObj;
		if (sculptOpenEnds == null || sculptOpenEnds.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetEndsOpen(openEnds));
			}
			else
			{
				setBoolean(player, stack, openEnds, NBTKeys.OPEN_ENDS);
			}
		}
		else if (world.isRemote)
		{
			setBooleanProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptOpenEnds, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, openEnds);
		}
	}
	
	public static int getWallThickness(NBTTagCompound nbt)
	{
		return getInt(Configs.sculptWallThickness, nbt, NBTKeys.WALL_THICKNESS);
	}
	
	public static void setWallThickness(EntityPlayer player, ItemStack stack, int wallThickness, ConfigBitToolSettingInt sculptWallThickness)
	{
		World world = player.worldObj;
		if (sculptWallThickness == null || sculptWallThickness.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetWallThickness(wallThickness));
			}
			else
			{
				setInt(player, stack, wallThickness, NBTKeys.WALL_THICKNESS);
			}
		}
		else if (world.isRemote)
		{
			setIntProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptWallThickness, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, wallThickness);
		}
	}
	
	public static ItemStack getBitStack(NBTTagCompound nbt, boolean isWire)
	{
		return getStack(isWire ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade, nbt, NBTKeys.SET_BIT);
	}
	
	public static void setBitStack(EntityPlayer player, ItemStack stack, boolean isWire, IBitBrush bit, ConfigBitStack sculptSetBit)
	{
		World world = player.worldObj;
		if (sculptSetBit == null || sculptSetBit.isPerTool())
		{
			if (world.isRemote)
			{
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetBitStack(isWire, bit));
			}
			else
			{
				setStack(player, stack, bit == null ? null : bit.getItemStack(1), NBTKeys.SET_BIT);
			}
		}
		else if (world.isRemote)
		{
			setStackProperty(world, ConfigHandlerExtraBitManipulation.sculptingMapConfigFile,
					sculptSetBit, ConfigHandlerExtraBitManipulation.DATA_CATAGORY_SCULPT, bit);
		}
	}
	
	public static String getModeText(String[] titles, String pefaceText, int mode)
	{
		return pefaceText + " Mode: " + titles[mode].toLowerCase();
	}
	
	public static String getModelAreaModeText(NBTTagCompound nbt)
	{
		return getModelAreaModeText(getModelAreaMode(nbt));
	}
	
	public static String getModelAreaModeText(int mode)
	{
		return getModeText(ItemModelingTool.AREA_MODE_TITLES, "Area", mode);
	}
	
	public static String getModelSnapModeText(NBTTagCompound nbt)
	{
		return getModelSnapModeText(getModelSnapMode(nbt));
	}
	
	public static String getModelSnapModeText(int mode)
	{
		return getModeText(ItemModelingTool.SNAP_MODE_TITLES, "Chunk Snap", mode);
	}
	
	public static String getModelGuiOpenText(NBTTagCompound nbt)
	{
		return getModelGuiOpenText(getModelGuiOpen(nbt));
	}
	
	public static String getModelGuiOpenText(boolean openGui)
	{
		return "Open GUI Upon Read: " + (openGui ? "true" : "false");
	}
	
	public static String getSculptModeText(NBTTagCompound nbt)
	{
		return getSculptModeText(getSculptMode(nbt));
	}
	
	public static String getSculptModeText(int mode)
	{
		return getModeText(ItemSculptingTool.MODE_TITLES, "Sculpting", mode);
	}
	
	public static String getDirectionText(NBTTagCompound nbt, boolean showRotation)
	{
		return getDirectionText(getDirection(nbt), showRotation);
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
	
	public static String getShapeTypeText(NBTTagCompound nbt, ItemSculptingTool item)
	{
		return getShapeTypeText(getShapeType(nbt, item.isCurved()));
	}
	
	public static String getShapeTypeText(int shapeType)
	{
		return "Shape: " + Shape.SHAPE_NAMES[shapeType].toLowerCase();
	}
	
	public static String getBitGridTargetedText(NBTTagCompound nbt)
	{
		return getBitGridTargetedText(isBitGridTargeted(nbt));
	}
	
	public static String getBitGridTargetedText(boolean targetBitGrid)
	{
		return "Targeting: " + (targetBitGrid ? "bit grid vertiecies" : "bits");
	}
	
	public static String getSemiDiameterText(NBTTagCompound nbt)
	{
		return getSemiDiameterText(nbt, getSemiDiameter(nbt));
	}
	
	public static String getSemiDiameterText(NBTTagCompound nbt, int semiDiameter)
	{
		double size = semiDiameter;
		boolean targetBitGrid = isBitGridTargeted(nbt);
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
	
	public static String getHollowShapeText(NBTTagCompound nbt, ItemSculptingTool item)
	{
		return getHollowShapeText(isHollowShape(nbt, item.removeBits()));
	}
	
	public static String getHollowShapeText(boolean isHollowShape)
	{
		return "Interior: " + (isHollowShape ? "hollow" : "solid");
	}
	
	public static String getOpenEndsText(NBTTagCompound nbt)
	{
		return getOpenEndsText(areEndsOpen(nbt));
	}
	
	public static String getOpenEndsText(boolean areEndsOpen)
	{
		return "Ends: " + (areEndsOpen ? "open" : "closed");
	}
	
	public static String getWallThicknessText(NBTTagCompound nbt)
	{
		return getWallThicknessText(getWallThickness(nbt));
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
	
	public static class ModelReadData
	{
		private int areaMode, chunkSnapMode;
		private boolean guiOpen;
		
		public ModelReadData() {}
		
		public ModelReadData(NBTTagCompound nbt)
		{
			areaMode = BitToolSettingsHelper.getModelAreaMode(nbt);
			chunkSnapMode = BitToolSettingsHelper.getModelSnapMode(nbt);
			guiOpen = BitToolSettingsHelper.getModelGuiOpen(nbt);
		}
		
		public void toBytes(ByteBuf buffer)
		{
			buffer.writeInt(areaMode);
			buffer.writeInt(chunkSnapMode);
			buffer.writeBoolean(guiOpen);
		}
		
		public void fromBytes(ByteBuf buffer)
		{
			areaMode = buffer.readInt();
			chunkSnapMode = buffer.readInt();
			guiOpen = buffer.readBoolean();
		}
		
		public int getAreaMode()
		{
			return areaMode;
		}
		
		public int getSnapMode()
		{
			return chunkSnapMode;
		}
		
		public boolean getGuiOpen()
		{
			return guiOpen;
		}
		
	}
	
	public static class ModelWriteData
	{
		private ConfigReplacementBits replacementBitsUnchiselable = new ConfigReplacementBits();
		private ConfigReplacementBits replacementBitsInsufficient = new ConfigReplacementBits();
		private Map<IBlockState, IBitBrush> stateToBitMap, blockToBitMap;
		private boolean bitMapPerTool;
		
		public ModelWriteData() {}
		
		public ModelWriteData(boolean bitMapPerTool)
		{
			this.bitMapPerTool = bitMapPerTool;
			replacementBitsUnchiselable = Configs.replacementBitsUnchiselable;
			replacementBitsInsufficient = Configs.replacementBitsInsufficient;
			stateToBitMap = Configs.modelStateToBitMap;
			blockToBitMap = Configs.modelBlockToBitMap;
		}
		
		public void toBytes(ByteBuf buffer)
		{
			replacementBitsUnchiselable.toBytes(buffer);
			replacementBitsInsufficient.toBytes(buffer);
			buffer.writeBoolean(bitMapPerTool);
			if (!bitMapPerTool)
			{
				BitIOHelper.stateToBitMapToBytes(buffer, stateToBitMap);
				BitIOHelper.stateToBitMapToBytes(buffer, blockToBitMap);
			}
		}
		
		public void fromBytes(ByteBuf buffer)
		{
			replacementBitsUnchiselable.fromBytes(buffer);
			replacementBitsInsufficient.fromBytes(buffer);
			bitMapPerTool = buffer.readBoolean();
			stateToBitMap = bitMapPerTool ? null : BitIOHelper.stateToBitMapFromBytes(buffer);
			blockToBitMap = bitMapPerTool ? null : BitIOHelper.stateToBitMapFromBytes(buffer);
		}
		
		public ConfigReplacementBits getReplacementBitsUnchiselable()
		{
			return replacementBitsUnchiselable;
		}
		
		public ConfigReplacementBits getReplacementBitsInsufficient()
		{
			return replacementBitsInsufficient;
		}
		
		public Map<IBlockState, IBitBrush> getStateToBitMap(IChiselAndBitsAPI api, ItemStack stack)
		{
			return bitMapPerTool ? BitIOHelper.readStateToBitMapFromNBT(api, stack, NBTKeys.STATE_TO_BIT_MAP_PERMANENT) : stateToBitMap;
		}
		
		public Map<IBlockState, IBitBrush> getBlockToBitMap(IChiselAndBitsAPI api, ItemStack stack)
		{
			return bitMapPerTool ? BitIOHelper.readStateToBitMapFromNBT(api, stack, NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT) : blockToBitMap;
		}
		
	}
	
	public static class SculptingData
	{
		private int sculptMode, direction, shapeType, semiDiameter, wallThickness;
		private boolean targetBitGridVertexes, hollowShape, openEnds;
		private ItemStack setBitStack;
		private float semiDiameterPadding;
		
		public SculptingData() {}
		
		public SculptingData(NBTTagCompound nbt, ItemSculptingTool toolItem)
		{
			sculptMode = BitToolSettingsHelper.getSculptMode(nbt);
			direction = BitToolSettingsHelper.getDirection(nbt);
			shapeType = BitToolSettingsHelper.getShapeType(nbt, toolItem.isCurved());
			targetBitGridVertexes = BitToolSettingsHelper.isBitGridTargeted(nbt);
			semiDiameter =  BitToolSettingsHelper.getSemiDiameter(nbt);
			hollowShape = BitToolSettingsHelper.isHollowShape(nbt, toolItem.removeBits());
			openEnds = BitToolSettingsHelper.areEndsOpen(nbt);
			wallThickness = BitToolSettingsHelper.getWallThickness(nbt);
			setBitStack = BitToolSettingsHelper.getBitStack(nbt, toolItem.removeBits());
			semiDiameterPadding = Configs.semiDiameterPadding;
		}
		
		public void toBytes(ByteBuf buffer)
		{
			buffer.writeInt(sculptMode);
			buffer.writeInt(direction);
			buffer.writeInt(shapeType);
			buffer.writeBoolean(targetBitGridVertexes);
			buffer.writeInt(semiDiameter);
			buffer.writeBoolean(hollowShape);
			buffer.writeBoolean(openEnds);
			buffer.writeInt(wallThickness);
			ItemStackHelper.stackToBytes(buffer, setBitStack);
			buffer.writeFloat(semiDiameterPadding);
		}
		
		public void fromBytes(ByteBuf buffer)
		{
			sculptMode = buffer.readInt();
			direction = buffer.readInt();
			shapeType = buffer.readInt();
			targetBitGridVertexes = buffer.readBoolean();
			semiDiameter = buffer.readInt();
			hollowShape = buffer.readBoolean();
			openEnds = buffer.readBoolean();
			wallThickness = buffer.readInt();
			setBitStack = ItemStackHelper.stackFromBytes(buffer);
			semiDiameterPadding = buffer.readFloat();
		}
		
		public int getSculptMode()
		{
			return sculptMode;
		}
		
		public int getDirection()
		{
			return direction;
		}
		
		public int getShapeType()
		{
			return shapeType;
		}
		
		public int getSemiDiameter()
		{
			return semiDiameter;
		}
		
		public int getWallThickness()
		{
			return wallThickness;
		}
		
		public boolean isBitGridTargeted()
		{
			return targetBitGridVertexes;
		}
		
		public boolean isHollowShape()
		{
			return hollowShape;
		}
		
		public boolean areEndsOpen()
		{
			return openEnds;
		}
		
		public ItemStack getBitStack()
		{
			return setBitStack;
		}
		
		public float getSemiDiameterPadding()
		{
			return semiDiameterPadding;
		}
		
	}
	
}