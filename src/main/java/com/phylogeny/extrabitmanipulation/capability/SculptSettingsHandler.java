package com.phylogeny.extrabitmanipulation.capability;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.packet.PacketSyncAllSculptingData;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class SculptSettingsHandler implements ICapabilityProvider, ISculptSettingsHandler, INBTSerializable<NBTTagCompound>
{
	@CapabilityInject(ISculptSettingsHandler.class)
	public static final Capability<ISculptSettingsHandler> SCULPT_SETTINGS_CAP = null;
	public int mode, direction, shapeTypeCurved, shapeTypeFlat, sculptSemiDiameter, wallThickness;
	public boolean targetBitGridVertexes, sculptHollowShapeWire, sculptHollowShapeSpade, openEnds;
	public ItemStack setBitWire, setBitSpade;
	
	public SculptSettingsHandler(int mode, int direction, int shapeTypeCurved, int shapeTypeFlat,
			boolean targetBitGridVertexes, int sculptSemiDiameter, boolean sculptHollowShapeWire,
			boolean sculptHollowShapeSpade, boolean openEnds, int wallThickness,
			ItemStack setBitWire, ItemStack setBitSpade)
	{
		this.mode = mode;
		this.direction = direction;
		this.shapeTypeCurved = shapeTypeCurved;
		this.shapeTypeFlat = shapeTypeFlat;
		this.targetBitGridVertexes = targetBitGridVertexes;
		this.sculptSemiDiameter = sculptSemiDiameter;
		this.sculptHollowShapeWire = sculptHollowShapeWire;
		this.sculptHollowShapeSpade = sculptHollowShapeSpade;
		this.openEnds = openEnds;
		this.wallThickness = wallThickness;
		this.setBitWire = setBitWire;
		this.setBitSpade = setBitSpade;
	}
	
	public SculptSettingsHandler()
	{
		mode = Configs.sculptMode.getDefaultValue();
		direction = Configs.sculptDirection.getDefaultValue();
		shapeTypeCurved = Configs.sculptShapeTypeCurved.getDefaultValue();
		shapeTypeFlat = Configs.sculptShapeTypeFlat.getDefaultValue();
		targetBitGridVertexes = Configs.sculptTargetBitGridVertexes.getDefaultValue();
		sculptSemiDiameter = Configs.sculptSemiDiameter.getDefaultValue();
		sculptHollowShapeWire = Configs.sculptHollowShapeWire.getDefaultValue();
		sculptHollowShapeSpade = Configs.sculptHollowShapeSpade.getDefaultValue();
		openEnds = Configs.sculptOpenEnds.getDefaultValue();
		wallThickness = Configs.sculptWallThickness.getDefaultValue();
		setBitWire = Configs.sculptSetBitWire.getDefaultValue();
		setBitSpade = Configs.sculptSetBitSpade.getDefaultValue();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		 return SCULPT_SETTINGS_CAP != null && capability == SCULPT_SETTINGS_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == SCULPT_SETTINGS_CAP ? SCULPT_SETTINGS_CAP.<T>cast(this) : null;
	}
	
	public static ISculptSettingsHandler getCapability(EntityPlayer player)
	{
		return player.getCapability(SCULPT_SETTINGS_CAP, null);
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger(NBTKeys.MODE, mode);
		nbt.setInteger(NBTKeys.DIRECTION, direction);
		nbt.setInteger(NBTKeys.SHAPE_TYPE_CURVED, shapeTypeCurved);
		nbt.setInteger(NBTKeys.SHAPE_TYPE_FLAT, shapeTypeFlat);
		nbt.setBoolean(NBTKeys.TARGET_BIT_GRID_VERTEXES, targetBitGridVertexes);
		nbt.setInteger(NBTKeys.SCULPT_SEMI_DIAMETER, sculptSemiDiameter);
		nbt.setBoolean(NBTKeys.SCULPT_HOLLOW_SHAPE_WIRE, sculptHollowShapeWire);
		nbt.setBoolean(NBTKeys.SCULPT_HOLLOW_SHAPE_SPADE, sculptHollowShapeSpade);
		nbt.setBoolean(NBTKeys.OPEN_ENDS, openEnds);
		nbt.setInteger(NBTKeys.WALL_THICKNESS, wallThickness);
		ItemStackHelper.saveStackToNBT(nbt, setBitWire, NBTKeys.SET_BIT_WIRE);
		ItemStackHelper.saveStackToNBT(nbt, setBitSpade, NBTKeys.SET_BIT_SPADE);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		mode = nbt.getInteger(NBTKeys.MODE);
		direction = nbt.getInteger(NBTKeys.DIRECTION);
		shapeTypeCurved = nbt.getInteger(NBTKeys.SHAPE_TYPE_CURVED);
		shapeTypeFlat = nbt.getInteger(NBTKeys.SHAPE_TYPE_FLAT);
		targetBitGridVertexes = nbt.getBoolean(NBTKeys.TARGET_BIT_GRID_VERTEXES);
		sculptSemiDiameter = nbt.getInteger(NBTKeys.SCULPT_SEMI_DIAMETER);
		sculptHollowShapeWire = nbt.getBoolean(NBTKeys.SCULPT_HOLLOW_SHAPE_WIRE);
		sculptHollowShapeSpade = nbt.getBoolean(NBTKeys.SCULPT_HOLLOW_SHAPE_SPADE);
		openEnds = nbt.getBoolean(NBTKeys.OPEN_ENDS);
		wallThickness = nbt.getInteger(NBTKeys.WALL_THICKNESS);
		setBitWire = ItemStackHelper.loadStackFromNBT(nbt, NBTKeys.SET_BIT_WIRE);
		setBitSpade = ItemStackHelper.loadStackFromNBT(nbt, NBTKeys.SET_BIT_SPADE);
	}
	
	@Override
	public void syncAllData(EntityPlayerMP player)
	{
		ExtraBitManipulation.packetNetwork.sendTo(new PacketSyncAllSculptingData(mode, direction, shapeTypeCurved,
				shapeTypeFlat, targetBitGridVertexes, sculptSemiDiameter, sculptHollowShapeWire,
				sculptHollowShapeSpade, openEnds, wallThickness, setBitWire, setBitSpade), player);
	}
	
	@Override
	public int getMode()
	{
		return mode;
	}

	@Override
	public void setMode(int mode)
	{
		this.mode = mode;
	}

	@Override
	public int getDirection()
	{
		return direction;
	}

	@Override
	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	@Override
	public int getShapeTypeCurved()
	{
		return shapeTypeCurved;
	}

	@Override
	public void setShapeTypeCurved(int shapeTypeCurved)
	{
		this.shapeTypeCurved = shapeTypeCurved;
	}

	@Override
	public int getShapeTypeFlat()
	{
		return shapeTypeFlat;
	}

	@Override
	public void setShapeTypeFlat(int shapeTypeFlat)
	{
		this.shapeTypeFlat = shapeTypeFlat;
	}

	@Override
	public int getSculptSemiDiameter()
	{
		return sculptSemiDiameter;
	}

	@Override
	public void setSculptSemiDiameter(int sculptSemiDiameter)
	{
		this.sculptSemiDiameter = sculptSemiDiameter;
	}

	@Override
	public int getWallThickness()
	{
		return wallThickness;
	}

	@Override
	public void setWallThickness(int wallThickness)
	{
		this.wallThickness = wallThickness;
	}

	@Override
	public boolean isBitGridTargeted()
	{
		return targetBitGridVertexes;
	}

	@Override
	public void setBitGridTargeted(boolean targetBitGridVertexes)
	{
		this.targetBitGridVertexes = targetBitGridVertexes;
	}

	@Override
	public boolean isShapeHollowWire()
	{
		return sculptHollowShapeWire;
	}

	@Override
	public void setShapeHollowWire(boolean sculptHollowShapeWire)
	{
		this.sculptHollowShapeWire = sculptHollowShapeWire;
	}

	@Override
	public boolean isShapeHollowSpade()
	{
		return sculptHollowShapeSpade;
	}

	@Override
	public void setShapeHollowSpade(boolean sculptHollowShapeSpade)
	{
		this.sculptHollowShapeSpade = sculptHollowShapeSpade;
	}

	@Override
	public boolean areEndsOpen()
	{
		return openEnds;
	}

	@Override
	public void setEndsOpen(boolean openEnds)
	{
		this.openEnds = openEnds;
	}

	@Override
	public ItemStack getBitStackWire()
	{
		return setBitWire;
	}

	@Override
	public void setBitStackWire(ItemStack setBitWire)
	{
		this.setBitWire = setBitWire;
	}

	@Override
	public ItemStack getBitStackSpade()
	{
		return setBitSpade;
	}

	@Override
	public void setBitStackSpade(ItemStack setBitSpade)
	{
		this.setBitSpade = setBitSpade;
	}
	
}