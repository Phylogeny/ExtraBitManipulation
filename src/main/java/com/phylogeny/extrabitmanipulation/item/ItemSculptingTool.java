package com.phylogeny.extrabitmanipulation.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.helper.BitAreaHelper;
import com.phylogeny.extrabitmanipulation.helper.BitInventoryHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.SculptingData;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;
import com.phylogeny.extrabitmanipulation.shape.AsymmetricalShape;
import com.phylogeny.extrabitmanipulation.shape.Cone;
import com.phylogeny.extrabitmanipulation.shape.ConeElliptic;
import com.phylogeny.extrabitmanipulation.shape.Cube;
import com.phylogeny.extrabitmanipulation.shape.Cuboid;
import com.phylogeny.extrabitmanipulation.shape.Cylinder;
import com.phylogeny.extrabitmanipulation.shape.CylinderElliptic;
import com.phylogeny.extrabitmanipulation.shape.Ellipsoid;
import com.phylogeny.extrabitmanipulation.shape.PrismIsoscelesTriangular;
import com.phylogeny.extrabitmanipulation.shape.PyramidIsoscelesTriangular;
import com.phylogeny.extrabitmanipulation.shape.PyramidRectangular;
import com.phylogeny.extrabitmanipulation.shape.PyramidSquare;
import com.phylogeny.extrabitmanipulation.shape.Shape;
import com.phylogeny.extrabitmanipulation.shape.Sphere;
import com.phylogeny.extrabitmanipulation.shape.SymmetricalShape;

public class ItemSculptingTool extends ItemBitToolBase
{
	public static final String[] MODE_TITLES = new String[]{"Local", "Global", "Drawn"};
	private boolean curved, removeBits;
	
	public ItemSculptingTool(boolean curved, boolean removeBits, String name)
	{
		super(name);
		this.curved = curved;
		this.removeBits = removeBits;
	}
	
	public boolean isCurved()
	{
		return curved;
	}
	
	public boolean removeBits()
	{
		return removeBits;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return ItemStackHelper.hasKey(stack, NBTKeys.REMAINING_USES) && ItemStackHelper.getNBT(stack).getInteger(NBTKeys.REMAINING_USES)
				< ((ConfigProperty) Configs.itemPropertyMap.get(this)).maxDamage;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1 - getDurability(stack);
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return MathHelper.hsvToRGB((float) (Math.max(0.0F, getDurability(stack)) / 3.0F), 1.0F, 1.0F);
	}
	
	private double getDurability(ItemStack stack)
	{
		return ItemStackHelper.getNBTOrNew(stack).getInteger(NBTKeys.REMAINING_USES)
				/ ((double) ((ConfigProperty) Configs.itemPropertyMap.get(this)).maxDamage);
	}
	
	@Override
	public boolean initialize(ItemStack stack)
	{
		super.initialize(stack);
		NBTTagCompound nbt = stack.getTagCompound();
		initInt(nbt, NBTKeys.REMAINING_USES, ((ConfigProperty) Configs.itemPropertyMap.get(this)).maxDamage);
		return true;
	}
	
	public NBTTagCompound initialize(ItemStack stack, SculptingData sculptingData)
	{
		NBTTagCompound nbt = BitToolSettingsHelper.initNBT(stack);
		initInt(nbt, NBTKeys.REMAINING_USES, ((ConfigProperty) Configs.itemPropertyMap.get(this)).maxDamage);
		initInt(nbt, NBTKeys.SCULPT_MODE, sculptingData.getSculptMode());
		initInt(nbt, NBTKeys.SCULPT_SEMI_DIAMETER, sculptingData.getSemiDiameter());
		initInt(nbt, NBTKeys.DIRECTION, sculptingData.getDirection());
		initBoolean(nbt, NBTKeys.TARGET_BIT_GRID_VERTEXES, sculptingData.isBitGridTargeted());
		initInt(nbt, NBTKeys.SHAPE_TYPE, sculptingData.getShapeType());
		initBoolean(nbt, NBTKeys.SCULPT_HOLLOW_SHAPE, sculptingData.isHollowShape());
		initBoolean(nbt, NBTKeys.OPEN_ENDS, sculptingData.areEndsOpen());
		initInt(nbt, NBTKeys.WALL_THICKNESS, sculptingData.getWallThickness());
		if (!nbt.hasKey(NBTKeys.SET_BIT) && !sculptingData.getBitStack().isEmpty())
		{
			NBTTagCompound nbt2 = new NBTTagCompound();
			sculptingData.getBitStack().writeToNBT(nbt2);
			nbt.setTag(NBTKeys.SET_BIT, nbt2);
		}
		initBoolean(nbt, NBTKeys.OFFSET_SHAPE, sculptingData.isShapeOffset());
		return nbt;
	}
	
