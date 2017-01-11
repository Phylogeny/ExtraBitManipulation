package com.phylogeny.extrabitmanipulation.extendedproperties;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.packet.PacketSyncAllBitToolData;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class BitToolSettingsPlayerProperties implements IExtendedEntityProperties
{
	private static final String ID = "BitToolPlayerProperties";
	public int modelAreaMode, modelSnapMode, sculptMode, direction, shapeTypeCurved, shapeTypeFlat, sculptSemiDiameter, wallThickness;
	public boolean modelGuiOpen, targetBitGridVertexes, sculptHollowShapeWire, sculptHollowShapeSpade, openEnds, offsetShape;
	public ItemStack setBitWire, setBitSpade;
	
	public void syncAllData(EntityPlayerMP player)
	{
		ExtraBitManipulation.packetNetwork.sendTo(new PacketSyncAllBitToolData(modelAreaMode, modelSnapMode, modelGuiOpen, sculptMode,
				direction, shapeTypeCurved, shapeTypeFlat, targetBitGridVertexes, sculptSemiDiameter, sculptHollowShapeWire,
				sculptHollowShapeSpade, openEnds, wallThickness, setBitWire, setBitSpade, offsetShape), player);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger(NBTKeys.MODEL_AREA_MODE, modelAreaMode);
		nbt.setInteger(NBTKeys.MODEL_SNAP_MODE, modelSnapMode);
		nbt.setBoolean(NBTKeys.MODEL_GUI_OPEN, modelGuiOpen);
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
		nbt.setBoolean(NBTKeys.OFFSET_SHAPE, offsetShape);
		compound.setTag(ID, nbt);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound nbt = (NBTTagCompound) compound.getTag(ID);
		modelAreaMode = nbt.getInteger(NBTKeys.MODEL_AREA_MODE);
		modelSnapMode = nbt.getInteger(NBTKeys.MODEL_SNAP_MODE);
		modelGuiOpen = nbt.getBoolean(NBTKeys.MODEL_GUI_OPEN);
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
		offsetShape = nbt.getBoolean(NBTKeys.OFFSET_SHAPE);
	}
	
	@Override
	public void init(Entity entity, World world)
	{
		modelAreaMode = Configs.modelAreaMode.getDefaultValue();
		modelSnapMode = Configs.modelSnapMode.getDefaultValue();
		modelGuiOpen = Configs.modelGuiOpen.getDefaultValue();
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
		offsetShape = Configs.sculptOffsetShape.getDefaultValue();
	}
	
	public static BitToolSettingsPlayerProperties get(Entity entity)
	{
		return (BitToolSettingsPlayerProperties) entity.getExtendedProperties(ID);
	}
	
	public static void register(Entity entity)
	{
		entity.registerExtendedProperties(ID, new BitToolSettingsPlayerProperties());
	}
	
}