	public boolean sculptBlocks(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, Vec3d hit, Vec3d drawnStartPoint, SculptingData sculptingData)
	{
		ItemStack setBitStack = sculptingData.getBitStack();
		if (setBitStack.isEmpty() && !removeBits)
			return false;
		
		if (!world.isRemote)
		{
			initialize(stack);
			player.inventoryContainer.detectAndSendChanges();
		}
		NBTTagCompound nbt = initialize(stack, sculptingData);
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		boolean inside = wasInsideClicked(side, hit, pos);
		if (!removeBits && !inside)
			pos = pos.offset(side);
		
		boolean globalMode = sculptingData.getSculptMode() == 1;
		if (drawnStartPoint != null || globalMode || isValidBlock(api, world, pos))
		{
			float hitX = (float) hit.xCoord - pos.getX();
			float hitY = (float) hit.yCoord - pos.getY();
			float hitZ = (float) hit.zCoord - pos.getZ();
			IBitLocation bitLoc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);
			if (bitLoc != null)
			{
				int direction = sculptingData.getDirection();
				int shapeType = sculptingData.getShapeType();
				boolean hollowShape = sculptingData.isHollowShape();
				boolean openEnds = sculptingData.areEndsOpen();
				float wallThickness = sculptingData.getWallThickness() * Utility.PIXEL_F;
				float padding = sculptingData.getSemiDiameterPadding();
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				float x2 = x + bitLoc.getBitX() * Utility.PIXEL_F;
				float y2 = y + bitLoc.getBitY() * Utility.PIXEL_F;
				float z2 = z + bitLoc.getBitZ() * Utility.PIXEL_F;
				Shape shape;
				AxisAlignedBB box;
				if (shapeType != 4 && shapeType != 5)
					direction %= 6;
				
				if (drawnStartPoint != null)
				{
					switch(shapeType)
					{
						case 1: shape = new CylinderElliptic(); break;
						case 2: shape = new ConeElliptic(); break;
						case 3: shape = new Cuboid(); break;
						case 4: shape = new PrismIsoscelesTriangular(); break;
						case 5: shape = new PyramidIsoscelesTriangular(); break;
						case 6: shape = new PyramidRectangular(); break;
						default: shape = new Ellipsoid(); break;
					}
					float x3 = (float) drawnStartPoint.xCoord;
					float y3 = (float) drawnStartPoint.yCoord;
					float z3 = (float) drawnStartPoint.zCoord;
					float minX = addPaddingToMin(x2, x3, padding);
					float minY = addPaddingToMin(y2, y3, padding);
					float minZ = addPaddingToMin(z2, z3, padding);
					float maxX = addPaddingToMax(x2, x3, padding);
					float maxY = addPaddingToMax(y2, y3, padding);
					float maxZ = addPaddingToMax(z2, z3, padding);
					box = new AxisAlignedBB(Math.floor(minX), Math.floor(minY), Math.floor(minZ),
							Math.ceil(maxX), Math.ceil(maxY), Math.ceil(maxZ));
					float f = 0.5F;
					minX *= f;
					minY *= f;
					minZ *= f;
					maxX *= f;
					maxY *= f;
					maxZ *= f;
					((AsymmetricalShape) shape).init(maxX + minX, maxY + minY, maxZ + minZ, maxX - minX, maxY - minY, maxZ - minZ,
							direction, hollowShape, wallThickness, openEnds);
				}
				else
				{
					switch(shapeType)
					{
						case 1: shape = new Cylinder(); break;
						case 2: shape = new Cone(); break;
						case 3: shape = new Cube(); break;
						case 4: shape = new PrismIsoscelesTriangular(); break;
						case 5: shape = new PyramidIsoscelesTriangular(); break;
						case 6: shape = new PyramidSquare(); break;
						default: shape = new Sphere(); break;
					}
					int semiDiameter = sculptingData.getSemiDiameter();
					int blockSemiDiameter = globalMode ? (int) Math.ceil(semiDiameter / 16.0) : 0;
					if (sculptingData.isShapeOffset() && !removeBits)
					{
						int offsetX = side.getFrontOffsetX();
						int offsetY = side.getFrontOffsetY();
						int offsetZ = side.getFrontOffsetZ();
						x2 += offsetX * Utility.PIXEL_F * semiDiameter;
						y2 += offsetY * Utility.PIXEL_F * semiDiameter;
						z2 += offsetZ * Utility.PIXEL_F * semiDiameter;
						x += offsetX * blockSemiDiameter;
						y += offsetY * blockSemiDiameter;
						z += offsetZ * blockSemiDiameter;
					}
					box = new AxisAlignedBB(x - blockSemiDiameter, y - blockSemiDiameter, z - blockSemiDiameter,
							x + blockSemiDiameter, y + blockSemiDiameter, z + blockSemiDiameter);
					float f = 0;
					Vec3d vecOffset = new Vec3d(0, 0, 0);
					if (sculptingData.isBitGridTargeted())
					{
						f = Utility.PIXEL_F * 0.5F;
						vecOffset = BitAreaHelper.getBitGridOffset(side, inside, hitX, hitY, hitZ, removeBits);
					}
					if (shapeType == 4 || shapeType == 5)
					{
						AsymmetricalShape asymmetricalShape = (AsymmetricalShape) shape;
						asymmetricalShape.setEquilateral(true);
						float radius = addPadding(semiDiameter, padding) - f;
						asymmetricalShape.init(x2 + f * (float) vecOffset.xCoord, y2 + f * (float) vecOffset.yCoord, z2 + f * (float) vecOffset.zCoord, radius,
								radius, radius, direction, hollowShape, wallThickness, openEnds);
					}
					else
					{
						((SymmetricalShape) shape).init(x2 + f * (float) vecOffset.xCoord, y2 + f * (float) vecOffset.yCoord, z2 + f * (float) vecOffset.zCoord,
								addPadding(semiDiameter, padding) - f, direction, hollowShape, wallThickness, openEnds);
					}
				}
				boolean creativeMode = player.capabilities.isCreativeMode;
				Map<IBlockState, Integer> bitTypes = null;
				if (removeBits && !world.isRemote && !creativeMode)
					bitTypes = new HashMap<IBlockState, Integer>();
				
				int initialpossibleUses = Integer.MAX_VALUE;
				IBitBrush setBit = null;
				try
				{
					setBit = api.createBrush(setBitStack);
					if (!removeBits && !creativeMode)
						initialpossibleUses = BitInventoryHelper.countInventoryBits(api, player, setBitStack);
				}
				catch (InvalidBitItem e) {}
				int remainingUses = nbt.getInteger(NBTKeys.REMAINING_USES);
				if (!creativeMode && initialpossibleUses > remainingUses)
					initialpossibleUses = remainingUses;
				
				int possibleUses = initialpossibleUses;
				boolean changed = false;
				try
				{
					api.beginUndoGroup(player);
					for (int i = (int) box.minX; i <= box.maxX; i++)
					{
						for (int j = (int) box.minY; j <= box.maxY; j++)
						{
							for (int k = (int) box.minZ; k <= box.maxZ; k++)
							{
								if (possibleUses > 0)
									possibleUses = sculptBlock(api, player, world, new BlockPos(i, j, k), shape, bitTypes, possibleUses, setBit);
							}
						}
					}
				}
				finally
				{
					api.endUndoGroup(player);
					if (!world.isRemote && !Configs.dropBitsPerBlock)
						BitInventoryHelper.giveOrDropStacks(player, world, pos, shape, api, bitTypes);
					
					int change = initialpossibleUses - possibleUses;
					int newRemainingUses = remainingUses - (((ConfigProperty) Configs.itemPropertyMap.get(this)).takesDamage ? change : 0);
					if (!world.isRemote && !creativeMode)
					{
						nbt.setInteger(NBTKeys.REMAINING_USES, newRemainingUses);
						if (!removeBits)
							BitInventoryHelper.removeOrAddInventoryBits(api, player, setBitStack, change, false);
						
						if (newRemainingUses <= 0)
						{
							player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
							ForgeEventFactory.onPlayerDestroyItem(player, stack, player.getActiveHand());
						}
						player.inventoryContainer.detectAndSendChanges();
					}
					if (!creativeMode && newRemainingUses <= 0)
						player.renderBrokenItemStack(stack);
					
					changed = possibleUses < initialpossibleUses;
					if (changed)
						playPlacementSound(player, world, pos, 8.0F);
				}
				return changed;
			}
		}
		return false;
	}
	
	public static void playPlacementSound(EntityPlayer player, World world, BlockPos pos, float volumeReduction)
	{
		@SuppressWarnings("deprecation")
		SoundType sound = Blocks.STONE.getSoundType();
		world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume()) / volumeReduction, sound.getPitch() * 0.8F);
	}
	
	private float addPadding(float value, float padding)
	{
		return (value + padding) * Utility.PIXEL_F;
	}
	
	private float addPaddingToMin(float value1, float value2, float padding)
	{
		return Math.min(value1, value2) - padding * Utility.PIXEL_F;
	}
	
	private float addPaddingToMax(float value1, float value2, float padding)
	{
		return Math.max(value1, value2) + padding * Utility.PIXEL_F;
	}
	
	public static boolean wasInsideClicked(EnumFacing dir, Vec3d hit, BlockPos pos)
	{
		if (hit != null)
		{
			switch (dir.ordinal())
			{
				case 0:	return hit.yCoord > pos.getY();
				case 1:	return hit.yCoord < pos.getY() + 1;
				case 2:	return hit.zCoord > pos.getZ();
				case 3:	return hit.zCoord < pos.getZ() + 1;
				case 4:	return hit.xCoord > pos.getX();
				case 5:	return hit.xCoord < pos.getX() + 1;
			}
		}
		return false;
	}
	
	@SuppressWarnings("null")
	private int sculptBlock(IChiselAndBitsAPI api, EntityPlayer player, World world, BlockPos pos, Shape shape,
			Map<IBlockState, Integer> bitTypes, int remainingUses, IBitBrush setBit)
	{
		if (isValidBlock(api, world, pos))
		{
			IBitAccess bitAccess;
			try
			{
				bitAccess = api.getBitAccess(world, pos);
			}
			catch (CannotBeChiseled e)
			{
				return remainingUses;
			}
			boolean byPassBitChecks = shape.isBlockInsideShape(pos);
			int initialRemainingUses = remainingUses;
			for (int i = 0; i < 16; i++)
			{
				for (int j = 0; j < 16; j++)
				{
					for (int k = 0; k < 16; k++)
					{
						IBitBrush bit = bitAccess.getBitAt(i, j, k);
						if ((removeBits ? (!bit.isAir() && !(setBit != null && !setBit.isAir() && !setBit.getState().equals(bit.getState()))) : bit.isAir())
								&& (byPassBitChecks || shape.isPointInsideShape(pos, i, j, k)))
						{
							if (bitTypes != null)
							{
								IBlockState state = bit.getState();
								if (!bitTypes.containsKey(state))
								{
									bitTypes.put(state, 1);
								}
								else
								{
									bitTypes.put(state, bitTypes.get(state) + 1);
								}
							}
							try
							{
								bitAccess.setBitAt(i, j, k, removeBits ? null : setBit);
								remainingUses--;
							}
							catch (SpaceOccupied e) {}
							if (remainingUses == 0)
							{
								bitAccess.commitChanges(true);
								return remainingUses;
							}
						}
					}
				}
			}
			if (!world.isRemote && Configs.dropBitsPerBlock)
				BitInventoryHelper.giveOrDropStacks(player, world, pos, shape, api, bitTypes);
			
			if (remainingUses < initialRemainingUses)
				bitAccess.commitChanges(true);
		}
		return remainingUses;
	}
	
	private boolean isValidBlock(IChiselAndBitsAPI api, World world, BlockPos pos)
	{
		return api.canBeChiseled(world, pos) && (!removeBits || !world.isAirBlock(pos));
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
	{
		boolean shiftDown = GuiScreen.isShiftKeyDown();
		boolean ctrlDown = GuiScreen.isCtrlKeyDown();
		addColorInformation(tooltip, shiftDown);
		NBTTagCompound nbt = stack.getTagCompound();
		int mode = BitToolSettingsHelper.getSculptMode(nbt);
		if (shiftDown)
			tooltip.add(colorSettingText(BitToolSettingsHelper.getSculptModeText(mode), Configs.sculptMode));
		
		ItemStack setBitStack = BitToolSettingsHelper.getBitStack(nbt, removeBits);
		if (!ctrlDown || shiftDown)
		{
			String bitType = "Bit Type To " + (removeBits ? "Remove" : "Add") + ": ";
			String unspecifiedBit = removeBits ? "any" : "none";
			if (!setBitStack.isEmpty())
			{
				String stackName = setBitStack.getDisplayName();
				bitType += (stackName.length() == 12 ? unspecifiedBit : stackName.substring(15));
			}
			else
			{
				bitType += unspecifiedBit;
			}
			tooltip.add(colorSettingText(bitType, removeBits ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade));
		}
		if (shiftDown)
		{
			int shapeType = BitToolSettingsHelper.getShapeType(nbt, curved);
			tooltip.add(colorSettingText(BitToolSettingsHelper.getDirectionText(nbt, shapeType == 4 || shapeType == 5), Configs.sculptDirection));
			tooltip.add(colorSettingText(BitToolSettingsHelper.getShapeTypeText(shapeType),
					removeBits ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat));
			boolean targetBits = BitToolSettingsHelper.isBitGridTargeted(nbt);
			tooltip.add(colorSettingText(BitToolSettingsHelper.getBitGridTargetedText(targetBits), Configs.sculptTargetBitGridVertexes)
					+ (targetBits ? " (corners)" : " (centers)"));
			tooltip.add(colorSettingText(BitToolSettingsHelper.getSemiDiameterText(nbt), Configs.sculptSemiDiameter));
			if (!removeBits)
				tooltip.add(colorSettingText(BitToolSettingsHelper.getOffsetShapeText(nbt), Configs.sculptOffsetShape));
			
			tooltip.add(colorSettingText(BitToolSettingsHelper.getHollowShapeText(nbt, this),
					removeBits ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade));
			tooltip.add(colorSettingText("  - " + BitToolSettingsHelper.getOpenEndsText(nbt), Configs.sculptOpenEnds));
			tooltip.add(colorSettingText("  - " + BitToolSettingsHelper.getWallThicknessText(nbt), Configs.sculptWallThickness));
		}
		else
		{
			if (ctrlDown)
			{
				String shiftText = getColoredKeyBindText(KeyBindingsExtraBitManipulation.SHIFT);
				String removeAddText = removeBits ? "remove" : "add";
				String toFromText = removeBits ? "from" : "to";
				if (mode == 2)
				{
					tooltip.add("Left click point on block, drag");
					tooltip.add("    to another point, then");
					tooltip.add("    release to " + removeAddText + " bits " + toFromText);
					tooltip.add("    all intersecting blocks.");
				}
				else
				{
					String shapeControlText = "Left click block to " + removeAddText + " bits";
					if (mode == 0)
						shapeControlText += ".";
					
					tooltip.add(shapeControlText);
					if (mode != 0)
					{
						String areaText = toFromText;
						tooltip.add("    " + areaText + " all intersecting blocks.");
					}
				}
				tooltip.add("Right click to cycle modes.");
				if (!removeBits)
					tooltip.add(shiftText + " left click bit to set bit type.");
				
				tooltip.add(shiftText + " mouse wheel to change");
				tooltip.add("    " + (removeBits ? "removal" : "addition") + (Configs.displayNameDiameter ? " " : " semi-") + "diameter.");
				tooltip.add("");
				String controlText = getColoredKeyBindText(KeyBindingsExtraBitManipulation.CONTROL);
				tooltip.add(controlText + " right click to");
				tooltip.add("    change shape.");
				tooltip.add(controlText + " left click to toggle");
				tooltip.add("    target between");
				tooltip.add("    bits & vertecies.");
				tooltip.add(controlText + " mouse wheel to");
				tooltip.add("    change direction.");
				tooltip.add("");
				String altText = getColoredKeyBindText(KeyBindingsExtraBitManipulation.ALT);
				tooltip.add(altText + " right click to toggle");
				tooltip.add("    shapes solid or hollow.");
				tooltip.add(altText + " left click to toggle hollow");
				tooltip.add("    shapes open or closed.");
				tooltip.add(altText + " mouse wheel to change hollow");
				tooltip.add("    shape wall thickness.");
				addKeybindReminders(tooltip, KeyBindingsExtraBitManipulation.SHIFT,
						KeyBindingsExtraBitManipulation.CONTROL, KeyBindingsExtraBitManipulation.ALT);
			}
			else
			{
				addKeyInformation(tooltip, true);
			}
		}
	}
	
}